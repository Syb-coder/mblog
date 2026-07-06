package com.mtons.mblog.modules.entity;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * 用户角色映射 Entity
 * <p>
 * 对应数据库表 {@code shiro_user_role}，维护用户与角色的多对多关系。
 * 业务含义：每条记录表示某用户拥有的一个角色，{@code user_id} 关联 {@link User}，
 * {@code role_id} 关联 {@link Role}，是 Shiro 鉴权模型中“用户-角色”关联的核心表。
 * </p>
 *
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
