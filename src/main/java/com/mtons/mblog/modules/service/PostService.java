package com.mtons.mblog.modules.service;

import com.mtons.mblog.base.lang.Consts;
import com.mtons.mblog.modules.data.PostVO;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 文章管理 Service
 * <p>
 * </p>
 * <p>
 * </p>
 *
 */
@CacheConfig(cacheNames = Consts.CACHE_USER)
public interface PostService {
	/**
	 * <p>支持按栏目筛选与排除栏目（用于隐藏私密栏目）。</p>
	 *
	 * @param pageable           分页参数
	 * @param channelId          栏目 ID，<= 0 时忽略
	 * @param excludeChannelIds  需要排除的栏目 ID 集合
	 * @return 文章分页结果
	 */
	@Cacheable
	Page<PostVO> paging(Pageable pageable, int channelId, Set<Integer> excludeChannelIds);

	/* *
	 * @param pageable  分页参数
	 * @param channelId 栏目 ID，<= 0 时忽略
	 * @param title     文章标题，模糊匹配
	 * @return 文章分页结果
	 */
	Page<PostVO> paging4Admin(Pageable pageable, int channelId, String title);

	/* *
	 * @param pageable 分页参数
	 * @param userId   作者 ID
	 * @return 文章分页结果
	 */
	@Cacheable
	Page<PostVO> pagingByAuthorId(Pageable pageable, long userId);

	/* *
	 * @param maxResults 返回记录上限
	 * @return 最新文章列表
	 */
	@Cacheable(key = "'latest_' + #maxResults")
	List<PostVO> findLatestPosts(int maxResults);

	/* *
	 * @param maxResults 返回记录上限
	 * @return 热门文章列表
	 */
	@Cacheable(key = "'hottest_' + #maxResults")
	List<PostVO> findHottestPosts(int maxResults);

	/* *
	 * @param ids 文章 ID 集合
	 * @return 以文章 id 为 key 的映射
	 */
	Map<Long, PostVO> findMapByIds(Set<Long> ids);

	/**
	 * 发布文章（新增）
	 *
	 * @param post 文章 VO
	 * @return 新文章 ID
	 */
	@CacheEvict(allEntries = true)
	long post(PostVO post);

	/* *
	 * @param id 文章 ID
	 */
	@Cacheable(key = "'post_' + #id")
	PostVO get(long id);

	/**
	 * 更新文章
	 *
	 * @param p 文章 VO
	 */
	@CacheEvict(allEntries = true)
	void update(PostVO p);

	/**
	 * 删除单条文章 - 需校验作者身份，触发级联清理资源、属性与事件广播
	 *
	 * @param id       文章 ID
	 * @param authorId 操作者 ID，用于权限校验
	 */
	@CacheEvict(allEntries = true)
	void delete(long id, long authorId);

	/**
	 * 批量删除文章，触发级联清理资源、属性与事件广播
	 *
	 * @param ids 文章 ID 集合
	 */
	@CacheEvict(allEntries = true)
	void delete(Collection<Long> ids);

	/* *
	 * @param id 文章 ID
	 */
	@CacheEvict(key = "'view_' + #id")
	void identityViews(long id);

	/* *
	 * @param id 文章 ID
	 */
	@CacheEvict(key = "'view_' + #id")
	void identityComments(long id);

	/**
	 * 收藏文章（同步累加文章收藏数 + 写入收藏记录）
	 *
	 * @param userId  用户 ID
	 * @param postId  文章 ID
	 */
	@CacheEvict(key = "'view_' + #postId")
	void favor(long userId, long postId);

	/**
	 * 取消收藏文章（同步减少文章收藏数 + 删除收藏记录）
	 *
	 * @param userId  用户 ID
	 * @param postId  文章 ID
	 */
	@CacheEvict(key = "'view_' + #postId")
	void unfavor(long userId, long postId);

	/**
	 * 统计文章总数
	 *
	 * @return 文章总数
	 */
	long count();
}
