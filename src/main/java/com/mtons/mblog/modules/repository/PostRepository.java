package com.mtons.mblog.modules.repository;

import com.mtons.mblog.modules.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

/**
 * 文章（Post）数据访问层
 * <p>
 * 对应 Entity：{@link Post}，主键类型 Long。
 * </p>
 *
 */
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    /* *
     * @param pageable 分页参数
     * @param authorId 作者 ID
     * @return 该作者的文章分页结果
     */
    Page<Post> findAllByAuthorId(Pageable pageable, long authorId);

    /**
     * <p>
     * :increment 可为负值用于回滚场景。
     * </p>
     *
     * @param id        文章 ID
     * @param increment 增量（可为负）
     */
    @Modifying
    @Query("update Post set views = views + :increment where id = :id")
    void updateViews(@Param("id") long id, @Param("increment") int increment);

    /**
     * 原子递增文章收藏数
     * <p>
     * :increment 可为负值用于取消收藏回退场景。
     * </p>
     *
     * @param id        文章 ID
     * @param increment 增量（可为负）
     */
    @Modifying
    @Query("update Post set favors = favors + :increment where id = :id")
    void updateFavors(@Param("id") long id, @Param("increment") int increment);

    /**
     * 原子递增文章评论数
     * <p>
     * :increment 可为负值用于评论删除回退场景。
     * </p>
     *
     * @param id        文章 ID
     * @param increment 增量（可为负）
     */
    @Modifying
    @Query("update Post set comments = comments + :increment where id = :id")
    void updateComments(@Param("id") long id, @Param("increment") int increment);

}
