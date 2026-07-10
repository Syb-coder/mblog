package com.sunblog.modules.entity;

import jakarta.persistence.*;
import java.util.Date;

/**
 * 标签 Entity
 * <p>
 * 业务含义：标签主表，{@link PostTag} 通过 {@code tag_id} 关联到本表主键，
 * 维护文章-标签的多对多关系；{@code latest_post_id} 与 {@code posts} 为冗余统计字段，
 * 便于前台标签云展示。
 * </p>
 *
 * <p>关键约束：
 * <ul>
 *   <li>{@code name} 不可空、唯一、{@code updatable = false}，长度 32；标签名创建后不可修改。</li>
 * </ul>
 * </p>
 *
 */
@Entity
@Table(name = "mto_tag")
public class Tag {
    /**
     * 主键ID
     * <p>自增主键，对应表 {@code mto_tag.id}，不可空。</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * 标签名称
     * <p>对应列 {@code name}，不可空、唯一，长度 32；{@code updatable = false} 表示创建后不可更新。</p>
     */
    @Column(unique = true, nullable = false, updatable = false, length = 32)
    private String name;

    /**
     * 预览图
     */
    @Column(length = 128)
    private String thumbnail;

    /**
     * 描述
     * <p>对应列 {@code description}；标签的中文说明，便于后台展示。</p>
     */
    private String description;

    /**
     * 最后发表的文章Id
     */
    private long latestPostId;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date created;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date updated;

    /**
     * 标签下的文章数
     * <p>对应列 {@code posts}；冗余统计字段，记录该标签下文章总数，便于标签云展示。</p>
     */
    private int posts;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getLatestPostId() {
        return latestPostId;
    }

    public void setLatestPostId(long latestPostId) {
        this.latestPostId = latestPostId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public int getPosts() {
        return posts;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }
}
