package com.mtons.mblog.modules.data;

import com.alibaba.fastjson2.annotation.JSONField;
import com.mtons.mblog.modules.entity.Comment;

import java.io.Serializable;
import java.util.Date;

/**
 * 评论视图对象 CommentVO。
 * <p>
 * 继承自 {@link Comment}，扩展出作者、父评论与所属文章等关联视图字段，
 * 同时覆盖 created 字段以定制 JSON 输出格式（yyyy-MM-dd）。
 * </p>
 *
 */
public class CommentVO extends Comment implements Serializable {
	private static final long serialVersionUID = 9192186139010913437L;

	@JSONField(format="yyyy-MM-dd")
	private Date created;

	/**
	 * 评论作者（扩展字段，非数据库列）
	 */
	private UserVO author;

	/**
	 * 父评论（扩展字段，用于层级展示）
	 */
	private CommentVO parent;

	/**
	 * 评论所属文章
	 */
	private PostVO post;

	public Date getCreated() {
		return super.getCreated();
	}

	/**
	 * 获取评论作者
	 * @return 评论作者
	 */
	public UserVO getAuthor() {
		return author;
	}

	/**
	 * 设置评论作者
	 * @param author 评论作者
	 */
	public void setAuthor(UserVO author) {
		this.author = author;
	}

	/**
	 * 获取父评论
	 * @return 父评论
	 */
	public CommentVO getParent() {
		return parent;
	}

	/**
	 * 设置父评论
	 * @param parent 父评论
	 */
	public void setParent(CommentVO parent) {
		this.parent = parent;
	}

	/**
	 * 获取评论所属文章
	 * @return 评论所属文章
	 */
	public PostVO getPost() {
		return post;
	}

	/**
	 * 设置评论所属文章
	 * @param post 评论所属文章
	 */
	public void setPost(PostVO post) {
		this.post = post;
	}
}
