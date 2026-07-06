package com.mtons.mblog.modules.entity;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * 角色权限映射 Entity
 * <p>
 * 对应数据库表 {@code shiro_role_permission}，维护角色与权限的多对多关系。
 * 业务含义：每条记录表示某角色拥有的一项权限，{@code role_id} 关联 {@link Role}，
 * {@code permission_id} 关联 {@link Permission}，构成 Shiro 鉴权模型的核心关联表。
 * </p>
 *
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
