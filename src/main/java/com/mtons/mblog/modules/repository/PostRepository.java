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
 * 文章数据访问层
 *
 * <h3>说明</h3>
 * 继承 JpaRepository<Post, Long> 获得基础 CRUD，
 * 继承 JpaSpecificationExecutor<Post> 获得动态查询支持（配合 @PostStatusFilter 的 Hibernate Filter）。
 * 之所以需要 JpaSpecificationExecutor，是因为前台列表页需要根据不同的排序方式（最新/最热）
 * 和筛选条件（栏目、状态）动态拼接查询条件。
 *
 * <h3>为什么 updateViews/updateComments 用 @Modifying + @Query？</h3>
 * 文章的浏览数和评论数是热点数据，每次访问都先查询再更新的做法有两个问题：
 * 1. 存在并发覆盖风险（读取旧值，写回旧值+1，覆盖了别人的 +1）
 * 2. 查询 + 更新需要两次数据库操作
 *
 * 使用 UPDATE ... SET views = views + 1 是单条 SQL 原子操作，
 * 数据库层面的行锁保证了并发安全，且只需一次数据库交互。
 * increment 参数支持负数，用于文章删除/评论删除时的回退。
 */
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    /**
     * 按作者 ID 分页查询
     * @param pageable 分页参数
     * @param authorId 作者 ID
     */
    Page<Post> findAllByAuthorId(Pageable pageable, long authorId);

    /**
     * 原子递增/递减文章浏览数
     * @param id        文章 ID
     * @param increment 增量（可为负，用于回滚）
     */
    @Modifying
    @Query("update Post set views = views + :increment where id = :id")
    void updateViews(@Param("id") long id, @Param("increment") int increment);

    /**
     * 原子递增/递减文章评论数
     * @param id        文章 ID
     * @param increment 增量（可为负，用于评论删除时回退）
     */
    @Modifying
    @Query("update Post set comments = comments + :increment where id = :id")
    void updateComments(@Param("id") long id, @Param("increment") int increment);

}
