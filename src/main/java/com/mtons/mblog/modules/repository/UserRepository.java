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
 * 用户数据访问层
 *
 * <h3>功能</h3>
 * 除了 JpaRepository 提供的标准 CRUD 外，还包含：
 * 1. 按用户名/邮箱精确查找（用于登录校验和注册判重）
 * 2. 原子 update 用户维度的文章数/评论数（配合 PostServiceImpl 的发布/删除流程）
 *
 * <h3>为什么用原字 update 而不是先查询再 set？</h3>
 * 和 PostRepository 同样的原因：高并发场景下防止计数丢失，
 * 以及减少一次数据库查询。
 *
 * <h3>批量 updateComments vs 单个 updatePosts</h3>
 * 发布/删除文章只影响一个用户，所以 updatePosts 按单用户更新。
 * 发表/删除评论可能涉及多个用户（回复场景），所以 updateComments 支持 ID 集合批量更新。
 */
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * 按用户名查找（登录校验）
     * @param username 用户名
     */
    User findByUsername(String username);

    /**
     * 按邮箱查找（注册判重）
     * @param email 邮箱地址
     */
    User findByEmail(String email);

    /**
     * 原子更新用户文章计数
     * @param id        用户 ID
     * @param increment 增量（可为负）
     * @return 受影响行数
     */
    @Modifying
    @Query("update User set posts = posts + :increment where id = :id")
    int updatePosts(@Param("id") long id, @Param("increment") int increment);

    /**
     * 批量原子更新用户评论计数
     * @param ids       用户 ID 集合
     * @param increment 增量（可为负）
     * @return 受影响行数
     */
    @Modifying
    @Query("update User set comments = comments + :increment where id in (:ids)")
    int updateComments(@Param("ids") Collection<Long> ids, @Param("increment") int increment);

}
