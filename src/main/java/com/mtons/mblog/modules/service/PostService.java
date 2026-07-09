// 包声明：文章服务接口所在包
package com.mtons.mblog.modules.service;

// 导入常量定义类，包含缓存名称等常量
import com.mtons.mblog.base.lang.Consts;
// 导入文章 VO（视图对象），包含作者、栏目等关联信息
import com.mtons.mblog.modules.data.PostVO;
// 导入缓存配置注解，指定缓存的名称空间
import org.springframework.cache.annotation.CacheConfig;
// 导入缓存驱逐注解，写操作时清除指定缓存
import org.springframework.cache.annotation.CacheEvict;
// 导入缓存可读注解，查询时自动从缓存读取或写入缓存
import org.springframework.cache.annotation.Cacheable;
// 导入 Spring Data 分页结果封装类
import org.springframework.data.domain.Page;
// 导入 Spring Data 分页请求参数接口
import org.springframework.data.domain.Pageable;

// 导入 Collection 集合接口，用于批量删除参数
import java.util.Collection;
// 导入 List 列表接口
import java.util.List;
// 导入 Map 映射接口
import java.util.Map;
// 导入 Set 集合接口，用于批量查询和排除栏目 ID 集合
import java.util.Set;

/**
 * 文章 Service 接口
 *
 * <h3>分层说明</h3>
 * Controller → PostService（接口） → PostServiceImpl（实现）
 * 接口层定义了文章的完整生命周期操作：分页查询、发布、更新、删除、浏览/评论计数。
 *
 * <h3>缓存策略</h3>
 * 查询类方法标注 @Cacheable：
 *   - paging() 缓存分页结果
 *   - findLatestPosts / findHottestPosts 分别缓存
 *   - get(id) 按单篇文章 ID 缓存
 * 写操作方法标注 @CacheEvict(allEntries = true) 全缓存失效，
 * 保证写入后缓存的即时一致性。identityViews/identityComments 只驱逐单条缓存。
 *
 * <h3>两个分页方法</h3>
 * paging() —— 前台使用，支持排除栏目（如隐藏的私密栏目）
 * paging4Admin() —— 后台使用，支持按标题模糊搜索
 */
// @CacheConfig：配置该 Service 所有方法的默认缓存名称为 CACHE_USER
@CacheConfig(cacheNames = Consts.CACHE_USER)
// PostService 接口：定义文章的业务操作契约
public interface PostService {

	// @Cacheable：自动缓存查询结果，下次相同参数直接返回缓存
	// paging：前台分页查询文章，支持按栏目过滤和排除指定栏目集合
	@Cacheable
	Page<PostVO> paging(Pageable pageable, int channelId, Set<Integer> excludeChannelIds);

	// paging4Admin：后台管理分页查询文章，支持按栏目过滤和标题模糊搜索
	Page<PostVO> paging4Admin(Pageable pageable, int channelId, String title);

	// @Cacheable：自动缓存按作者分页的查询结果
	// pagingByAuthorId：按作者 ID 分页查询其发布的文章
	@Cacheable
	Page<PostVO> pagingByAuthorId(Pageable pageable, long userId);

	// @Cacheable(key = "'latest_' + #maxResults")：以 latest_+数量 为缓存 key
	// findLatestPosts：查询最新的 N 篇文章，用于首页展示
	@Cacheable(key = "'latest_' + #maxResults")
	List<PostVO> findLatestPosts(int maxResults);

	// @Cacheable(key = "'hottest_' + #maxResults")：以 hottest_+数量 为缓存 key
	// findHottestPosts：按浏览量排序查询热门文章
	@Cacheable(key = "'hottest_' + #maxResults")
	List<PostVO> findHottestPosts(int maxResults);

	// findMapByIds：根据 ID 集合批量查询文章，返回以文章 ID 为 key 的 Map
	Map<Long, PostVO> findMapByIds(Set<Long> ids);

	// @CacheEvict(allEntries = true)：发布文章后清除所有文章相关缓存
	// post：发布新文章，返回新文章的 ID；内部同步维护标签关联和资源引用计数
	@CacheEvict(allEntries = true)
	long post(PostVO post);

	// @Cacheable(key = "'post_' + #id")：以 post_+ID 为缓存 key
	// get：根据 ID 查询文章详情（含正文内容）
	@Cacheable(key = "'post_' + #id")
	PostVO get(long id);

	// @CacheEvict(allEntries = true)：更新文章后清除所有文章相关缓存
	// update：更新文章信息（标题、正文、栏目、标签等）
	@CacheEvict(allEntries = true)
	void update(PostVO p);

	// @CacheEvict(allEntries = true)：删除文章后清除所有文章相关缓存
	// delete：删除单条文章（带作者身份校验）
	@CacheEvict(allEntries = true)
	void delete(long id, long authorId);

	// @CacheEvict(allEntries = true)：批量删除文章后清除所有文章相关缓存
	// delete：批量删除文章
	@CacheEvict(allEntries = true)
	void delete(Collection<Long> ids);

	// @CacheEvict(key = "'view_' + #id")：仅驱逐该文章浏览数缓存
	// identityViews：累加文章浏览计数（每次访问 +1）
	@CacheEvict(key = "'view_' + #id")
	void identityViews(long id);

	// @CacheEvict(key = "'view_' + #id")：仅驱逐该文章评论数缓存
	// identityComments：累加文章评论计数（每条评论 +1）
	@CacheEvict(key = "'view_' + #id")
	void identityComments(long id);

	// count：统计文章总数，用于仪表盘展示
	long count();
}