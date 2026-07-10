package com.sunblog.modules.repository;

import com.sunblog.modules.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 评论数据访问层
 *
 * <h3>查询场景</h3>
 * - findAllByPostId：文章详情页加载评论列表
 * - findAllByAuthorId：用户个人主页展示"我的评论"
 * - countByAuthorIdAndPostId：防止重复刷评论
 *
 * <h3>删除说明</h3>
 * removeByIdIn 和 removeByPostId 返回被删除的实体列表，
 * 便于 Service 层拿到列表后同步更新 Post.comments 计数。
 */
public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {

	Page<Comment> findAll(Pageable pageable);

	/**
	 * 按文章 ID 分页查询评论
	 * @param pageable 分页参数
	 * @param postId   文章 ID
	 */
	Page<Comment> findAllByPostId(Pageable pageable, long postId);

	/**
	 * 按作者 ID 分页查询评论
	 * @param pageable 分页参数
	 * @param authorId 作者 ID
	 */
	Page<Comment> findAllByAuthorId(Pageable pageable, long authorId);

	/**
	 * 按 ID 集合批量删除（删除单条评论时用）
	 * @return 被删除的评论列表，用于后续计数回退
	 */
	List<Comment> removeByIdIn(Collection<Long> ids);

	/**
	 * 按文章 ID 删除所有评论（删除文章时级联清理）
	 * @return 被删除的评论列表
	 */
	List<Comment> removeByPostId(long postId);

	/**
	 * 统计某用户在某篇文章的评论数（用于频率控制）
	 */
	long countByAuthorIdAndPostId(long authorId, long postId);
}