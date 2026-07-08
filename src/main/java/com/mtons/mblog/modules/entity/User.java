package com.mtons.mblog.modules.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息实体 —— 对应数据库表 mto_user
 *
 * <h3>业务含义</h3>
 * 这是系统的用户主表。用户通过 username + password 登录（密码用 MD5 加密存储）。
 * 一个用户可以有多个角色（通过 UserRole 中间表），每个角色又关联多个权限
 * （通过 RolePermission 中间表），构成经典的 RBAC（Role-Based Access Control）
 * 权限模型。
 *
 * <h3>关键字段说明</h3>
 * <ul>
 *   <li>posts / comments —— 冗余统计字段，记录用户发表的文章数和评论数。
 *       每次用户发表或删除文章/评论时，由 UserEventService 异步更新。
 *       采用冗余字段而非 SQL COUNT 的原因：减少 JOIN 查询的开销。</li>
 *   <li>status —— 用户状态。0=正常，1=禁用。
 *       禁用的用户无法登录系统。</li>
 *   <li>lastLogin —— 最后登录时间，由 UserServiceImpl.login() 在每次登录时更新。</li>
 * </ul>
 *
 * <h3>关联关系</h3>
 * User (1) ←→ UserRole (N) ←→ Role (N) ←→ RolePermission (N) ←→ Permission (N)
 *    ↑ 用户              ↑ 用户-角色         ↑ 角色               ↑ 角色-权限       ↑ 权限
 *
 * 一个用户可以有多个角色（多对多），一个角色可以有多个权限（多对多）。
 *
 * <h3>数据库表结构</h3>
 * <pre>
 * mto_user
 * ├── id          BIGINT       PK, 自增
 * ├── username    VARCHAR(64)  UNIQUE, NOT NULL  ← 登录账号
 * ├── password    VARCHAR(64)                      ← MD5 密文
 * ├── avatar      VARCHAR(255)                     ← 头像 URL
 * ├── name        VARCHAR(18)                      ← 显示昵称
 * ├── gender      INT                              ← 0=未知 1=男 2=女
 * ├── email       VARCHAR(64)  UNIQUE
 * ├── posts       INT                              ← 冗余：文章数
 * ├── comments    INT                              ← 冗余：评论数
 * ├── created     DATETIME
 * ├── last_login  DATETIME
 * ├── signature   VARCHAR(255)                     ← 个性签名
 * └── status      INT                              ← 0=正常 1=禁用
 * </pre>
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
