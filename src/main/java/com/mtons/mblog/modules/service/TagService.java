package com.mtons.mblog.modules.service;

import com.mtons.mblog.modules.data.PostTagVO;
import com.mtons.mblog.modules.data.TagVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 标签管理 Service
 * <p>
 * </p>
 *
 */
public interface TagService {
    /* *
     * @param pageable 分页参数
     * @return 标签分页结果
     */
    Page<TagVO> pagingQueryTags(Pageable pageable);

    /* *
     * @param pageable 分页参数
     * @param tagName  标签名
     * @return 文章-标签关联分页结果
     */
    Page<PostTagVO> pagingQueryPosts(Pageable pageable, String tagName);

    /**
     * 批量更新文章-标签关联（发布/编辑文章时调用）
     *
     * @param names         标签名集合（按分隔符拆分）
     * @param latestPostId  最新关联文章 ID
     */
    void batchUpdate(String names, long latestPostId);

    /**
     * 删除某文章的全部标签关联（用于文章删除级联清理）
     * <p>同步减少标签的 posts 计数。</p>
     *
     * @param postId 文章 ID
     */
    void deteleMappingByPostId(long postId);
}
