package com.mtons.mblog.modules.entity;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.Filters;

import jakarta.persistence.Index;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 内容表 Entity，对应数据库 mto_post 表。
 * 通过 POST_STATUS_FILTER 过滤器，默认仅暴露 status = 0 的有效记录。
 */
@Entity
@Table(name = "mto_post", indexes = {
		@Index(name = "IK_CHANNEL_ID", columnList = "channel_id")
})
@FilterDefs({
		@FilterDef(name = "POST_STATUS_FILTER", defaultCondition = "status = 0" )})
@Filters({ @Filter(name = "POST_STATUS_FILTER") })
public class Post implements Serializable {
	private static final long serialVersionUID = 7144425803920583495L;

	/**
	 * 主键ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "channel_id", length = 5)
	private int channelId;

	/**
	 * 标题
	 */
	@Column(name = "title", length = 64)
	private String title;

	/**
	 * 摘要
	 */
	@Column(length = 140)
	private String summary;

	/**
	 * 预览图
	 */
	@Column(length = 128)
	private String thumbnail;

	/**
	 * 标签, 多个逗号隔开
	 */
	@Column(length = 64)
	private String tags;

	/**
	 * 作者Id
	 */
	@Column(name = "author_id")
	private long authorId;

	@Temporal(value = TemporalType.TIMESTAMP)
	private Date created;

	/**
	 * 评论数
	 */
	private int comments;

	/**
	 * 阅读数
	 */
	private int views;

	/**
	 * 文章状态
	 */
	private int status;

	/**
	 * 推荐状态
	 */
	private int featured;

	/**
	 * 排序值
	 */
	private int weight;

	/**
	 * 获取主键ID
	 * @return 主键ID
	 */
	public long getId() {
		return id;
	}

	/**
	 * 设置主键ID
	 * @param id 主键ID
	 */
	public void setId(long id) {
		this.id = id;
	}

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	/**
	 * 获取标题
	 * @return 标题
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 设置标题
	 * @param title 标题
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 获取摘要
	 * @return 摘要
	 */
	public String getSummary() {
		return summary;
	}

	/**
	 * 设置摘要
	 * @param summary 摘要
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}

	/**
	 * 获取标签
	 * @return 标签
	 */
	public String getTags() {
		return tags;
	}

	/**
	 * 设置标签
	 * @param tags 标签
	 */
	public void setTags(String tags) {
		this.tags = tags;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * 获取作者ID
	 * @return 作者ID
	 */
	public long getAuthorId() {
		return authorId;
	}

	/**
	 * 设置作者ID
	 * @param authorId 作者ID
	 */
	public void setAuthorId(long authorId) {
		this.authorId = authorId;
	}

	/**
	 * 获取文章状态
	 * @return 文章状态
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * 设置文章状态
	 * @param status 文章状态
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * 获取推荐状态
	 * @return 推荐状态
	 */
	public int getFeatured() {
		return featured;
	}

	/**
	 * 设置推荐状态
	 * @param featured 推荐状态
	 */
	public void setFeatured(int featured) {
		this.featured = featured;
	}

	/**
	 * 获取收藏数
	 * @return 收藏数
	 */
	/**
	 * 获取评论数
	 * @return 评论数
	 */
	public int getComments() {
		return comments;
	}

	/**
	 * 设置评论数
	 * @param comments 评论数
	 */
	public void setComments(int comments) {
		this.comments = comments;
	}

	/**
	 * 获取阅读数
	 * @return 阅读数
	 */
	public int getViews() {
		return views;
	}

	/**
	 * 设置阅读数
	 * @param views 阅读数
	 */
	public void setViews(int views) {
		this.views = views;
	}

	/**
	 * 获取排序值
	 * @return 排序值
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * 设置排序值
	 * @param weight 排序值
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}

	/**
	 * 获取预览图
	 * @return 预览图
	 */
	public String getThumbnail() {
		return thumbnail;
	}

	/**
	 * 设置预览图
	 * @param thumbnail 预览图
	 */
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
}
