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
 * 文章实体 —— 对应数据库表 mto_post
 *
 * <h3>业务含义</h3>
 * 这是博客系统的核心实体，每一条记录就是一篇文章。
 * 文章的内容（正文）被拆分到了 PostAttribute 表中（一对一），
 * 原因是正文可能很大（CLOB/TEXT 类型），而列表查询通常只需要 title/summary 等字段，
 * 拆分开来可以避免每次都加载大字段，提升列表查询性能。
 *
 * <h3>Hibernate 过滤器</h3>
 * 本类上标注了 @FilterDef 和 @Filter，定义了一个名为 "POST_STATUS_FILTER" 的过滤器，
 * 默认条件为 "status = 0"（即只显示正常状态的文章）。
 *
 * 这个过滤器不是自动生效的，需要配合 HibernateFilterAspect 使用：
 * 在 Service 层方法上标注 @PostStatusFilter 注解后，AOP 会在方法执行前
 * 自动启用当前 Session 的此过滤器。这样前端默认就看不到"已删除"的文章。
 *
 * <h3>关键字段说明</h3>
 * <ul>
 *   <li>channelId —— 文章所属频道/分类 ID，对应 Channel 表</li>
 *   <li>tags —— 逗号分隔的标签字符串，如 "Java,Spring Boot,博客"</li>
 *   <li>featured —— 是否推荐/加精（0=否, 1=是）</li>
 *   <li>weight —— 排序权重，数值越大越靠前，用于置顶功能</li>
 *   <li>status —— 文章状态（0=正常, 1=删除）</li>
 * </ul>
 *
 * <h3>关联关系</h3>
 * Post (1) ←→ PostAttribute (1)  一对一，共享主键
 * Post (1) ←→ PostTag (N)         中间表 → Tag (N)
 * Post (1) ←→ Comment (N)         一对多
 * Post (N) ←→ User (1)            多对一，通过 authorId 关联
 * Post (N) ←→ Channel (1)         多对一，通过 channelId 关联
 */
@Entity
@Table(name = "mto_post", indexes = {
		@Index(name = "IK_CHANNEL_ID", columnList = "channel_id")
})
/*
 * Hibernate 过滤器定义与使用
 *
 * @FilterDef(name = "POST_STATUS_FILTER") → 定义了一个名为 POST_STATUS_FILTER 的过滤器
 *   defaultCondition = "status = 0"       → 默认过滤条件：只查询 status = 0（正常）的文章
 *
 * @Filter(name = "POST_STATUS_FILTER")    → 在当前实体类（Post）上应用该过滤器
 *
 * 工作原理：
 * 配合 AOP 切面 @PostStatusFilter 使用，在 Service 层方法执行前，
 * 自动启用 Hibernate Session 上的此过滤器，后续所有 Post 查询都会
 * 自动带上 WHERE status = 0 条件，避免前端展示已删除的文章。
 *
 * 参考：modules/aspect/PostStatusFilter.java、HibernateFilterAspect.java
 */
@FilterDefs({
		@FilterDef(name = "POST_STATUS_FILTER", defaultCondition = "status = 0" )})
@Filters({ @Filter(name = "POST_STATUS_FILTER") })
public class Post implements Serializable {
	private static final long serialVersionUID = 7144425803920583495L;

	/**
	 * 主键 ID，使用数据库自增策略生成
	 * @Id 标记为主键
	 * @GeneratedValue(strategy = GenerationType.IDENTITY) 表示由数据库自动生成（自增）
	 * 每篇文章的唯一标识
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	/**
	 * 文章所属频道/分类 ID
	 * @Column(name = "channel_id") 映射到数据库的 channel_id 字段
	 * 对应 Channel 表的主键，表示这篇文章属于哪个频道（如技术、生活、问答等）
	 * Post (N) ←→ Channel (1) 多对一关系
	 */
	@Column(name = "channel_id", length = 5)
	private int channelId;

	/**
	 * 文章标题
	 * @Column(name = "title") 映射到数据库的 title 字段
	 * 最大长度 64 个字符
	 */
	@Column(name = "title", length = 64)
	private String title;

	/**
	 * 文章摘要/简介
	 * 用于列表页展示的文字摘要，最大长度 140 个字符
	 * 如果文章没有手动填写摘要，系统会自动截取正文前一段文字作为摘要
	 */
	@Column(length = 140)
	private String summary;

	/**
	 * 预览图/封面图 URL
	 * 文章在列表页展示的缩略图地址，最大长度 128 个字符
	 */
	@Column(length = 128)
	private String thumbnail;

	/**
	 * 标签，多个标签用逗号隔开
	 * 例如 "Java,Spring Boot,博客"，最大长度 64 个字符
	 * 用于文章分类检索和标签云展示
	 */
	@Column(length = 64)
	private String tags;

	/**
	 * 作者 ID
	 * @Column(name = "author_id") 映射到数据库的 author_id 字段
	 * 对应 User 表的主键，表示这篇文章的作者是谁
	 * Post (N) ←→ User (1) 多对一关系
	 */
	@Column(name = "author_id")
	private long authorId;

	/**
	 * 文章创建时间
	 * @Temporal(TemporalType.TIMESTAMP) 表示精确到日期的日期时间类型
	 * 对应数据库的 DATETIME/TIMESTAMP 类型
	 */
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date created;

	/**
	 * 评论数
	 * 冗余字段：记录这篇文章收到的评论总数
	 * 每次新增/删除评论时同步更新此值，避免每次都 COUNT 查询
	 */
	private int comments;

	/**
	 * 阅读数
	 * 记录文章的浏览总数（PV），每次访问时增加
	 */
	private int views;

	/**
	 * 文章状态：0=正常，1=删除
	 * 用于实现"逻辑删除"（软删除），即删除时不是真的 DELETE 掉数据，
	 * 而是把 status 设为 1，配合 Hibernate 过滤器自动过滤掉已删除的文章
	 */
	private int status;

	/**
	 * 推荐状态/加精：0=否，1=是
	 * 管理员可以将优质文章标记为"推荐"，推荐的文章会在首页等位置优先展示
	 */
	private int featured;

	/**
	 * 排序权重，数值越大越靠前
	 * 用于文章置顶功能：weight 大的文章在列表页排在前面
	 * 普通文章 weight = 0，置顶文章 weight > 0
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