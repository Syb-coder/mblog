package com.mtons.mblog.modules.service.impl;

import com.mtons.mblog.modules.repository.PermissionRepository;
import com.mtons.mblog.modules.repository.RolePermissionRepository;
import com.mtons.mblog.modules.entity.Permission;
import com.mtons.mblog.modules.entity.RolePermission;
import com.mtons.mblog.modules.service.RolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 角色权限关联 Service 实现
 * <p>
 * 关键依赖：
 * <ul>
 *   <li>{@link RolePermissionRepository}：角色-权限关联仓储</li>
 * </ul>
 * </p>
 *
 * @create - 2018/5/18
 */
@Service
public class RolePermissionServiceImpl implements RolePermissionService {
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    /**
     * 避免直接 JOIN 带来的 N+1 性能问题。</p>
     */
    @Override
    @Transactional(readOnly = true)
    public List<Permission> findPermissions(long roleId) {
        List<RolePermission> rps = rolePermissionRepository.findAllByRoleId(roleId);

        List<Permission> rets = null;
        if (rps != null && rps.size() > 0) {
            Set<Long> pids = new HashSet<>();
            rps.forEach(rp -> pids.add(rp.getPermissionId()));
            rets = permissionRepository.findAllById(pids);
        }
        return rets;
    }

    /**
     * 删除指定角色的全部权限关联
     * <p>用于角色重新授权前的清空，配合 {@link #add} 实现权限关联的全量替换。</p>
     */
    @Override
    @Transactional
    public void deleteByRoleId(long roleId) {
        rolePermissionRepository.deleteByRoleId(roleId);
    }

    /**
     * 批量新增角色权限关联
     * <p>使用 {@link RolePermissionRepository#saveAll(Iterable)} 批量写入，减少 SQL 执行次数。</p>
     */
    @Override
    @Transactional
    public void add(Set<RolePermission> rolePermissions) {
        rolePermissionRepository.saveAll(rolePermissions);
    }
}
