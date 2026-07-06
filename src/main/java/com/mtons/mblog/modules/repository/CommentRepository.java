package com.mtons.mblog.modules.repository;

import com.mtons.mblog.modules.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 评论（Comment）数据访问层
 * <p>
 * 对应 Entity：{@link Comment}，主键类型 Long。
 * </p>
 *
 */
public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {

	/* *
	 * @param pageable 分页参数
	 * @return 评论分页结果
	 */
	Page<Comment> findAll(Pageable pageable);

	/* *
	 * @param pageable 分页参数
	 * @param postId   文章 ID
	 * @return 该文章的评论分页结果
	 */
	Page<Comment> findAllByPostId(Pageable pageable, long postId);

	/* *
	 * @param pageable 分页参数
	 * @param authorId 作者 ID
	 * @return 该作者发表的评论分页结果
	 */
	Page<Comment> findAllByAuthorId(Pageable pageable, long authorId);

	/**
	 * 按 ID 集合批量删除评论
	 *
	 * @param ids 待删除评论 ID 集合
	 * @return 被删除的评论列表
	 */
	List<Comment> removeByIdIn(Collection<Long> ids);

	/**
	 * 按文章 ID 删除该文章下所有评论（用于文章删除时级联清理）
	 *
	 * @param postId 文章 ID
	 * @return 被删除的评论列表
	 */
	List<Comment> removeByPostId(long postId);

	/**
	 * 统计某用户在某篇文章下发布的评论数量
	 * <p>用于校验评论频率 / 防止重复刷评论。</p>
	 *
	 * @param authorId 作者 ID
	 * @param postId   文章 ID
	 * @return 评论数量
	 */
	long countByAuthorIdAndPostId(long authorId, long postId);
}
