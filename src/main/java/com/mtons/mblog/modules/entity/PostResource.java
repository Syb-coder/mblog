package com.mtons.mblog.modules.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * 文章-资源关联实体 —— 对应数据库表 mto_post_resource
 *
 * <h3>业务含义</h3>
 * 记录文章中使用了哪些上传的资源文件（图片），以及它们在文章中的排序和完整路径。
 * Post 和 Resource 是多对多关系：一篇文章可以包含多张图片，
 * 一张图片也可以被多篇文章使用（通过 MD5 去重）。
 *
 * {@code path} 字段存储图片在文章中的完整 URL 路径，
 * 而 Resource 表的 path 是文件存储的相对路径。
 * 两者不同：PostResource.path 包含了域名/主题路径等信息，用于直接在页面上显示。
 *
 * <h3>sort 字段</h3>
 * 控制图片在文章中出现的先后顺序。数值越小越靠前。
 * 目前系统主要通过解析文章正文中的 &lt;img&gt; 标签来提取图片，
 * sort 字段的值基于图片在正文中出现的顺序自动分配。
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
