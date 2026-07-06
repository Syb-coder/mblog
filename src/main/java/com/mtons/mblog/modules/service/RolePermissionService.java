package com.mtons.mblog.modules.service;

import com.mtons.mblog.modules.entity.Permission;
import com.mtons.mblog.modules.entity.RolePermission;

import java.util.List;
import java.util.Set;

/**
 * 角色权限关联 Service
 * <p>
 * </p>
 *
 * @create - 2018/5/18
 */
public interface RolePermissionService {
    /* *
     * @param roleId 角色 ID
     * @return 权限列表，无授权时返回 null
     */
    List<Permission> findPermissions(long roleId);

    /**
     * 删除指定角色的全部权限关联（用于角色重新授权前的清空）
     *
     * @param roleId 角色 ID
     */
    void deleteByRoleId(long roleId);

    /**
     * 批量新增角色权限关联
     *
     * @param rolePermissions 角色权限关联集合
     */
    void add(Set<RolePermission> rolePermissions);

}
