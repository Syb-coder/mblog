package com.mtons.mblog.modules.service;

import com.mtons.mblog.modules.data.CommentVO;
import com.mtons.mblog.modules.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 评论管理 Service
 * <p>
 * </p>
 *
 */
public interface CommentService {
	/* *
	 * @param pageable 分页参数
	 * @return 评论分页结果
	 */
	Page<CommentVO> paging4Admin(Pageable pageable);

	/* *
	 * @param pageable 分页参数
	 * @param authorId 作者 ID
	 * @return 评论分页结果
	 */
	Page<CommentVO> pagingByAuthorId(Pageable pageable, long authorId);

	/* *
	 * @param pageable 分页参数
	 * @param postId   文章 ID
	 * @return 评论分页结果
	 */
	Page<CommentVO> pagingByPostId(Pageable pageable, long postId);

	/* *
	 * @param maxResults 返回记录上限
	 * @return 最新评论列表
	 */
	List<CommentVO> findLatestComments(int maxResults);

	/* *
	 * @param ids 评论 ID 集合
	 * @return 以评论 id 为 key 的映射
	 */
	Map<Long, CommentVO> findByIds(Set<Long> ids);

	/* *
	 * @param id 评论 ID
	 */
	Comment findById(long id);

	/**
	 * 发布评论
	 * <p>写入评论后同步累加作者评论数（UserEvent 维护）。</p>
	 *
	 * @param comment 评论 VO
	 * @return 新评论 ID
	 */
	long post(CommentVO comment);

	/**
	 * 批量删除评论并维护作者评论计数
	 *
	 * @param ids 评论 ID 列表
	 */
	void delete(List<Long> ids);

	/**
	 * 删除单条评论（需校验作者身份）
	 *
	 * @param id       评论 ID
	 * @param authorId 操作者 ID，用于权限校验
	 */
	void delete(long id, long authorId);

	/**
	 * 删除某文章下的全部评论（用于文章删除级联清理）
	 *
	 * @param postId 文章 ID
	 */
	void deleteByPostId(long postId);

	/**
	 * 统计评论总数
	 *
	 * @return 评论总数
	 */
	long count();

	/**
	 * 统计某作者在某文章下的评论数（用于评论频率限制等场景）
	 *
	 * @param authorId 作者 ID
	 * @param postId   文章 ID
	 * @return 评论数
	 */
	long countByAuthorIdAndPostId(long authorId, long postId);
}
