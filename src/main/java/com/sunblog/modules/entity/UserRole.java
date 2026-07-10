package com.sunblog.modules.entity;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * 用户-角色关联实体 —— 对应数据库表 shiro_user_role
 *
 * <h3>业务含义</h3>
 * 这是用户和角色之间的"桥梁表"（多对多关联的中间表）。
 * 每一条记录表示"某个用户拥有某个角色"。
 * 例如：用户 ID=1 可能同时拥有 role_id=1（管理员）和 role_id=2（普通用户）。
 * 用户的最终权限是其所拥有角色的权限并集。
 *
 * <h3>为什么需要这个中间表？</h3>
 * User 和 Role 是多对多关系：一个用户可以有多个角色，一个角色也可以属于多个用户。
 * 在关系数据库中，多对多关系需要一张中间表来存储关联关系。
 */
@Entity
@Table(name = "shiro_user_role")
public class UserRole implements Serializable {
	private static final long serialVersionUID = -2908144287976184011L;

	/**
	 * 主键ID
	 * <p>自增主键，对应表 {@code shiro_user_role.id}，不可空。</p>
	 */
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	/**
	 * 用户ID
	 * <p>对应列 {@code user_id}，关联 {@code mto_user.id}，标识所属用户。</p>
	 */
	@Column(name = "user_id")
	private Long userId;

	/**
	 * 角色ID
	 * <p>对应列 {@code role_id}，关联 {@code shiro_role.id}，标识关联的角色。</p>
	 */
	@Column(name = "role_id")
    private Long roleId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
}
