package com.mtons.mblog.modules.repository;

import com.mtons.mblog.modules.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 收藏（Favorite）数据访问层
 * <p>
 * 对应 Entity：{@link Favorite}，主键类型 Long。
 * </p>
 *
 */
public interface FavoriteRepository extends JpaRepository<Favorite, Long>, JpaSpecificationExecutor<Favorite> {

    /**
     * <p>用于判断当前用户是否已收藏某篇文章。</p>
     *
     * @param userId 用户 ID
     * @param postId 文章 ID
     * @return 收藏记录，未收藏返回 null
     */
    Favorite findByUserIdAndPostId(long userId, long postId);

    /* *
     * @param pageable 分页参数
     * @param userId   用户 ID
     * @return 该用户的收藏分页结果
     */
    Page<Favorite> findAllByUserId(Pageable pageable, long userId);

    /**
     * 按文章 ID 删除所有相关收藏（用于文章删除时级联清理）
     *
     * @param postId 文章 ID
     * @return 被删除的记录数
     */
    int deleteByPostId(long postId);
}
