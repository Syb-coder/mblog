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
 * 标签数据访问层
 *
 * <h3>功能</h3>
 * findByName 用于文章发布时：用户输入标签名 → 查找已有标签 → 存在则复用 / 不存在则新建。
 * decrementPosts 用于文章删除时回退标签的引用计数（posts > 0 条件防止扣成负数）。
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, Long>, JpaSpecificationExecutor<Tag> {

    /**
     * 按标签名精确查找（用于复用已有标签）
     */
    Tag findByName(String name);

    /**
     * 批量扣减标签的文章计数（文章删除时回退）
     * posts > 0 条件防止扣成负数
     */
    @Modifying
    @Query("update Tag set posts = posts - 1 where id in (:ids) and posts > 0")
    int decrementPosts(@Param("ids") Collection<Long> ids);
}
