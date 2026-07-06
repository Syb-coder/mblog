package com.mtons.mblog.modules.repository;

import com.mtons.mblog.modules.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 用户（User）数据访问层
 * <p>
 * 对应 Entity：{@link User}，主键类型 Long。
 * 以及用户文章数、评论数的原子递增更新能力。
 * </p>
 *
 */
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /* *
     * @param username 用户名
     * @return 用户记录，未命中返回 null
     */
    User findByUsername(String username);

    /* *
     * @param email 邮箱地址
     * @return 用户记录，未命中返回 null
     */
    User findByEmail(String email);

    /**
     * 原子递增用户的文章数计数
     * <p>
     * :increment 可为负值用于文章删除回退场景。
     * </p>
     *
     * @param id        用户 ID
     * @param increment 增量（可为负）
     * @return 受影响行数
     */
    @Modifying
    @Query("update User set posts = posts + :increment where id = :id")
    int updatePosts(@Param("id") long id, @Param("increment") int increment);

    /**
     * 按 ID 集合原子递增用户的评论数计数
     * <p>
     * 通过 JPQL update 一次性累加 comments 字段，支持批量用户场景；
     * :increment 可为负值用于评论删除回退场景。
     * </p>
     *
     * @param ids       用户 ID 集合
     * @param increment 增量（可为负）
     * @return 受影响行数
     */
    @Modifying
    @Query("update User set comments = comments + :increment where id in (:ids)")
    int updateComments(@Param("ids") Collection<Long> ids, @Param("increment") int increment);

}
