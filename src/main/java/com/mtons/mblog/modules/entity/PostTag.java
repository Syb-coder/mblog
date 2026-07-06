package com.mtons.mblog.modules.entity;

import jakarta.persistence.*;

/**
 * 文章标签映射 Entity
 * <p>
 * 对应数据库表 {@code mto_post_tag}，维护文章与标签的多对多关系。
 * 业务含义：每条记录表示一篇文章的一个标签关联，{@code weight} 用于排序与权重控制。
 * </p>
 *
 * <p>关键索引：
 * <ul>
 * </ul>
 * </p>
 *
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
