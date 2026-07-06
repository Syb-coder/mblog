package com.mtons.mblog.modules.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * 角色 Entity
 * <p>
 * 对应数据库表 {@code shiro_role}，存储系统角色定义。
 * 业务含义：Shiro 角色实体，通过 {@link UserRole} 关联到用户，
 * 通过 {@link RolePermission} 关联到权限，构成“用户-角色-权限”鉴权模型。
 * </p>
 *
 * <p>关键约束：
 * <ul>
 *   <li>{@code name} 不可空且 {@code updatable = false}，长度 32；角色创建后名称不可修改。</li>
 *   <li>{@code permissions} 使用 {@link Transient} 标注，不持久化到数据库，
 *       仅用于业务层组装角色的权限列表。</li>
 *   <li>类内静态常量定义角色状态与内置角色名，供全局复用。</li>
 * </ul>
 * </p>
 *
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
