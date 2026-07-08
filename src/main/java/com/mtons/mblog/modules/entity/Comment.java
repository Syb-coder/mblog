package com.mtons.mblog.modules.entity;

import jakarta.persistence.*;
import java.util.Date;

/**
 * 评论实体 —— 对应数据库表 mto_comment
 *
 * <h3>业务含义</h3>
 * 记录用户对文章的评论。支持"盖楼"形式的嵌套评论（通过 pid 指向父评论）。
 * pid = 0 表示这是一条顶级评论（直接回复文章的）。
 * pid ≠ 0 表示这是一条子评论（回复某条评论的）。
 *
 * <h3>关键字段</h3>
 * <ul>
 *   <li>pid —— 父评论 ID。0=顶级评论，非0=回复某条评论</li>
 *   <li>postId —— 所属文章 ID</li>
 *   <li>authorId —— 评论作者 ID，关联 User 表</li>
 *   <li>status —— 评论状态（0=正常，1=隐藏/删除）</li>
 * </ul>
 *
 * <h3>数据库表结构</h3>
 * <pre>
 * mto_comment
 * ├── id        BIGINT    PK, 自增
 * ├── pid       BIGINT                         ← 父评论ID(0=顶级)
 * ├── post_id   BIGINT    INDEX                ← 所属文章
 * ├── content   TEXT
 * ├── created   DATETIME
 * ├── author_id BIGINT                         ← 评论作者
 * └── status    INT                            ← 0=正常 1=删除
 * </pre>
 */
@Entity
@Table(name = "mto_comment", indexes = {
        @Index(name = "IK_POST_ID", columnList = "post_id")
})
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * 父评论ID
     */
    private long pid;

    /**
     * 所属内容ID
     */
    @Column(name = "post_id")
    private long postId;

    /**
     * 评论内容
     */
    @Column(name = "content")
    private String content;

    @Column(name = "created")
    private Date created;

    @Column(name = "author_id")
    private long authorId;

    private int status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
