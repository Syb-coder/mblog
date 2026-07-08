package com.mtons.mblog.modules.entity;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * 角色-权限关联实体 —— 对应数据库表 shiro_role_permission
 *
 * <h3>业务含义</h3>
 * 这是角色和权限之间的"桥梁表"。Role 和 Permission 是多对多关系：
 * 一个角色可以拥有多个权限，一个权限也可以属于多个角色。
 * 例如："管理员"角色可能同时拥有 "admin:user:list"、"admin:post:delete" 等多个权限。
 * "普通用户"角色可能只有 "user:post:view" 等基础权限。
 *
 * <h3>完整 RBAC 链路</h3>
 * <pre>
 *   用户登录 → 查 UserRole 获取角色列表
 *           → 查 RolePermission 获取每个角色的权限列表
 *           → Shiro 将这些权限缓存起来
 *           → 用户访问资源时，Shiro 检查权限是否匹配
 * </pre>
 */
@Entity
@Table(name = "shiro_role_permission")
public class RolePermission implements Serializable {
    private static final long serialVersionUID = -5979636077649378677L;

    /**
     * 主键ID
     * <p>自增主键，对应表 {@code shiro_role_permission.id}，不可空。</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * 角色ID
     * <p>对应列 {@code role_id}，关联 {@code shiro_role.id}，标识所属角色。</p>
     */
    @Column(name = "role_id")
    private long roleId;


    /**
     * 权限ID
     * <p>对应列 {@code permission_id}，关联 {@code shiro_permission.id}，标识关联的权限。</p>
     */
    @Column(name = "permission_id")
    private long permissionId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(long permissionId) {
        this.permissionId = permissionId;
    }
}
