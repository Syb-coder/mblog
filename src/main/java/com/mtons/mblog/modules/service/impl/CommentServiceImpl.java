// 包声明：评论服务实现类所在包
package com.mtons.mblog.modules.service.impl;

// 导入评论 VO
import com.mtons.mblog.modules.data.CommentVO;
// 导入评论实体类
import com.mtons.mblog.modules.entity.Comment;
// 导入评论仓储接口
import com.mtons.mblog.modules.repository.CommentRepository;
// 导入评论服务接口，本类实现该接口
import com.mtons.mblog.modules.service.CommentService;
// 导入文章服务接口
import com.mtons.mblog.modules.service.PostService;
// 导入用户事件计数服务接口
import com.mtons.mblog.modules.service.UserEventService;
// 导入用户服务接口
import com.mtons.mblog.modules.service.UserService;
// 导入评论补全器，用于将评论实体转为完整 VO
import com.mtons.mblog.modules.service.complementor.CommentComplementor;
// 导入 Apache Commons 集合工具类，用于判断集合非空
import org.apache.commons.collections.CollectionUtils;
// 导入 Spring 自动注入注解
import org.springframework.beans.factory.annotation.Autowired;
// 导入 Spring Data 分页相关类
import org.springframework.data.domain.*;
// 导入 Spring Service 注解
import org.springframework.stereotype.Service;
// 导入 Spring 事务注解
import org.springframework.transaction.annotation.Transactional;
// 导入 Spring 断言工具
import org.springframework.util.Assert;

// 导入 Java 工具类包
import java.util.*;

/**
 * 评论管理 Service 实现
 *
 * <h3>职责</h3>
 * 提供评论的增删查、分页查询等功能，并在评论发布/删除时同步维护
 * 用户的评论计数及文章的评论计数。
 *
 * <h3>评论的父子嵌套</h3>
 * Comment 实体通过 pid 字段实现评论回复：
 * - pid = 0：这是一条"顶级评论"（直接回复文章）
 * - pid ≠ 0：这是一条"子评论"（回复某条评论，类似盖楼）
 * 前台通过 CommentComplementor 将平铺的 Comment 列表组装为树形结构。
 *
 * <h3>评论删除的级联处理</h3>
 * 有 3 种删除场景：
 * 1. 前台用户删自己的评论 → delete(id, authorId) —— 校验作者身份
 * 2. 后台管理员批量删 → delete(List ids)
 * 3. 删除文章时级联删评论 → deleteByPostId(postId)
 *
 * 每次删除都会通过 UserEventService 减少作者的评论计数，
 * 保证 User.comments 字段与实际评论数一致。
 */
// @Service 注解：将该类注册为 Spring 业务服务组件
@Service
// @Transactional(readOnly = true)：类级别只读事务
@Transactional(readOnly = true)
// CommentServiceImpl：评论管理服务实现类
public class CommentServiceImpl implements CommentService {
	// @Autowired：Spring 自动注入评论仓储实例
	@Autowired
	// commentRepository：评论 JPA 仓储，提供评论表的 CRUD 操作
	private CommentRepository commentRepository;
	// @Autowired：Spring 自动注入用户服务实例
	@Autowired
	// userService：用户服务，用于补全评论作者信息
	private UserService userService;
	// @Autowired：Spring 自动注入用户事件计数服务实例
	@Autowired
	// userEventService：用户事件计数服务，用于维护用户的评论计数
	private UserEventService userEventService;
	// @Autowired：Spring 自动注入文章服务实例
	@Autowired
	// postService：文章服务，用于补全评论所属文章信息
	private PostService postService;

	// @Override：实现接口方法
	@Override
	// paging4Admin：后台管理分页查询评论，返回带作者信息的评论 VO 分页
	public Page<CommentVO> paging4Admin(Pageable pageable) {
		// 分页查询所有评论实体
		Page<Comment> page = commentRepository.findAll(pageable);
		// 使用评论补全器批量补全作者信息并转换为 VO 列表
		List<CommentVO> rets = CommentComplementor.of(page.getContent())
				.flutBuildUser()
				.getComments();
		// 返回 VO 分页结果
		return new PageImpl<>(rets, pageable, page.getTotalElements());
	}

	// @Override：实现接口方法
	@Override
	// pagingByAuthorId：按作者 ID 分页查询评论
	public Page<CommentVO> pagingByAuthorId(Pageable pageable, long authorId) {
		// 根据作者 ID 分页查询评论实体
		Page<Comment> page = commentRepository.findAllByAuthorId(pageable, authorId);

		// 使用评论补全器批量补全作者、父评论、文章信息
		List<CommentVO> rets = CommentComplementor.of(page.getContent())
				.flutBuildUser()
				.flutBuildParent()
				.flutBuildPost()
				.getComments();
		// 返回 VO 分页结果
		return new PageImpl<>(rets, pageable, page.getTotalElements());
	}

	// @Override：实现接口方法
	@Override
	// pagingByPostId：按文章 ID 分页查询评论
	public Page<CommentVO> pagingByPostId(Pageable pageable, long postId) {
		// 根据文章 ID 分页查询评论实体
		Page<Comment> page = commentRepository.findAllByPostId(pageable, postId);

		// 使用评论补全器批量补全作者和父评论信息
		List<CommentVO> rets = CommentComplementor.of(page.getContent())
				.flutBuildUser()
				.flutBuildParent()
				.getComments();
		// 返回 VO 分页结果
		return new PageImpl<>(rets, pageable, page.getTotalElements());
	}

	// @Override：实现接口方法
	@Override
	// findLatestComments：查询最新的 N 条评论，用于侧边栏展示
	public List<CommentVO> findLatestComments(int maxResults) {
		// 构建分页请求：第 0 页，取 maxResults 条，按 ID 倒序
		Pageable pageable = PageRequest.of(0, maxResults, Sort.by(Sort.Direction.DESC, "id"));
		// 分页查询评论实体
		Page<Comment> page = commentRepository.findAll(pageable);
		// 使用评论补全器批量补全作者信息并返回
		return CommentComplementor.of(page.getContent())
				.flutBuildUser()
				.getComments();
	}

	// @Override：实现接口方法
	@Override
	// findByIds：根据 ID 集合批量查询评论，返回以评论 ID 为 key 的 Map
	public Map<Long, CommentVO> findByIds(Set<Long> ids) {
		// 根据 ID 集合查询评论实体列表
		List<Comment> list = commentRepository.findAllById(ids);
		// 使用评论补全器批量补全作者信息并转换为 Map
		return CommentComplementor.of(list)
				.flutBuildUser()
				.toMap();
	}

	// @Override：实现接口方法
	@Override
	// findById：根据单个 ID 查询评论实体
	public Comment findById(long id) {
		// 通过仓储按 ID 查询，不存在则返回 null
		return commentRepository.findById(id).orElse(null);
	}

	/**
	 * 发布评论
	 * <p>事务传播：REQUIRED；回滚场景：评论写入失败或后续计数维护异常时整体回滚。</p>
	 * <p>计数维护：写入评论后调用 {@link UserEventService#identityComment} 累加作者评论数，
	 * 计数与评论强一致，不可分离。</p>
	 */
	// @Override：实现接口方法
	@Override
	// @Transactional(rollbackFor = Throwable.class)：可写事务，任意异常都回滚
	@Transactional(rollbackFor = Throwable.class)
	// post：发布评论，返回新评论的 ID
	public long post(CommentVO comment) {
		// 创建新的评论实体
		Comment po = new Comment();

		// 设置评论作者 ID
		po.setAuthorId(comment.getAuthorId());
		// 设置评论所属文章 ID
		po.setPostId(comment.getPostId());
		// 设置评论内容
		po.setContent(comment.getContent());
		// 设置创建时间
		po.setCreated(new Date());
		// 设置最后修改时间（发布评论时也记录）
		po.setUpdated(new Date());
		// 设置父评论 ID（0 表示顶级评论）
		po.setPid(comment.getPid());
		// 保存评论到数据库
		commentRepository.save(po);

		// 同步累加作者评论计数，与评论写入在同一事务中保证一致性
		userEventService.identityComment(comment.getAuthorId(), true);
		// 返回新评论的 ID
		return po.getId();
	}

	/**
	 * 批量删除评论
	 * <p>事务回滚场景：任一评论删除失败或计数维护异常时整体回滚。</p>
	 */
	// @Override：实现接口方法
	@Override
	// @Transactional(rollbackFor = Throwable.class)：可写事务
	@Transactional(rollbackFor = Throwable.class)
	// delete：批量删除评论（后台管理员操作）
	public void delete(List<Long> ids) {
		// 批量删除指定 ID 的评论，返回被删除的评论列表
		List<Comment> list = commentRepository.removeByIdIn(ids);
		// 如果有被删除的评论
		if (CollectionUtils.isNotEmpty(list)) {
			// 逐条减少作者评论计数，保证计数与评论状态一致
			list.forEach(po -> {
				// 减少每条评论作者的评论计数
				userEventService.identityComment(po.getAuthorId(), false);
			});
		}
	}

	/**
	 * 删除单条评论（带作者身份校验）
	 * <p>权限校验：仅作者本人可删除，校验失败抛出 IllegalArgumentException。</p>
	 */
	// @Override：实现接口方法
	@Override
	// @Transactional(rollbackFor = Throwable.class)：可写事务
	@Transactional(rollbackFor = Throwable.class)
	// delete：删除单条评论（前台用户操作，需校验作者身份）
	public void delete(long id, long authorId) {
		// 根据 ID 查询评论记录
		Optional<Comment> optional = commentRepository.findById(id);
		// 如果评论存在
		if (optional.isPresent()) {
			// 获取评论实体
			Comment po = optional.get();
			// 校验操作者与评论作者一致，防止越权删除他人评论
			Assert.isTrue(po.getAuthorId() == authorId, "无权限操作");
			// 从数据库删除评论
			commentRepository.deleteById(id);

			// 减少作者的评论计数
			userEventService.identityComment(authorId, false);
		}
	}

	/**
	 * 删除某文章下的全部评论（文章删除时级联调用）
	 * <p>级联维护：聚合所有受影响作者 ID，批量减少评论计数，避免逐条调用产生 N+1。</p>
	 */
	// @Override：实现接口方法
	@Override
	// @Transactional(rollbackFor = Throwable.class)：可写事务
	@Transactional(rollbackFor = Throwable.class)
	// deleteByPostId：删除某文章下的全部评论
	public void deleteByPostId(long postId) {
		// 根据文章 ID 删除所有关联评论，返回被删除的评论列表
		List<Comment> list = commentRepository.removeByPostId(postId);
		// 如果有被删除的评论
		if (CollectionUtils.isNotEmpty(list)) {
			// 收集去重受影响作者 ID
			Set<Long> userIds = new HashSet<>();
			// 遍历被删除的评论，收集所有作者 ID（自动去重）
			list.forEach(n -> userIds.add(n.getAuthorId()));
			// 批量减少这些作者的评论计数，避免 N+1 问题
			userEventService.identityComment(userIds, false);
		}
	}

	// @Override：实现接口方法
	@Override
	// count：统计评论总数
	public long count() {
		// 调用仓储的 count 方法获取总数
		return commentRepository.count();
	}

	// @Override：实现接口方法
	@Override
	// countByAuthorIdAndPostId：统计某用户在某文章下的评论数
	public long countByAuthorIdAndPostId(long authorId, long toId) {
		// 调用仓储的自定义统计方法
		return commentRepository.countByAuthorIdAndPostId(authorId, toId);
	}

}