package com.mtons.mblog.modules.service.impl;

import com.mtons.mblog.base.lang.Consts;
import com.mtons.mblog.base.utils.*;
import com.mtons.mblog.modules.aspect.PostStatusFilter;
import com.mtons.mblog.modules.data.PostVO;
import com.mtons.mblog.modules.data.UserVO;
import com.mtons.mblog.modules.entity.*;
import com.mtons.mblog.modules.repository.ResourceRepository;
import com.mtons.mblog.modules.repository.PostAttributeRepository;
import com.mtons.mblog.modules.repository.PostResourceRepository;
import com.mtons.mblog.modules.repository.PostRepository;
import com.mtons.mblog.modules.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.persistence.criteria.Predicate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 文章管理 Service 实现
 * <p>
 * 关键依赖：
 * <ul>
 *   <li>{@link PostRepository}：文章主表 JPA 仓储</li>
 *   <li>{@link PostResourceRepository}：文章-资源关联仓储（图片引用计数）</li>
 *   <li>{@link ResourceRepository}：资源仓储（图片 md5 与引用计数）</li>
 *   <li>{@link UserService}：回填作者信息</li>
 *   <li>{@link ChannelService}：回填栏目信息</li>
 *   <li>{@link TagService}：标签关联维护</li>
 * </ul>
 * </p>
 * <p>
 * 事务策略：类级别 {@code @Transactional} 可写事务；
 * 写操作方法使用 {@code rollbackFor = Throwable.class} 全异常回滚，
 * 因为涉及文章、属性、资源关联、标签关联多表联动，必须保证原子性。
 * </p>
 * <p>
 * 并发控制：使用 {@link ResourceLock} 基于文章 ID 的细粒度锁（AtomicInteger），
 * 防止同文章并发写入导致资源引用计数错乱。
 * </p>
 */
@Service
@Transactional
public class PostServiceImpl implements PostService {
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private PostAttributeRepository postAttributeRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private ChannelService channelService;
	@Autowired
	private TagService tagService;
	@Autowired
	private PostResourceRepository postResourceRepository;
	@Autowired
	private ResourceRepository resourceRepository;

    // 提取文章正文中图片签名（md5）的正则，用于资源引用计数维护
    private static Pattern pattern = Pattern.compile("(?<=/_signature/)(.+?)(?=\\.)");

	/**
	 * <p>动态拼接 channelId 与排除栏目条件，{@link PostStatusFilter} 切面负责过滤不展示状态的文章。</p>
	 */
	@Override
	@PostStatusFilter
	public Page<PostVO> paging(Pageable pageable, int channelId, Set<Integer> excludeChannelIds) {
		Page<Post> page = postRepository.findAll((root, query, builder) -> {
			Predicate predicate = builder.conjunction();

			if (channelId > Consts.ZERO) {
				predicate.getExpressions().add(
						builder.equal(root.get("channelId").as(Integer.class), channelId));
			}

			if (null != excludeChannelIds && !excludeChannelIds.isEmpty()) {
				// 排除私密/关闭栏目，保证前台不展示
				predicate.getExpressions().add(
						builder.not(root.get("channelId").in(excludeChannelIds)));
			}

			return predicate;
		}, pageable);

		return new PageImpl<>(toPosts(page.getContent()), pageable, page.getTotalElements());
	}

	@Override
	public Page<PostVO> paging4Admin(Pageable pageable, int channelId, String title) {
		Page<Post> page = postRepository.findAll((root, query, builder) -> {
            Predicate predicate = builder.conjunction();
			if (channelId > Consts.ZERO) {
				predicate.getExpressions().add(
						builder.equal(root.get("channelId").as(Integer.class), channelId));
			}
			if (StringUtils.isNotBlank(title)) {
				predicate.getExpressions().add(
						builder.like(root.get("title").as(String.class), "%" + title + "%"));
			}
            return predicate;
        }, pageable);

		return new PageImpl<>(toPosts(page.getContent()), pageable, page.getTotalElements());
	}

	@Override
	@PostStatusFilter
	public Page<PostVO> pagingByAuthorId(Pageable pageable, long userId) {
		Page<Post> page = postRepository.findAllByAuthorId(pageable, userId);
		return new PageImpl<>(toPosts(page.getContent()), pageable, page.getTotalElements());
	}

	@Override
	@PostStatusFilter
	public List<PostVO> findLatestPosts(int maxResults) {
		return find("created", maxResults).stream().map(BeanMapUtils::copy).collect(Collectors.toList());
	}

	@Override
	@PostStatusFilter
	public List<PostVO> findHottestPosts(int maxResults) {
		return find("views", maxResults).stream().map(BeanMapUtils::copy).collect(Collectors.toList());
	}

	@Override
	@PostStatusFilter
	public Map<Long, PostVO> findMapByIds(Set<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return Collections.emptyMap();
		}

		List<Post> list = postRepository.findAllById(ids);
		Map<Long, PostVO> rets = new HashMap<>();

		HashSet<Long> uids = new HashSet<>();

		list.forEach(po -> {
			rets.put(po.getId(), BeanMapUtils.copy(po));
			uids.add(po.getAuthorId());
		});

		// 批量回填作者信息
		buildUsers(rets.values(), uids);
		return rets;
	}

	/**
	 * 发布文章
	 * <p>事务回滚：文章/属性/标签关联/资源引用计数任一失败整体回滚。</p>
	 * <p>核心步骤：
	 * <ol>
	 *   <li>写入文章主表</li>
	 *   <li>同步标签关联（{@link TagService#batchUpdate}）</li>
	 *   <li>统计正文图片引用并维护资源引用计数</li>
	 * </ol>
	 * </p>
	 */
	@Override
    @Transactional(rollbackFor = Throwable.class)
	public long post(PostVO post) {
		Post po = new Post();

		BeanUtils.copyProperties(post, po);

		po.setCreated(new Date());
		po.setStatus(post.getStatus());

		// 摘要为空时根据编辑器类型自动生成，避免正文过长导致列表渲染性能问题
		if (StringUtils.isBlank(post.getSummary())) {
			po.setSummary(trimSummary(post.getEditor(), post.getContent()));
		} else {
			po.setSummary(post.getSummary());
		}

		postRepository.save(po);
		tagService.batchUpdate(po.getTags(), po.getId());

		// 文章 ID 维度的细粒度锁，防止并发写入导致资源引用计数错乱
        String key = ResourceLock.getPostKey(po.getId());
        AtomicInteger lock = ResourceLock.getAtomicInteger(key);
        try {
            synchronized (lock){
                PostAttribute attr = new PostAttribute();
                attr.setContent(post.getContent());
                attr.setEditor(post.getEditor());
                attr.setId(po.getId());
                postAttributeRepository.save(attr);

                // 统计正文中新增图片并维护资源引用计数
                countResource(po.getId(), null,  attr.getContent());
                return po.getId();
            }
        }finally {
            ResourceLock.giveUpAtomicInteger(key);
        }
	}

	@Override
	public PostVO get(long id) {
		Optional<Post> po = postRepository.findById(id);
		if (po.isPresent()) {
			PostVO d = BeanMapUtils.copy(po.get());

			d.setAuthor(userService.get(d.getAuthorId()));
			d.setChannel(channelService.getById(d.getChannelId()));

			PostAttribute attr = postAttributeRepository.findById(d.getId()).get();
			d.setContent(attr.getContent());
			d.setEditor(attr.getEditor());
			return d;
		}
		return null;
	}

	/**
	 * 更新文章
	 * <p>事务回滚：文章主表/属性/标签关联/资源引用计数任一失败整体回滚。</p>
	 * <p>资源计数维护：先读取旧正文中的图片引用，与新正文对比，计算增量 add/delete 列表后批量更新。</p>
	 */
	@Override
    @Transactional(rollbackFor = Throwable.class)
	public void update(PostVO p){
		Optional<Post> optional = postRepository.findById(p.getId());

		if (optional.isPresent()) {
            String key = ResourceLock.getPostKey(p.getId());
            AtomicInteger lock = ResourceLock.getAtomicInteger(key);
            try {
                synchronized (lock){
                    Post po = optional.get();
                    po.setTitle(p.getTitle());//标题
                    po.setChannelId(p.getChannelId());
                    po.setThumbnail(p.getThumbnail());
                    po.setStatus(p.getStatus());

                    // 摘要为空时根据编辑器类型自动生成
                    if (StringUtils.isBlank(p.getSummary())) {
                        po.setSummary(trimSummary(p.getEditor(), p.getContent()));
                    } else {
                        po.setSummary(p.getSummary());
                    }

                    po.setTags(p.getTags());//标签

                    // 读取旧正文内容，用于后续比对图片引用增量
                    Optional<PostAttribute> attributeOptional = postAttributeRepository.findById(po.getId());
                    String originContent = "";
                    if (attributeOptional.isPresent()){
                        originContent = attributeOptional.get().getContent();
                    }
                    PostAttribute attr = new PostAttribute();
                    attr.setContent(p.getContent());
                    attr.setEditor(p.getEditor());
                    attr.setId(po.getId());
                    postAttributeRepository.save(attr);

                    tagService.batchUpdate(po.getTags(), po.getId());

                    // 增量维护资源引用计数：新增图片 +1，删除图片 -1
                    countResource(po.getId(), originContent, p.getContent());
                }
            }finally {
                ResourceLock.giveUpAtomicInteger(key);
            }
		}
	}

	/**
	 * 删除单条文章（带作者身份校验）
	 * <p>级联清理：文章主体 + 文章属性 + 文章资源关联（含资源计数 -1）；
	 * 广播文章删除事件触发用户文章数减少与索引删除。</p>
	 */
	@Override
    @Transactional(rollbackFor = Throwable.class)
	public void delete(long id, long authorId) {
		Post po = postRepository.findById(id).get();
		// 校验操作者与文章作者一致，防止越权删除他人文章
		Assert.isTrue(po.getAuthorId() == authorId, "无权限操作");

        String key = ResourceLock.getPostKey(po.getId());
        AtomicInteger lock = ResourceLock.getAtomicInteger(key);
		try	{
			synchronized (lock){
				postRepository.deleteById(id);
				postAttributeRepository.deleteById(id);
				// 级联清理资源引用计数，避免资源成为孤儿引用
				cleanResource(po.getId());
			}
		}finally {
			ResourceLock.giveUpAtomicInteger(key);
		}
	}

	/**
	 * 批量删除文章
	 * <p>逐条加锁删除，每条文章的级联清理独立互不影响。</p>
	 */
	@Override
    @Transactional(rollbackFor = Throwable.class)
	public void delete(Collection<Long> ids) {
		if (CollectionUtils.isNotEmpty(ids)) {
			List<Post> list = postRepository.findAllById(ids);
			list.forEach(po -> {
				String key = ResourceLock.getPostKey(po.getId());
				AtomicInteger lock = ResourceLock.getAtomicInteger(key);
				try	{
					synchronized (lock){
						postRepository.delete(po);
						postAttributeRepository.deleteById(po.getId());
						cleanResource(po.getId());
					}
				}finally {
					ResourceLock.giveUpAtomicInteger(key);
				}
			});
		}
	}

	@Override
    @Transactional(rollbackFor = Throwable.class)
	public void identityViews(long id) {
		postRepository.updateViews(id, Consts.IDENTITY_STEP);
	}

	/**
	 * 累加文章评论数
	 */
	@Override
	@Transactional
	public void identityComments(long id) {
		postRepository.updateComments(id, Consts.IDENTITY_STEP);
	}

	@Override
	@PostStatusFilter
	public long count() {
		return postRepository.count();
	}

	/**
	 * <p>统一排除已关闭栏目（{@link Consts#STATUS_CLOSED}）的文章；
	 */
	@PostStatusFilter
	private List<Post> find(String orderBy, int size) {
		Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, orderBy));

		Set<Integer> excludeChannelIds = new HashSet<>();

		List<Channel> channels = channelService.findAll(Consts.STATUS_CLOSED);
		if (channels != null) {
			channels.forEach((c) -> excludeChannelIds.add(c.getId()));
		}

		Page<Post> page = postRepository.findAll((root, query, builder) -> {
			Predicate predicate = builder.conjunction();
			if (excludeChannelIds.size() > 0) {
				predicate.getExpressions().add(
						builder.not(root.get("channelId").in(excludeChannelIds)));
			}
			return predicate;
		}, pageable);
		return page.getContent();
	}

	/**
	 * 生成文章摘要
	 * <p>根据编辑器类型选择处理路径：Markdown 先渲染为 HTML 再提取纯文本；
	 * 截取长度 126 字符避免列表渲染过长。</p>
	 */
	private String trimSummary(String editor, final String text){
		if (Consts.EDITOR_MARKDOWN.endsWith(editor)) {
			return PreviewTextUtils.getText(MarkdownUtils.renderMarkdown(text), 126);
		} else {
			return PreviewTextUtils.getText(text, 126);
		}
	}

	/**
	 * 将文章实体列表转换为 VO 列表，并批量回填作者与栏目信息
	 */
	private List<PostVO> toPosts(List<Post> posts) {
		HashSet<Long> uids = new HashSet<>();
		HashSet<Integer> groupIds = new HashSet<>();

		List<PostVO> rets = posts
				.stream()
				.map(po -> {
					uids.add(po.getAuthorId());
					groupIds.add(po.getChannelId());
					return BeanMapUtils.copy(po);
				})
				.collect(Collectors.toList());

		// 批量回填作者与栏目信息
		buildUsers(rets, uids);
		buildGroups(rets, groupIds);

		return rets;
	}

	private void buildUsers(Collection<PostVO> posts, Set<Long> uids) {
		Map<Long, UserVO> userMap = userService.findMapByIds(uids);
		posts.forEach(p -> p.setAuthor(userMap.get(p.getAuthorId())));
	}

	private void buildGroups(Collection<PostVO> posts, Set<Integer> groupIds) {
		Map<Integer, Channel> map = channelService.findMapByIds(groupIds);
		posts.forEach(p -> p.setChannel(map.get(p.getChannelId())));
	}

	/**
	 * 维护文章正文中的图片资源引用计数
	 * <p>对比新旧正文的图片引用 md5 集合，计算 add 与 delete 列表：
	 * <ul>
	 *   <li>新增图片：建立 PostResource 关联并资源引用计数 +1</li>
	 *   <li>删除图片：移除 PostResource 关联并资源引用计数 -1</li>
	 * </ul>
	 * </p>
	 */
	private void countResource(Long postId, String originContent, String newContent){
	    if (StringUtils.isEmpty(originContent)){
	        originContent = "";
        }
        if (StringUtils.isEmpty(newContent)){
	        newContent = "";
        }

		Set<String> exists = extractImageMd5(originContent);
		Set<String> news = extractImageMd5(newContent);

		// 计算新增与删除的图片 md5 列表
        List<String> adds = ListUtils.removeAll(news, exists);
		List<String> deleteds = ListUtils.removeAll(exists, news);

		if (adds.size() > 0) {
			List<Resource> resources = resourceRepository.findByMd5In(adds);

			List<PostResource> prs = resources.stream().map(n -> {
				PostResource pr = new PostResource();
				pr.setResourceId(n.getId());
				pr.setPostId(postId);
				pr.setPath(n.getPath());
				return pr;
			}).collect(Collectors.toList());
			postResourceRepository.saveAll(prs);

			// 资源引用计数 +1
			resourceRepository.updateAmount(adds, 1);
		}

		if (deleteds.size() > 0) {
			List<Resource> resources = resourceRepository.findByMd5In(deleteds);
			List<Long> rids = resources.stream().map(Resource::getId).collect(Collectors.toList());
			postResourceRepository.deleteByPostIdAndResourceIdIn(postId, rids);
			// 资源引用计数 -1
			resourceRepository.updateAmount(deleteds, -1);
		}
	}

	/**
	 * 文章删除时清理资源引用
	 * <p>将该文章关联的所有资源引用计数 -1，并删除全部 PostResource 关联记录。</p>
	 */
	private void cleanResource(long postId) {
		List<PostResource> list = postResourceRepository.findByPostId(postId);
		if (null == list || list.isEmpty()) {
			return;
		}
		List<Long> rids = list.stream().map(PostResource::getResourceId).collect(Collectors.toList());
		resourceRepository.updateAmountByIds(rids, -1);
		postResourceRepository.deleteByPostId(postId);
	}

	/**
	 * 从正文中提取所有图片的签名（md5）
	 * <p>使用预编译正则匹配 /_signature/ 后的 md5 片段，用于资源引用计数。</p>
	 */
	private Set<String> extractImageMd5(String text) {
//		Pattern pattern = Pattern.compile("(?<=/_signature/)[^/]+?jpg");

		Set<String> md5s = new HashSet<>();

		Matcher originMatcher = pattern.matcher(text);
		while (originMatcher.find()) {
			String key = originMatcher.group();
//			md5s.add(key.substring(0, key.lastIndexOf(".")));
			md5s.add(key);
		}

		return md5s;
	}
}
