// 包声明：标签服务接口所在包
package com.mtons.mblog.modules.service;

// 导入文章标签关联 VO，包含文章信息
import com.mtons.mblog.modules.data.PostTagVO;
// 导入标签 VO，包含标签基本信息和最新关联文章
import com.mtons.mblog.modules.data.TagVO;
// 导入 Spring Data 分页结果封装类
import org.springframework.data.domain.Page;
// 导入 Spring Data 分页请求参数接口
import org.springframework.data.domain.Pageable;

/**
 * 标签 Service 接口
 *
 * <h3>标签的工作方式</h3>
 * 文章发布/编辑时，用户输入的标签字符串（逗号分隔）传入 batchUpdate()：
 * 1. 切分标签名
 * 2. 对每个标签：查找 Tag 表，存在则复用，不存在则新建
 * 3. 建立 PostTag 关联
 *
 * <h3>数据模型</h3>
 * 标签是"多对多"关系的简化实现：
 * Tag ← PostTag → Post（通过 PostTag 关联表）
 */
// TagService 接口：定义标签的业务操作契约
public interface TagService {

    // pagingQueryTags：分页查询标签列表，每个标签附带最新关联文章信息
    Page<TagVO> pagingQueryTags(Pageable pageable);

    // pagingQueryPosts：根据标签名分页查询关联的文章列表
    Page<PostTagVO> pagingQueryPosts(Pageable pageable, String tagName);

    // batchUpdate：批量更新文章标签关联，names 为逗号分隔的标签名，latestPostId 为文章 ID
    void batchUpdate(String names, long latestPostId);

    // deteleMappingByPostId：删除某文章的全部标签关联，文章删除时级联调用
    void deteleMappingByPostId(long postId);
}