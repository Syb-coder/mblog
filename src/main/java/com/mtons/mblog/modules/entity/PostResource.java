package com.mtons.mblog.modules.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * 文章图片 Entity
 * <p>
 * 对应数据库表 {@code mto_post_resource}，记录文章与图片资源的关联关系及在文章中的排序。
 * 业务含义：一篇文章可关联多张图片，通过 {@code post_id} 关联文章、{@code resource_id}
 * </p>
 *
 * <p>关键索引：
 * <ul>
 * </ul>
 * </p>
 *
 * <p>注：使用 Lombok {@link Data} 自动生成 getter/setter。</p>
 *
 */
@Data
@Entity
@Table(name = "mto_post_resource", indexes = {
        @Index(name = "IK_R_POST_ID", columnList = "post_id")
})
public class PostResource implements Serializable {
    private static final long serialVersionUID = -2343406058301647253L;

    /**
     * 主键ID
     * <p>自增主键，对应表 {@code mto_post_resource.id}，不可空。</p>
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
     * 资源ID
     * <p>对应列 {@code resource_id}，关联 {@code mto_resource.id}，指向资源主表记录。</p>
     */
    private long resourceId;

    /**
     * 图片路径
     */
    private String path;

    /**
     * 排序值
     * <p>对应列 {@code sort}，数据库默认 0；数值越小越靠前，用于文章内图片展示排序。</p>
     */
    @Column(name = "sort", columnDefinition = "int(11) NOT NULL DEFAULT '0'")
    private int sort;

}
