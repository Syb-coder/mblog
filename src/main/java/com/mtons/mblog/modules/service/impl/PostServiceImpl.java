// 包声明：文章服务实现类所在包
package com.mtons.mblog.modules.service.impl;

// 导入常量定义类
import com.mtons.mblog.base.lang.Consts;
// 导入工具类包（BeanMapUtils、MarkdownUtils、PreviewTextUtils、ResourceLock 等）
import com.mtons.mblog.base.utils.*;
// 导入文章状态过滤切面注解
import com.mtons.mblog.modules.aspect.PostStatusFilter;
// 导入文章 VO
import com.mtons.mblog.modules.data.PostVO;
// 导入用户 VO
import com.mtons.mblog.modules.data.UserVO;
// 导入实体类（Post、PostAttribute、PostResource、Resource 等）
import com.mtons.mblog.modules.entity.*;
// 导入仓储接口（ResourceRepository、PostAttributeRepository、PostResourceRepository、PostRepository）
import com.mtons.mblog.modules.repository.ResourceRepository;
import com.mtons.mblog.modules.repository.PostAttributeRepository;
import com.mtons.mblog.modules.repository.PostResourceRepository;
import com.mtons.mblog.modules.repository.PostRepository;
// 导入服务接口（ChannelService、TagService、PostService、UserService 等）
import com.mtons.mblog.modules.service.*;
// 导入 Apache Commons 集合和列表工具类
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
// 导入 Apache Commons 字符串工具类
import org.apache.commons.lang3.StringUtils;
// 导入 SLF4J 日志门面
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// 导入 Spring Bean 属性拷贝和自动注入
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
// 导入 Spring Data 分页相关类
import org.springframework.data.domain.*;
// 导入 Spring Service 和事务注解
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// 导入 Spring 断言工具
import org.springframework.util.Assert;

// 导入 JPA Criteria 断言接口
import jakarta.persistence.criteria.Predicate;
// 导入 Java 工具类包
import java.util.*;
// 导入 AtomicInteger 原子整数类，用于并发控制
import java.util.concurrent.atomic.AtomicInteger;
// 导入正则表达式类
import java.util.regex.Matcher;
import java.util.regex.Pattern;
// 导入 Stream 收集器
import java.util.stream.Collectors;

/**
 * 文章管理 Service 实现 —— 整个系统中最核心的业务类
 *
 * <h3>职责</h3>
 * 提供文章 CRUD、分页查询、资源引用计数维护、标签同步等核心功能。
 *
 * <h3>涉及的表</h3>
 * 每次发布/更新文章时，会联动操作 4 张表：
 * <ol>
 *   <li><b>mto_post</b> — 文章主表（标题、摘要、标签字符串等）</li>
 *   <li><b>mto_post_attribute</b> — 文章正文（正文内容单独拆表，避免列表查询加载大字段）</li>
 *   <li><b>mto_post_tag</b> — 文章-标签关联（通过 TagService.batchUpdate 维护）</li>
 *   <li><b>mto_post_resource / mto_resource</b> — 资源引用计数
 *       （通过解析正文中的 &lt;img&gt; 标签自动维护）</li>
 * </ol>
 *
 * <h3>事务策略</h3>
 * 类级别 @Transactional（可写事务），所有写操作加上
 * rollbackFor = Throwable.class 确保任意异常都回滚。
 * 因为跨表操作，必须保证原子性。
 *
 * <h3>并发控制</h3>
 * 使用 ResourceLock（基于 AtomicInteger + synchronized 的细粒度锁）
 * 防止同文章并发写入导致资源引用计数错乱。
 * 锁的粒度是"文章 ID"，不同文章之间不互斥，并发性能好。
 *
 * <h3>关键方法执行流程</h3>
 * <pre>
 * 发布文章 post():
 *   1) 保存 Post 主表
 *   2) tagService.batchUpdate() → 同步标签关联
 *   3) 获取文章 ID 的细粒度锁
 *   4) 保存 PostAttribute（正文）
 *   5) countResource() → 解析正文图片 md5，维护资源引用计数
 *   6) 释放锁
 *
 * 更新文章 update():
 *   1) 读取旧正文 → 提取旧图片 md5 列表
 *   2) 更新 Post 主表
 *   3) 保存新正文
 *   4) tagService.batchUpdate() → 同步标签
 *   5) countResource(旧正文, 新正文) → 计算差值，增量更新资源计数
 *
 * 删除文章 delete():
 *   1) postRepository.deleteById()
 *   2) postAttributeRepository.deleteById()
 *   3) cleanResource() → 删除 PostResource 关联，资源计数 -1
 * </pre>
 */
// @Service 注解：将该类注册为 Spring 业务服务组件
@Service
// @Transactional：类级别可写事务
@Transactional
// PostServiceImpl：文章管理服务实现类——整个系统中最核心的业务类
public class PostServiceImpl implements PostService {
	// @Autowired：Spring 自动注入文章仓储实例
	@Autowired
	// postRepository：文章主表 JPA 仓储
	private PostRepository postRepository;
	// @Autowired：Spring 自动注入文章属性仓储实例
	@Autowired
	// postAttributeRepository：文章正文仓储（正文单独拆表存储）
	private PostAttributeRepository postAttributeRepository;
	// @Autowired：Spring 自动注入用户服务实例
	@Autowired
	// userService：用户服务，用于补全作者信息
	private UserService userService;
	// @Autowired：Spring 自动注入频道服务实例
	@Autowired
	// channelService：频道服务，用于补全栏目信息
	private ChannelService channelService;
	// @Autowired：Spring 自动注入标签服务实例
	@Autowired
	// tagService：标签服务，用于同步文章标签关联
	private TagService tagService;
	// @Autowired：Spring 自动注入文章资源关联仓储实例
	@Autowired
	// postResourceRepository：文章-资源关联仓储
	private PostResourceRepository postResourceRepository;
	// @Autowired：Spring 自动注入资源仓储实例
	@Autowired
	// resourceRepository：资源仓储，提供资源的引用计数更新
	private ResourceRepository resourceRepository;

    // pattern：预编译正则表达式，用于从正文中提取图片签名（md5），格式如 /_signature/xxxxx.jpg
    private static Pattern pattern = Pattern.compile("(?<=/_signature/)(.+?)(?=\\.)");

    // 日志记录器
    private static final Logger log = LoggerFactory.getLogger(PostServiceImpl.class);

	/**
	 * <p>动态拼接 channelId 与排除栏目条件，{@link PostStatusFilter} 切面负责过滤不展示状态的文章。</p>
	 */
	// @Override：实现接口方法
	@Override
	// @PostStatusFilter：AOP 切面，过滤掉草稿、删除等状态的文章
	@PostStatusFilter
	// paging：前台分页查询文章，支持按栏目过滤和排除指定栏目集合
	public Page<PostVO> paging(Pageable pageable, int channelId, Set<Integer> excludeChannelIds) {
        // 调试日志：打印分页查询参数
        log.info("PostServiceImpl.paging —— channelId = {}, excludeChannelIds = {}", channelId, excludeChannelIds);

        // 使用 JPA Criteria 动态构建查询条件
        Page<Post> page = postRepository.findAll((root, query, builder) -> {
			// 创建空 conjunction 条件
			Predicate predicate = builder.conjunction();

			// 如果指定了栏目 ID
			if (channelId > Consts.ZERO) {
				// 添加栏目等值匹配条件
				predicate.getExpressions().add(
						builder.equal(root.get("channelId").as(Integer.class), channelId));
			}

			// 如果指定了排除的栏目集合
			if (null != excludeChannelIds && !excludeChannelIds.isEmpty()) {
				// 排除私密/关闭栏目，保证前台不展示
				predicate.getExpressions().add(
						builder.not(root.get("channelId").in(excludeChannelIds)));
			}

			// 返回查询条件
			return predicate;
		}, pageable);

		// 将实体列表转为 VO 列表并返回分页结果
		return new PageImpl<>(toPosts(page.getContent()), pageable, page.getTotalElements());
	}

	// @Override：实现接口方法
	@Override
	// paging4Admin：后台管理分页查询文章，支持按栏目过滤和标题模糊搜索
	public Page<PostVO> paging4Admin(Pageable pageable, int channelId, String title) {
		// 使用 JPA Criteria 动态构建查询条件
		Page<Post> page = postRepository.findAll((root, query, builder) -> {
            // 创建空 conjunction 条件
            Predicate predicate = builder.conjunction();
			// 如果指定了栏目 ID
			if (channelId > Consts.ZERO) {
				// 添加栏目等值匹配条件
				predicate.getExpressions().add(
						builder.equal(root.get("channelId").as(Integer.class), channelId));
			}
			// 如果指定了搜索标题
			if (StringUtils.isNotBlank(title)) {
				// 添加标题模糊匹配条件
				predicate.getExpressions().add(
						builder.like(root.get("title").as(String.class), "%" + title + "%"));
			}
            // 返回查询条件
            return predicate;
        }, pageable);

		// 将实体列表转为 VO 列表并返回分页结果
		return new PageImpl<>(toPosts(page.getContent()), pageable, page.getTotalElements());
	}

	// @Override：实现接口方法
	@Override
	// @PostStatusFilter：AOP 切面，过滤不展示状态的文章
	@PostStatusFilter
	// pagingByAuthorId：按作者 ID 分页查询其发布的文章
	public Page<PostVO> pagingByAuthorId(Pageable pageable, long userId) {
		// 根据作者 ID 分页查询文章实体
		Page<Post> page = postRepository.findAllByAuthorId(pageable, userId);
		// 将实体列表转为 VO 并返回分页结果
		return new PageImpl<>(toPosts(page.getContent()), pageable, page.getTotalElements());
	}

	// @Override：实现接口方法
	@Override
	// @PostStatusFilter：AOP 切面，过滤不展示状态的文章
	@PostStatusFilter
	// findLatestPosts：查询最新的 N 篇文章，按创建时间排序
	public List<PostVO> findLatestPosts(int maxResults) {
		// 调用内部方法 find，按 created 字段排序取 maxResults 条
		return find("created", maxResults).stream().map(BeanMapUtils::copy).collect(Collectors.toList());
	}

	// @Override：实现接口方法
	@Override
	// @PostStatusFilter：AOP 切面，过滤不展示状态的文章
	@PostStatusFilter
	// findHottestPosts：按浏览量排序查询热门文章
	public List<PostVO> findHottestPosts(int maxResults) {
		// 调用内部方法 find，按 views 字段排序取 maxResults 条
		return find("views", maxResults).stream().map(BeanMapUtils::copy).collect(Collectors.toList());
	}

	// @Override：实现接口方法
	@Override
	// @PostStatusFilter：AOP 切面，过滤不展示状态的文章
	@PostStatusFilter
	// findMapByIds：根据 ID 集合批量查询文章，返回以文章 ID 为 key 的 Map
	public Map<Long, PostVO> findMapByIds(Set<Long> ids) {
		// 如果 ID 集合为空或 null，直接返回空 Map
		if (ids == null || ids.isEmpty()) {
			return Collections.emptyMap();
		}

		// 根据 ID 集合查询文章列表
		List<Post> list = postRepository.findAllById(ids);
		// 创建返回结果 Map
		Map<Long, PostVO> rets = new HashMap<>();

		// 收集所有文章的作者 ID，用于批量查询
		HashSet<Long> uids = new HashSet<>();

		// 遍历文章列表，转为 VO 并收集作者 ID
		list.forEach(po -> {
			// 将文章实体拷贝为 VO 并存入 Map
			rets.put(po.getId(), BeanMapUtils.copy(po));
			// 收集作者 ID
			uids.add(po.getAuthorId());
		});

		// 批量回填作者信息到所有文章 VO 中
		buildUsers(rets.values(), uids);
		// 返回文章映射
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
	// @Override：实现接口方法
	@Override
    // @Transactional(rollbackFor = Throwable.class)：可写事务，任意异常都回滚
    @Transactional(rollbackFor = Throwable.class)
	// post：发布新文章，返回新文章的 ID
	public long post(PostVO post) {
		// 创建新的文章实体
		Post po = new Post();

		// 将传入的 VO 属性拷贝到实体中
		BeanUtils.copyProperties(post, po);

		// 设置创建时间
		po.setCreated(new Date());
		// 设置最后修改时间
		po.setUpdated(new Date());
		// 设置文章状态
		po.setStatus(post.getStatus());

		// 摘要为空时根据编辑器类型自动生成，避免正文过长导致列表渲染性能问题
		if (StringUtils.isBlank(post.getSummary())) {
			// 根据编辑器类型生成摘要（Markdown 先渲染再提取纯文本）
			po.setSummary(trimSummary(post.getEditor(), post.getContent()));
		} else {
			// 使用手动填写的摘要
			po.setSummary(post.getSummary());
		}

		// 保存文章主表到数据库
		postRepository.save(po);
		// 同步标签关联（新建/复用标签 + 建立 PostTag 关联）
		tagService.batchUpdate(po.getTags(), po.getId());

		// 文章 ID 维度的细粒度锁，防止并发写入导致资源引用计数错乱
        String key = ResourceLock.getPostKey(po.getId());
        // 获取该文章的原子锁对象
        AtomicInteger lock = ResourceLock.getAtomicInteger(key);
        try {
        	// 加 synchronized 同步块，同一时刻只有一个线程能操作该文章的资源引用
            synchronized (lock){
            	// 创建文章正文属性实体
                PostAttribute attr = new PostAttribute();
                // 设置正文内容
                attr.setContent(post.getContent());
                // 设置编辑器类型
                attr.setEditor(post.getEditor());
                // 设置 ID 与文章主表一致
                attr.setId(po.getId());
                // 保存文章正文到数据库
                postAttributeRepository.save(attr);

                // 统计正文中新增图片并维护资源引用计数（无旧正文，全部视为新增）
                countResource(po.getId(), null,  attr.getContent());
                // 返回新文章的 ID
                return po.getId();
            }
        }finally {
        	// finally 块确保锁一定被释放，避免死锁
            ResourceLock.giveUpAtomicInteger(key);
        }
	}

	// @Override：实现接口方法
	@Override
	// get：根据 ID 查询文章详情（含正文内容）
	public PostVO get(long id) {
		// 根据 ID 查询文章主表记录
		Optional<Post> po = postRepository.findById(id);
		// 如果存在
		if (po.isPresent()) {
			// 将实体拷贝为 VO
			PostVO d = BeanMapUtils.copy(po.get());

			// 补全作者信息（通过 userService 单条查询）
			d.setAuthor(userService.get(d.getAuthorId()));
			// 补全栏目信息（通过 channelService 单条查询）
			d.setChannel(channelService.getById(d.getChannelId()));

			// 根据文章 ID 查询正文属性
			PostAttribute attr = postAttributeRepository.findById(d.getId()).get();
			// 设置正文内容到 VO
			d.setContent(attr.getContent());
			// 设置编辑器类型到 VO
			d.setEditor(attr.getEditor());
			// 返回完整的文章 VO
			return d;
		}
		// 文章不存在则返回 null
		return null;
	}

	/**
	 * 更新文章
	 * <p>事务回滚：文章主表/属性/标签关联/资源引用计数任一失败整体回滚。</p>
	 * <p>资源计数维护：先读取旧正文中的图片引用，与新正文对比，计算增量 add/delete 列表后批量更新。</p>
	 */
	// @Override：实现接口方法
	@Override
    // @Transactional(rollbackFor = Throwable.class)：可写事务
    @Transactional(rollbackFor = Throwable.class)
	// update：更新文章信息（标题、正文、栏目、标签等）
	public void update(PostVO p){
		// 根据 ID 查询已有文章记录
		Optional<Post> optional = postRepository.findById(p.getId());

		// 如果文章存在
		if (optional.isPresent()) {
            // 获取该文章的细粒度锁
            String key = ResourceLock.getPostKey(p.getId());
            AtomicInteger lock = ResourceLock.getAtomicInteger(key);
            try {
            	// 加 synchronized 同步块
                synchronized (lock){
                	// 获取已有文章实体
                    Post po = optional.get();
                    // 更新标题
                    po.setTitle(p.getTitle());//标题
                    // 更新所属栏目 ID
                    po.setChannelId(p.getChannelId());
                    // 更新缩略图路径
                    po.setThumbnail(p.getThumbnail());
                    // 更新文章状态
                    po.setStatus(p.getStatus());

                    // 摘要为空时根据编辑器类型自动生成
                    if (StringUtils.isBlank(p.getSummary())) {
                        // 根据编辑器类型生成摘要
                        po.setSummary(trimSummary(p.getEditor(), p.getContent()));
                    } else {
                        // 使用手动填写的摘要
                        po.setSummary(p.getSummary());
                    }

                    // 更新标签字符串
                    po.setTags(p.getTags());//标签

                    // 更新最后修改时间
                    po.setUpdated(new Date());

                    // 读取旧正文内容，用于后续比对图片引用增量
                    Optional<PostAttribute> attributeOptional = postAttributeRepository.findById(po.getId());
                    // 初始化旧正文变量
                    String originContent = "";
                    // 如果旧正文存在
                    if (attributeOptional.isPresent()){
                        // 获取旧正文内容
                        originContent = attributeOptional.get().getContent();
                    }
                    // 创建新的正文属性实体
                    PostAttribute attr = new PostAttribute();
                    // 设置新正文内容
                    attr.setContent(p.getContent());
                    // 设置新编辑器类型
                    attr.setEditor(p.getEditor());
                    // 设置 ID 与文章主表一致
                    attr.setId(po.getId());
                    // 保存新正文到数据库（覆盖旧的）
                    postAttributeRepository.save(attr);

                    // 同步标签关联
                    tagService.batchUpdate(po.getTags(), po.getId());

                    // 增量维护资源引用计数：新增图片 +1，删除图片 -1
                    countResource(po.getId(), originContent, p.getContent());
                }
            }finally {
            	// 确保锁释放
                ResourceLock.giveUpAtomicInteger(key);
            }
		}
	}

	/**
	 * 删除单条文章（带作者身份校验）
	 * <p>级联清理：文章主体 + 文章属性 + 文章资源关联（含资源计数 -1）；
	 * 广播文章删除事件触发用户文章数减少与索引删除。</p>
	 */
	// @Override：实现接口方法
	@Override
    // @Transactional(rollbackFor = Throwable.class)：可写事务
    @Transactional(rollbackFor = Throwable.class)
	// delete：删除单条文章（带作者身份校验）
	public void delete(long id, long authorId) {
		// 根据 ID 查询文章记录
		Post po = postRepository.findById(id).get();
		// 校验操作者与文章作者一致，防止越权删除他人文章
		Assert.isTrue(po.getAuthorId() == authorId, "无权限操作");

        // 获取该文章的细粒度锁
        String key = ResourceLock.getPostKey(po.getId());
        AtomicInteger lock = ResourceLock.getAtomicInteger(key);
		try	{
			// 加 synchronized 同步块
			synchronized (lock){
				// 从数据库删除文章主表记录
				postRepository.deleteById(id);
				// 从数据库删除文章正文记录
				postAttributeRepository.deleteById(id);
				// 级联清理资源引用计数，避免资源成为孤儿引用
				cleanResource(po.getId());
			}
		}finally {
			// 确保锁释放
			ResourceLock.giveUpAtomicInteger(key);
		}
	}

	/**
	 * 批量删除文章
	 * <p>逐条加锁删除，每条文章的级联清理独立互不影响。</p>
	 */
	// @Override：实现接口方法
	@Override
    // @Transactional(rollbackFor = Throwable.class)：可写事务
    @Transactional(rollbackFor = Throwable.class)
	// delete：批量删除文章
	public void delete(Collection<Long> ids) {
		// 如果 ID 集合非空
		if (CollectionUtils.isNotEmpty(ids)) {
			// 根据 ID 集合查询所有待删除文章
			List<Post> list = postRepository.findAllById(ids);
			// 逐条删除每篇文章
			list.forEach(po -> {
				// 获取当前文章的细粒度锁
				String key = ResourceLock.getPostKey(po.getId());
				AtomicInteger lock = ResourceLock.getAtomicInteger(key);
				try	{
					// 加 synchronized 同步块
					synchronized (lock){
						// 删除文章主表
						postRepository.delete(po);
						// 删除文章正文
						postAttributeRepository.deleteById(po.getId());
						// 清理资源引用计数
						cleanResource(po.getId());
					}
				}finally {
					// 确保锁释放
					ResourceLock.giveUpAtomicInteger(key);
				}
			});
		}
	}

	// @Override：实现接口方法
	@Override
    // @Transactional(rollbackFor = Throwable.class)：可写事务
    @Transactional(rollbackFor = Throwable.class)
	// identityViews：累加文章浏览计数（每次访问 +1）
	public void identityViews(long id) {
		// 调用仓储原子更新方法，浏览数 +1
		postRepository.updateViews(id, Consts.IDENTITY_STEP);
	}

	/**
	 * 累加文章评论数
	 */
	// @Override：实现接口方法
	@Override
	// @Transactional：可写事务
	@Transactional
	// identityComments：累加文章评论计数（每条评论 +1）
	public void identityComments(long id) {
		// 调用仓储原子更新方法，评论数 +1
		postRepository.updateComments(id, Consts.IDENTITY_STEP);
	}

	// @Override：实现接口方法
	@Override
	// @PostStatusFilter：AOP 切面，过滤不展示状态的文章
	@PostStatusFilter
	// count：统计文章总数
	public long count() {
		// 调用仓储的 count 方法获取总数
		return postRepository.count();
	}

	/**
	 * <p>统一排除已关闭栏目（{@link Consts#STATUS_CLOSED}）的文章；
	 */
	// @PostStatusFilter：AOP 切面，过滤不展示状态的文章
	@PostStatusFilter
	// find：内部辅助方法，按指定排序字段查询文章列表
	private List<Post> find(String orderBy, int size) {
		// 构建分页请求：第 0 页，取 size 条，按 orderBy 字段倒序
		Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, orderBy));

		// 创建排除栏目集合（关闭状态的栏目）
		Set<Integer> excludeChannelIds = new HashSet<>();
		// 查询所有已关闭的栏目
		List<Channel> channels = channelService.findAll(Consts.STATUS_CLOSED);
		// 如果有关闭的栏目
		if (channels != null) {
			// 将关闭栏目的 ID 加入排除集合
			channels.forEach((c) -> excludeChannelIds.add(c.getId()));
		}

		// 使用 JPA Criteria 动态构建查询条件
		Page<Post> page = postRepository.findAll((root, query, builder) -> {
			// 创建空 conjunction 条件
			Predicate predicate = builder.conjunction();
			// 如果有需要排除的栏目
			if (excludeChannelIds.size() > 0) {
				// 添加 NOT IN 条件排除这些栏目
				predicate.getExpressions().add(
						builder.not(root.get("channelId").in(excludeChannelIds)));
			}
			// 返回查询条件
			return predicate;
		}, pageable);
		// 返回文章实体列表
		return page.getContent();
	}

	/**
	 * 生成文章摘要
	 * <p>根据编辑器类型选择处理路径：Markdown 先渲染为 HTML 再提取纯文本；
	 * 截取长度 126 字符避免列表渲染过长。</p>
	 */
	// trimSummary：根据编辑器类型生成文章摘要
	private String trimSummary(String editor, final String text){
		// 如果是 Markdown 编辑器
		if (Consts.EDITOR_MARKDOWN.endsWith(editor)) {
			// 先将 Markdown 渲染为 HTML，再提取纯文本并截取 126 字符
			return PreviewTextUtils.getText(MarkdownUtils.renderMarkdown(text), 126);
		} else {
			// 其他编辑器直接提取纯文本截取 126 字符
			return PreviewTextUtils.getText(text, 126);
		}
	}

	/**
	 * 将文章实体列表转换为 VO 列表，并批量回填作者与栏目信息
	 */
	// toPosts：将文章实体列表批量转换为 VO 列表，并回填作者与栏目信息
	private List<PostVO> toPosts(List<Post> posts) {
		// 收集所有文章的作者 ID
		HashSet<Long> uids = new HashSet<>();
		// 收集所有文章的栏目 ID
		HashSet<Integer> groupIds = new HashSet<>();

		// 遍历文章列表，转为 VO 并收集外键 ID
		List<PostVO> rets = posts
				.stream()
				.map(po -> {
					// 收集作者 ID
					uids.add(po.getAuthorId());
					// 收集栏目 ID
					groupIds.add(po.getChannelId());
					// 将实体拷贝为 VO
					return BeanMapUtils.copy(po);
				})
				.collect(Collectors.toList());

		// 批量回填作者与栏目信息
		buildUsers(rets, uids);
		buildGroups(rets, groupIds);

		// 返回 VO 列表
		return rets;
	}

	// buildUsers：批量回填文章列表的作者信息
	private void buildUsers(Collection<PostVO> posts, Set<Long> uids) {
		// 通过 userService 批量查询用户信息，返回以用户 ID 为 key 的 Map
		Map<Long, UserVO> userMap = userService.findMapByIds(uids);
		// 遍历文章 VO 列表，设置每个文章的作者信息
		posts.forEach(p -> p.setAuthor(userMap.get(p.getAuthorId())));
	}

	// buildGroups：批量回填文章列表的栏目信息
	private void buildGroups(Collection<PostVO> posts, Set<Integer> groupIds) {
		// 通过 channelService 批量查询栏目信息，返回以栏目 ID 为 key 的 Map
		Map<Integer, Channel> map = channelService.findMapByIds(groupIds);
		// 遍历文章 VO 列表，设置每个文章的栏目信息
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
	// countResource：增量维护文章正文中图片资源的引用计数
	private void countResource(Long postId, String originContent, String newContent){
	    // 处理旧正文为 null 的情况
	    if (StringUtils.isEmpty(originContent)){
	        originContent = "";
        }
        // 处理新正文为 null 的情况
        if (StringUtils.isEmpty(newContent)){
	        newContent = "";
        }

		// 从旧正文中提取所有图片的 md5 签名集合
		Set<String> exists = extractImageMd5(originContent);
		// 从新正文中提取所有图片的 md5 签名集合
		Set<String> news = extractImageMd5(newContent);

		// 计算新增的图片 md5 列表（在 news 但不在 exists 中）
        List<String> adds = ListUtils.removeAll(news, exists);
		// 计算删除的图片 md5 列表（在 exists 但不在 news 中）
		List<String> deleteds = ListUtils.removeAll(exists, news);

		// 处理新增图片
		if (adds.size() > 0) {
			// 根据 md5 列表查询对应的资源实体
			List<Resource> resources = resourceRepository.findByMd5In(adds);

			// 为每个新增图片创建 PostResource 关联记录
			List<PostResource> prs = resources.stream().map(n -> {
				// 创建文章-资源关联实体
				PostResource pr = new PostResource();
				// 设置资源 ID
				pr.setResourceId(n.getId());
				// 设置文章 ID
				pr.setPostId(postId);
				// 设置资源路径
				pr.setPath(n.getPath());
				// 返回关联实体
				return pr;
			}).collect(Collectors.toList());
			// 批量保存关联记录
			postResourceRepository.saveAll(prs);

			// 资源引用计数 +1
			resourceRepository.updateAmount(adds, 1);
		}

		// 处理删除图片
		if (deleteds.size() > 0) {
			// 根据 md5 列表查询对应的资源实体
			List<Resource> resources = resourceRepository.findByMd5In(deleteds);
			// 提取资源 ID 列表
			List<Long> rids = resources.stream().map(Resource::getId).collect(Collectors.toList());
			// 删除文章-资源关联记录
			postResourceRepository.deleteByPostIdAndResourceIdIn(postId, rids);
			// 资源引用计数 -1
			resourceRepository.updateAmount(deleteds, -1);
		}
	}

	/**
	 * 文章删除时清理资源引用
	 * <p>将该文章关联的所有资源引用计数 -1，并删除全部 PostResource 关联记录。</p>
	 */
	// cleanResource：文章删除时级联清理资源引用
	private void cleanResource(long postId) {
		// 查询该文章的所有资源关联记录
		List<PostResource> list = postResourceRepository.findByPostId(postId);
		// 如果没有关联记录，直接返回
		if (null == list || list.isEmpty()) {
			return;
		}
		// 提取所有资源 ID
		List<Long> rids = list.stream().map(PostResource::getResourceId).collect(Collectors.toList());
		// 这些资源的引用计数 -1
		resourceRepository.updateAmountByIds(rids, -1);
		// 删除该文章的所有资源关联记录
		postResourceRepository.deleteByPostId(postId);
	}

	/**
	 * 从正文中提取所有图片的签名（md5）
	 * <p>使用预编译正则匹配 /_signature/ 后的 md5 片段，用于资源引用计数。</p>
	 */
	// extractImageMd5：从 HTML 正文中提取所有图片的 md5 签名
	private Set<String> extractImageMd5(String text) {
		// 创建结果集合
		Set<String> md5s = new HashSet<>();

		// 使用预编译正则在文本中查找匹配项
		Matcher originMatcher = pattern.matcher(text);
		// 循环遍历所有匹配项
		while (originMatcher.find()) {
			// 提取当前匹配到的 md5 字符串
			String key = originMatcher.group();
			// 加入结果集合（Set 自动去重）
			md5s.add(key);
		}

		// 返回 md5 签名集合
		return md5s;
	}
}