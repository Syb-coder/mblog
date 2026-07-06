package com.mtons.mblog.modules.service.impl;

import com.mtons.mblog.modules.repository.UserRoleRepository;
import com.mtons.mblog.modules.entity.Role;
import com.mtons.mblog.modules.entity.UserRole;
import com.mtons.mblog.modules.service.RoleService;
import com.mtons.mblog.modules.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 用户角色关联 Service 实现
 * <p>
 * 关键依赖：
 * <ul>
 *   <li>{@link UserRoleRepository}：用户-角色关联 JPA 仓储</li>
 * </ul>
 * </p>
 *
 */
@Service
@Transactional(readOnly = true)
public class UserRoleServiceImpl implements UserRoleService {
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private RoleService roleService;

    @Override
    public List<Long> listRoleIds(long userId) {
        List<UserRole> list = userRoleRepository.findAllByUserId(userId);
        List<Long> roleIds = new ArrayList<>();
        if (null != list) {
            list.forEach(po -> roleIds.add(po.getRoleId()));
        }
        return roleIds;
    }

    @Override
    public List<Role> listRoles(long userId) {
        List<Long> roleIds = listRoleIds(userId);
        return new ArrayList<>(roleService.findByIds(new HashSet<>(roleIds)).values());
    }

    @Override
    public Map<Long, List<Role>> findMapByUserIds(List<Long> userIds) {
        List<UserRole> list = userRoleRepository.findAllByUserIdIn(userIds);
        Map<Long, Set<Long>> map = new HashMap<>();

        list.forEach(po -> {
            Set<Long> roleIds = map.computeIfAbsent(po.getUserId(), k -> new HashSet<>());
            roleIds.add(po.getRoleId());
        });

        Map<Long, List<Role>> ret = new HashMap<>();
        map.forEach((k, v) -> {
            ret.put(k, new ArrayList<>(roleService.findByIds(v).values()));
        });
        return ret;
    }

    /**
     * 修改用户角色授权
     * <p>策略：
     * <ul>
     *   <li>入参为空：清空该用户全部角色授权</li>
     *   <li>新增不在 exitIds 中的角色授权</li>
     * </ul>
     */
    @Override
    @Transactional
    public void updateRole(long userId, Set<Long> roleIds) {
        // 入参为空：清空全部角色授权
        if (null == roleIds || roleIds.isEmpty()) {
            userRoleRepository.deleteByUserId(userId);
        } else {
            List<UserRole> list = userRoleRepository.findAllByUserId(userId);
            List<Long> exitIds = new ArrayList<>();

            if (null != list) {
                list.forEach(po -> {
                    if (!roleIds.contains(po.getRoleId())) {
                        userRoleRepository.delete(po);
                    } else {
                        exitIds.add(po.getRoleId());
                    }
                });
            }

            // 新增不在已有角色中的新角色授权
            roleIds.stream().filter(id -> !exitIds.contains(id)).forEach(roleId -> {
                UserRole po = new UserRole();
                po.setUserId(userId);
                po.setRoleId(roleId);

                userRoleRepository.save(po);
            });
        }


    }
}
