package com.mtons.mblog.modules.service.impl;

import com.mtons.mblog.modules.data.CommentVO;
import com.mtons.mblog.modules.entity.Comment;
import com.mtons.mblog.modules.repository.CommentRepository;
import com.mtons.mblog.modules.service.CommentService;
import com.mtons.mblog.modules.service.PostService;
import com.mtons.mblog.modules.service.UserEventService;
import com.mtons.mblog.modules.service.UserService;
import com.mtons.mblog.modules.service.complementor.CommentComplementor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

/**
 * 评论管理 Service 实现
 * <p>
 * 关键依赖：
 * <ul>
 *   <li>{@link CommentRepository}：评论 JPA 仓储</li>
 *   <li>{@link UserService}：用于回填评论作者信息</li>
 *   <li>{@link UserEventService}：用于同步作者评论计数</li>
 *   <li>{@link PostService}：用于回填评论所属文章信息</li>
 * </ul>
 * </p>
 * <p>
 * 事务策略：类级别只读事务；写操作方法（post/delete）使用 {@code rollbackFor = Throwable.class}
 * 对所有受检异常回滚，保证评论写入与计数维护的原子性。
 * </p>
 *
 */
@Service
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private UserEventService userEventService;
	@Autowired
	private PostService postService;

	@Override
	public Page<CommentVO> paging4Admin(Pageable pageable) {
		Page<Comment> page = commentRepository.findAll(pageable);
		List<CommentVO> rets = CommentComplementor.of(page.getContent())
				.flutBuildUser()
				.getComments();
		return new PageImpl<>(rets, pageable, page.getTotalElements());
	}

	@Override
	public Page<CommentVO> pagingByAuthorId(Pageable pageable, long authorId) {
		Page<Comment> page = commentRepository.findAllByAuthorId(pageable, authorId);

		List<CommentVO> rets = CommentComplementor.of(page.getContent())
				.flutBuildUser()
				.flutBuildParent()
				.flutBuildPost()
				.getComments();
		return new PageImpl<>(rets, pageable, page.getTotalElements());
	}

	@Override
	public Page<CommentVO> pagingByPostId(Pageable pageable, long postId) {
		Page<Comment> page = commentRepository.findAllByPostId(pageable, postId);

		List<CommentVO> rets = CommentComplementor.of(page.getContent())
				.flutBuildUser()
				.flutBuildParent()
				.getComments();
		return new PageImpl<>(rets, pageable, page.getTotalElements());
	}

	@Override
	public List<CommentVO> findLatestComments(int maxResults) {
		Pageable pageable = PageRequest.of(0, maxResults, Sort.by(Sort.Direction.DESC, "id"));
		Page<Comment> page = commentRepository.findAll(pageable);
		return CommentComplementor.of(page.getContent())
				.flutBuildUser()
				.getComments();
	}

	@Override
	public Map<Long, CommentVO> findByIds(Set<Long> ids) {
		List<Comment> list = commentRepository.findAllById(ids);
		return CommentComplementor.of(list)
				.flutBuildUser()
				.toMap();
	}

	@Override
	public Comment findById(long id) {
		return commentRepository.findById(id).orElse(null);
	}

	/**
	 * 发布评论
	 * <p>事务传播：REQUIRED；回滚场景：评论写入失败或后续计数维护异常时整体回滚。</p>
	 * <p>计数维护：写入评论后调用 {@link UserEventService#identityComment} 累加作者评论数，
	 * 计数与评论强一致，不可分离。</p>
	 */
	@Override
	@Transactional(rollbackFor = Throwable.class)
	public long post(CommentVO comment) {
		Comment po = new Comment();

		po.setAuthorId(comment.getAuthorId());
		po.setPostId(comment.getPostId());
		po.setContent(comment.getContent());
		po.setCreated(new Date());
		po.setPid(comment.getPid());
		commentRepository.save(po);

		// 同步累加作者评论计数，与评论写入在同一事务中保证一致性
		userEventService.identityComment(comment.getAuthorId(), true);
		return po.getId();
	}

	/**
	 * 批量删除评论
	 * <p>事务回滚场景：任一评论删除失败或计数维护异常时整体回滚。</p>
	 */
	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void delete(List<Long> ids) {
		List<Comment> list = commentRepository.removeByIdIn(ids);
		if (CollectionUtils.isNotEmpty(list)) {
			// 逐条减少作者评论计数，保证计数与评论状态一致
			list.forEach(po -> {
				userEventService.identityComment(po.getAuthorId(), false);
			});
		}
	}

	/**
	 * 删除单条评论（带作者身份校验）
	 * <p>权限校验：仅作者本人可删除，校验失败抛出 IllegalArgumentException。</p>
	 */
	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void delete(long id, long authorId) {
		Optional<Comment> optional = commentRepository.findById(id);
		if (optional.isPresent()) {
			Comment po = optional.get();
			// 校验操作者与评论作者一致，防止越权删除他人评论
			Assert.isTrue(po.getAuthorId() == authorId, "无权限操作");
			commentRepository.deleteById(id);

			userEventService.identityComment(authorId, false);
		}
	}

	/**
	 * 删除某文章下的全部评论（文章删除时级联调用）
	 * <p>级联维护：聚合所有受影响作者 ID，批量减少评论计数，避免逐条调用产生 N+1。</p>
	 */
	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void deleteByPostId(long postId) {
		List<Comment> list = commentRepository.removeByPostId(postId);
		if (CollectionUtils.isNotEmpty(list)) {
			// 收集去重受影响作者 ID，批量减少计数
			Set<Long> userIds = new HashSet<>();
			list.forEach(n -> userIds.add(n.getAuthorId()));
			userEventService.identityComment(userIds, false);
		}
	}

	@Override
	public long count() {
		return commentRepository.count();
	}

	@Override
	public long countByAuthorIdAndPostId(long authorId, long toId) {
		return commentRepository.countByAuthorIdAndPostId(authorId, toId);
	}

}
