package com.mtons.mblog.modules.service;

import com.mtons.mblog.modules.data.FavoriteVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 收藏记录 Service
 * <p>
 * 负责用户对文章的收藏/取消收藏及级联清理等业务。
 * </p>
 *
 */
public interface FavoriteService {
    /* *
     * @param pageable 分页参数
     * @param userId   用户 ID
     * @return 收藏记录分页结果
     */
    Page<FavoriteVO> pagingByUserId(Pageable pageable, long userId);

    /**
     * 添加收藏记录
     *
     * @param userId  用户 ID
     * @param postId  文章 ID
     */
    void add(long userId, long postId);

    /**
     * 取消收藏（未收藏时抛出业务异常）
     *
     * @param userId  用户 ID
     * @param postId  文章 ID
     */
    void delete(long userId, long postId);

    /**
     * 删除某文章下的全部收藏记录（用于文章删除级联清理）
     *
     * @param postId 文章 ID
     */
    void deleteByPostId(long postId);
}
