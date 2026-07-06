package com.mtons.mblog.modules.repository;

import com.mtons.mblog.modules.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * 标签（Tag）数据访问层
 * <p>
 * 对应 Entity：{@link Tag}，主键类型 Long。
 * </p>
 *
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, Long>, JpaSpecificationExecutor<Tag> {

    /* *
     * @param name 标签名
     * @return 标签记录，未命中返回 null
     */
    Tag findByName(String name);

    /**
     * 按 ID 集合原子递减标签下文章计数
     * <p>
     * 通过 JPQL update 一次性扣减 posts 字段，附加 posts &gt; 0 条件防止计数出现负数；
     * 用于文章删除 / 解绑标签时回退标签的引用计数。
     * </p>
     *
     * @param ids 标签 ID 集合
     * @return 受影响行数
     */
    @Modifying
    @Query("update Tag set posts = posts - 1 where id in (:ids) and posts > 0")
    int decrementPosts(@Param("ids") Collection<Long> ids);
}
