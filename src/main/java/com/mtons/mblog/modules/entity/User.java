package com.mtons.mblog.modules.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息 Entity
 * <p>
 * 业务含义：用户主表，{@code posts}、{@code comments} 为冗余统计字段，
 * 通过 {@link UserRole} 关联到角色，构成“用户-角色-权限”鉴权模型。
 * </p>
 *
 * <p>关键约束：
 * <ul>
 *   <li>{@code username} 不可空且唯一，长度 64；登录账号业务键。</li>
 *   <li>{@code email} 唯一，长度 64；用于找回密码与通知。</li>
 *   <li>{@code status} 0=正常，其他值表示冻结/禁用等异常状态。</li>
 * </ul>
 * </p>
 *
 */
@Entity
@Table(name = "mto_user")
public class User implements Serializable {
	private static final long serialVersionUID = -3629784071225214858L;

	/**
	 * 主键ID
	 * <p>自增主键，对应表 {@code mto_user.id}，不可空。</p>
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	/**
	 * 用户名
	 * <p>对应列 {@code username}，不可空且唯一，长度 64；登录账号业务键。</p>
	 */
	@Column(name = "username", unique = true, nullable = false, length = 64)
	private String username;

	/**
	 * 密码(密文)
	 */
	@Column(name = "password", length = 64)
	private String password;

	/**
	 * 头像
	 */
	private String avatar;

	/**
	 * 昵称
	 * <p>对应列 {@code name}，长度 18；前台展示的显示名。</p>
	 */
	@Column(name = "name", length = 18)
	private String name;

	/**
	 * 性别
	 * <p>对应列 {@code gender}；0=未知/保密，1=男，2=女（具体取值以前台约定为准）。</p>
	 */
	private int gender;

	/**
	 * 邮箱
	 * <p>对应列 {@code email}，唯一，长度 64；用于找回密码与系统通知。</p>
	 */
	@Column(name = "email", unique = true, length = 64)
	private String email;

	/**
	 * 文章数
	 * <p>对应列 {@code posts}；冗余统计字段，记录用户发表的文章总数。</p>
	 */
	private int posts;

	/**
	 * 发布评论数
	 * <p>对应列 {@code comments}；冗余统计字段，记录用户发表的评论总数。</p>
	 */
	private int comments;

	private Date created;

	@Column(name = "last_login")
	private Date lastLogin;

	/**
	 * 个性签名
	 * <p>对应列 {@code signature}；用户自定义的个性签名，前台用户主页展示。</p>
	 */
	private String signature;

	/**
	 * 用户状态
	 * <p>对应列 {@code status}；0=正常，其他值表示冻结/禁用等异常状态。</p>
	 */
	private int status;

	public User() {

	}

	public User(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getPosts() {
		return posts;
	}

	public void setPosts(int posts) {
		this.posts = posts;
	}

	public int getComments() {
		return comments;
	}

	public void setComments(int comments) {
		this.comments = comments;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
}
