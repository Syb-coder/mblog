package com.mtons.mblog.modules.service;

import com.mtons.mblog.modules.data.PostVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 文章全文检索 Service
 * <p>
 * 基于 Hibernate Search 实现文章标题、摘要、标签的全文检索，并支持索引重建。
 * 检索结果会回填作者信息。
 * </p>
 *
 * @version : 1.0
 * @date : 2019/1/18
 */
public interface PostSearchService {
    /**
     * 根据关键词全文检索文章
     * <p>对 title、summary、tags 字段进行模糊匹配（fuzzy = 1），结果回填作者信息。</p>
     *
     * @param pageable 分页参数
     * @param term     检索关键词
     * @return 文章分页结果
     * @throws Exception 检索过程异常（如索引未就绪等）
     */
    Page<PostVO> search(Pageable pageable, String term) throws Exception;

    /**
     * 重建文章索引
     * <p>使用 Hibernate Search 的 MassIndexer 全量重建，建议在数据迁移或索引异常时执行。</p>
     */
    void resetIndexes();
}
