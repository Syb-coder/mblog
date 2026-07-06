package com.mtons.mblog.modules.entity;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * 文章正文属性 Entity
 * <p>
 * 业务含义：与 {@link Post} 一对一关联（主键即 {@code post_id}），将大字段拆出独立表，
 * </p>
 *
 * <p>关键约束：
 * <ul>
 *   <li>{@code id} 同时作为主键与外键，对应 {@code mto_post.id}，无独立自增。</li>
 *   <li>{@code editor} 默认 {@code markdown}，标识正文所用编辑器类型。</li>
 *   <li>{@code content} 使用 {@link Lob} + {@code TEXT} 列类型，{@link Basic#fetch()} = {@link FetchType#LAZY}
 * </ul>
 * </p>
 *
 */
@Entity
@Table(name = "mto_post_attribute")
public class PostAttribute implements Serializable {
	private static final long serialVersionUID = 7829351358884064647L;

	/**
	 * 主键ID
	 * <p>对应表 {@code mto_post_attribute.id}，同时作为外键关联 {@code mto_post.id}，
	 * 与文章主表共享主键（无独立自增策略）。</p>
	 */
	@Id
    private long id;

	/**
	 * 编辑器类型
	 * <p>对应列 {@code editor}，长度 16，默认 {@code markdown}；标识正文所用的编辑器
	 * （如 {@code markdown}、{@code html}）。</p>
	 */
	@Column(length = 16, columnDefinition = "varchar(16) default 'markdown'")
    private String editor;

    /**
     * 内容
     * <p>对应列 {@code content}，类型为 {@code TEXT}；{@link Lob} 标注为大字段，
     * {@link Basic#fetch()} = {@link FetchType#LAZY} 表示懒加载，
     */
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "TEXT")
    private String content; // 内容

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
