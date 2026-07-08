package com.mtons.mblog.modules.entity;

import jakarta.persistence.*;

/**
 * 文章-标签关联实体 —— 对应数据库表 mto_post_tag
 *
 * <h3>业务含义</h3>
 * Post 和 Tag 是多对多关系：一篇文章可以有多个标签（如 "Java,Spring Boot"），
 * 一个标签也可以属于多篇文章。这个中间表就是用来存储这种多对多关系的。
 *
 * <h3>为什么需要 PostTag 而不是直接用 Post.tags 字段？</h3>
 * Post.tags 字段只是一个逗号分隔的字符串（如 "Java,Spring Boot"），
 * 用于前台展示。但通过 PostTag 表，我们可以：
 * 1. 查询"所有打了'Java'标签的文章"
 * 2. 统计每个标签的文章数量（Tag.posts 字段） — 需要 PostTag 来计数
 * 3. 实现标签云等功能
 * 4. 发现同标签相关文章
 *
 * 简而言之：Post.tags 是给人看的，PostTag 是给程序查询用的。
 */
@Entity
@Table(name = "mto_post_tag", indexes = {
        @Index(name = "IK_TAG_ID", columnList = "tag_id")
})
public class PostTag {
    /**
     * 主键ID
     * <p>自增主键，对应表 {@code mto_post_tag.id}，不可空。</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * 文章ID
     * <p>对应列 {@code post_id}，关联 {@code mto_post.id}，标识所属文章。</p>
     */
    @Column(name = "post_id")
    private long postId;

    /**
     * 标签ID
     * <p>对应列 {@code tag_id}，关联 {@code mto_tag.id}，标识关联的标签。</p>
     */
    @Column(name = "tag_id")
    private long tagId;

    /**
     * 权重
     * <p>对应列 {@code weight}；用于标签在该文章中的排序与权重控制。</p>
     */
    private long weight;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public long getTagId() {
        return tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }
}
