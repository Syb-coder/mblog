package com.mtons.mblog.modules.entity;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * 文章正文属性实体 —— 对应数据库表 mto_post_attribute
 *
 * <h3>业务含义</h3>
 * 本表与 Post 表是一对一关系，但共享同一个主键值（Post 的 id 就是本表的 id）。
 * 这样做的好处是：查询文章列表时不需要加载冗长的正文内容，
 * 只有在用户点开某篇文章详情时，才会真正去查这条记录。
 *
 * <h3>为什么拆表？</h3>
 * 文章正文（content）通常是 TEXT 甚至 LONGTEXT 类型，可能包含数万字符。
 * 如果把它放在 Post 表里，每次查询列表都要连带加载这个大字段，效率很低。
 * Spring Data JPA 的 LAZY 加载配合拆表，可以做到"按需加载"。
 * 实际上这里的 LAZY 是对"关联实体"的懒加载，而非对"同一个实体中的字段"的懒加载。
 *
 * <h3>编辑器类型</h3>
 * editor 字段标识用户使用哪种编辑器创作本文：
 * - "markdown" → Markdown 编辑器（前端用 marked.js 渲染）
 * - "tinymce"  → TinyMCE 富文本编辑器（已从当前版本中移除，但为了兼容旧数据仍保留）
 *
 * <h3>数据库表结构</h3>
 * <pre>
 * mto_post_attribute
 * ├── id       BIGINT    PK, FK → mto_post.id  ← 与文章表共享主键
 * ├── editor   VARCHAR(16)        DEFAULT 'markdown'
 * └── content  TEXT               ← 文章正文（懒加载）
 * </pre>
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
