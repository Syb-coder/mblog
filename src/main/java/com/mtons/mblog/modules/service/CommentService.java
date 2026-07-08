// 包声明：评论服务接口所在包
package com.mtons.mblog.modules.service;

// 导入评论 VO（视图对象），包含作者、文章等关联信息
import com.mtons.mblog.modules.data.CommentVO;
// 导入评论实体类，数据库映射对象
import com.mtons.mblog.modules.entity.Comment;
// 导入 Spring Data 分页结果封装类
import org.springframework.data.domain.Page;
// 导入 Spring Data 分页请求参数接口
import org.springframework.data.domain.Pageable;

// 导入 List 列表接口
import java.util.List;
// 导入 Map 映射接口
import java.util.Map;
// 导入 Set 集合接口，用于批量查询参数
import java.util.Set;

/**
 * 评论 Service 接口
 *
 * <h3>评论的核心流程</h3>
 * post(commentVO)：新增评论 →
 *   1. 保存 Comment 记录（含父评论 pid，支持嵌套回复）
 *   2. 调用 UserRepository.updateComments(+1) 原子递增
 *   3. 调用 PostRepository.updateComments(+1) 原子递增
 *
 * delete(id, authorId)：删除单条评论 →
 *   1. 校验作者身份
 *   2. 删除 Comment
 *   3. 原子递减相关计数
 *
 * <h3>pid 字段的用途</h3>
 * pid = 0 → 顶层评论
 * pid > 0 → 回复某条评论（但项目采用"平铺显示"而非真正的嵌套展示）
 */
// CommentService 接口：定义评论的业务操作契约
public interface CommentService {

	// paging4Admin：后台管理分页查询评论，返回带作者信息的评论 VO 分页
	Page<CommentVO> paging4Admin(Pageable pageable);

	// pagingByAuthorId：按作者 ID 分页查询评论，用于"我的评论"页面
	Page<CommentVO> pagingByAuthorId(Pageable pageable, long authorId);

	// pagingByPostId：按文章 ID 分页查询评论，用于文章详情页的评论列表
	Page<CommentVO> pagingByPostId(Pageable pageable, long postId);

	// findLatestComments：查询最新的 N 条评论，用于侧边栏展示
	List<CommentVO> findLatestComments(int maxResults);

	// findByIds：根据 ID 集合批量查询评论，返回以评论 ID 为 key 的 Map
	Map<Long, CommentVO> findByIds(Set<Long> ids);

	// findById：根据单个 ID 查询评论实体
	Comment findById(long id);

	// post：发布评论，返回新评论的 ID；内部同步维护用户和文章的评论计数
	long post(CommentVO comment);

	// delete：批量删除评论（后台管理员操作），内部同步维护评论计数
	void delete(List<Long> ids);

	// delete：删除单条评论（前台用户操作，需校验作者身份）
	void delete(long id, long authorId);

	// deleteByPostId：删除某文章下的全部评论（文章删除时级联调用）
	void deleteByPostId(long postId);

	// count：统计评论总数，用于仪表盘展示
	long count();

	// countByAuthorIdAndPostId：统计某用户在某文章下的评论数，用于防刷评论校验
	long countByAuthorIdAndPostId(long authorId, long postId);
}