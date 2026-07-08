package com.mtons.mblog.modules.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * 角色实体 —— 对应数据库表 shiro_role
 *
 * <h3>业务含义</h3>
 * 角色是 RBAC 权限模型中的核心概念。一个角色代表一组权限的集合，
 * 例如"管理员"角色拥有所有权限，"普通用户"角色只有基础权限。
 * 用户的最终权限 = 用户所属所有角色的权限并集。
 *
 * <h3>系统内置角色</h3>
 * <ul>
 *   <li>ROLE_ADMIN = "admin" —— 管理员，ADMIN_ID = 1</li>
 *   <li>系统初始化时，schema.sql 会自动创建这个角色</li>
 * </ul>
 *
 * <h3>关联关系</h3>
 * Role (1) ←→ UserRole (N) ←→ User (N)     —— 用户-角色关联
 * Role (1) ←→ RolePermission (N) ←→ Permission (N) —— 角色-权限关联
 *
 * <h3>关键字段</h3>
 * <ul>
 *   <li>permissions —— @Transient，不持久化，仅用于业务层在内存中组装权限列表</li>
 *   <li>name —— 角色标识，如 "admin"，不可修改</li>
 *   <li>status —— 0=正常, 1=关闭</li>
 * </ul>
 */
@Entity
@Table(name = "shiro_role")
public class Role implements Serializable {
    private static final long serialVersionUID = -1153854616385727165L;

    /** 角色状态：正常 */
    public static int STATUS_NORMAL = 0;
    /** 角色状态：关闭 */
    public static int STATUS_CLOSED = 1;

    /** 内置角色名：管理员 */
    public static String ROLE_ADMIN = "admin";

    /** 内置管理员角色ID */
    public static long ADMIN_ID = 1;

    /**
     * 主键ID
     * <p>自增主键，对应表 {@code shiro_role.id}，不可空。</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * 角色名称
     * <p>对应列 {@code name}，不可空，长度 32；{@code updatable = false} 表示角色名创建后不可更新。</p>
     */
    @Column(nullable = false, updatable = false, length = 32)
    private String name;

    /**
     * 描述
     * <p>对应列 {@code description}，长度 140；角色的中文说明，便于后台展示。</p>
     */
    @Column(length = 140)
    private String description;

    /**
     * 状态
     * <p>对应列 {@code status}；0=正常（{@link #STATUS_NORMAL}），1=关闭（{@link #STATUS_CLOSED}）。</p>
     */
    private int status;

    /**
     * 权限列表
     * <p>{@link Transient} 标注表示不持久化到数据库，仅供业务层在装载角色时
     * 组装其拥有的权限集合。</p>
     */
    @Transient
    private List<Permission> permissions;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
