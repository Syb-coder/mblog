package com.mtons.mblog.modules.data;

import com.alibaba.fastjson2.annotation.JSONField;
import com.mtons.mblog.modules.entity.Role;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户视图对象 UserVO。
 * <p>
 * 通过 {@link JSONField}(serialize = false) 标注禁止序列化输出，防止敏感数据泄露。
 * </p>
 *
 */
public class UserVO implements Serializable {
	private static final long serialVersionUID = 107193816173103116L;

	/**
	 * 用户ID
	 */
	private long id;

	/**
	 * 用户名（登录账号）
	 */
	private String username;

	/**
	 * 密码（不参与 JSON 序列化，避免敏感数据外泄）
	 */
	@JSONField(serialize = false)
	private String password;

	/**
	 * 头像URL
	 */
	private String avatar;

	/**
	 * 昵称
	 */
	private String name;

	/**
	 * 邮箱（不参与 JSON 序列化，避免敏感数据外泄）
	 */
	@JSONField(serialize = false)
	private String email;

	/**
	 * 文章数
	 */
	private int posts;

	/**
	 * 发布评论数
	 */
	private int comments;

	private Date created;

	private Date lastLogin;

	/**
	 * 个性签名
	 */
	private String signature;

	/**
	 * 用户状态
	 */
	private int status;

	/**
	 * 最后修改时间
	 */
	private Date updated;

	/**
	 * 角色列表（不参与 JSON 序列化，避免权限信息外泄）
	 */
	@JSONField(serialize = false)
	private List<Role> roles = new ArrayList<>();

	/**
	 * 获取用户ID
	 * @return 用户ID
	 */
	public long getId() {
		return id;
	}

	/**
	 * 设置用户ID
	 * @param id 用户ID
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * 获取用户名
	 * @return 用户名
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * 设置用户名
	 * @param username 用户名
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * 获取密码
	 * @return 密码
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * 设置密码
	 * @param password 密码
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 获取昵称
	 * @return 昵称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置昵称
	 * @param name 昵称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取邮箱
	 * @return 邮箱
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * 设置邮箱
	 * @param email 邮箱
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	/**
	 * 获取用户状态
	 * @return 用户状态
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * 设置用户状态
	 * @param status 用户状态
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * 获取头像URL
	 * @return 头像URL
	 */
	public String getAvatar() {
		return avatar;
	}

	/**
	 * 设置头像URL
	 * @param avatar 头像URL
	 */
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	/**
	 * 获取文章数
	 * @return 文章数
	 */
	public int getPosts() {
		return posts;
	}

	/**
	 * 设置文章数
	 * @param posts 文章数
	 */
	public void setPosts(int posts) {
		this.posts = posts;
	}

	/**
	 * 获取发布评论数
	 * @return 发布评论数
	 */
	public int getComments() {
		return comments;
	}

	/**
	 * 设置发布评论数
	 * @param comments 发布评论数
	 */
	public void setComments(int comments) {
		this.comments = comments;
	}

	/**
	 * 获取个性签名
	 * @return 个性签名
	 */
	public String getSignature() {
		return signature;
	}

	/**
	 * 设置个性签名
	 * @param signature 个性签名
	 */
	public void setSignature(String signature) {
		this.signature = signature;
	}

	/**
	 * 获取角色列表
	 * @return 角色列表
	 */
	public List<Role> getRoles() {
		return roles;
	}

	/**
	 * 设置角色列表
	 * @param roles 角色列表
	 */
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}
}
