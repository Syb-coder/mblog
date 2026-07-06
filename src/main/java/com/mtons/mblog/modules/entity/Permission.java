package com.mtons.mblog.modules.entity;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * 权限 Entity
 * <p>
 * 业务含义：定义系统权限点（权限值），供 {@link RolePermission} 关联到角色，
 * 由 Shiro 在鉴权时根据权限值匹配资源访问控制。
 * </p>
 *
 * <p>关键约束：
 * <ul>
 *       （如 {@code admin:user:list}）。</li>
 *   <li>{@code parent_id} 对应列 {@code parent_id}，{@code updatable = false} 表示创建后不可更新，
 *       用于权限树层级关联。</li>
 * </ul>
 * </p>
 *
 */
@Entity
@Table(name = "shiro_permission")
public class Permission implements Serializable {
    private static final long serialVersionUID = -5979636077639378677L;

    /**
     * 主键ID
     * <p>自增主键，对应表 {@code shiro_permission.id}，不可空。</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * 父权限ID
     * <p>对应列 {@code parent_id}；{@code updatable = false} 表示该字段一旦创建即不可更新，
     * 用于权限树层级结构（顶级权限为 0）。</p>
     */
    @Column(name = "parent_id", updatable = false)
    private long parentId;

    /**
     * 权限值
     * 供 Shiro 鉴权时匹配（如 {@code admin:user:list}）。</p>
     */
    @Column(nullable = false, unique = true, length = 32)
    private String name;

    /**
     * 描述
     * <p>对应列 {@code description}，长度 140；权限的中文说明，便于后台展示。</p>
     */
    @Column(length = 140)
    private String description;

    /**
     * 排序值
     * <p>对应列 {@code weight}；数值越大越靠前，用于权限树展示排序。</p>
     */
    private int weight;

    /**
     * <p>对应列 {@code version}；{@link Version} 注解启用 JPA 乐观锁，并发更新时自动校验。</p>
     */
    @Version
    private Integer version;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
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

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

}
