package com.mtons.mblog.modules.service.impl;

import com.mtons.mblog.modules.entity.Permission;
import com.mtons.mblog.modules.entity.Role;
import com.mtons.mblog.modules.entity.RolePermission;
import com.mtons.mblog.modules.entity.UserRole;
import com.mtons.mblog.modules.repository.PermissionRepository;
import com.mtons.mblog.modules.repository.RoleRepository;
import com.mtons.mblog.modules.repository.UserRoleRepository;
import com.mtons.mblog.modules.service.RoleService;
import com.mtons.mblog.modules.service.RolePermissionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.persistence.criteria.Predicate;
import java.util.*;

/**
 * 角色管理 Service 实现
 * <p>
 * 关键依赖：
 * <ul>
 *   <li>{@link RoleRepository}：角色 JPA 仓储</li>
 *   <li>{@link RolePermissionService}：角色权限关联维护</li>
 *   <li>{@link UserRoleRepository}：用户-角色关联仓储（用于删除前的占用校验）</li>
 * </ul>
 * </p>
 * <p>
 * 事务策略：类级别 {@code @Transactional} 可写事务；权限关联更新采用"先删后增"全量替换策略。
 * </p>
 *
 */
@Service
@Transactional
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private RolePermissionService rolePermissionService;
    @Autowired
    private UserRoleRepository userRoleRepository;

    /**
     * <p>使用 JPA Criteria 动态拼接 name 模糊匹配条件。</p>
     */
    @Override
    public Page<Role> paging(Pageable pageable, String name) {
        Page<Role> page = roleRepository.findAll((root, query, builder) -> {
            Predicate predicate = builder.conjunction();

            if (StringUtils.isNoneBlank(name)) {
                predicate.getExpressions().add(
                        builder.like(root.get("name"), "%" + name + "%"));
            }

            query.orderBy(builder.desc(root.get("id")));
            return predicate;
        }, pageable);
        return page;
    }

    @Override
    public List<Role> list() {
        List<Role> list = roleRepository.findAllByStatus(Role.STATUS_NORMAL);
        return list;
    }

    @Override
    public Map<Long, Role> findByIds(Set<Long> ids) {
        List<Role> list = roleRepository.findAllById(ids);
        Map<Long, Role> ret = new LinkedHashMap<>();
        list.forEach(po -> {
            Role vo = toVO(po);
            ret.put(vo.getId(), vo);
        });
        return ret;
    }

    @Override
    public Role get(long id) {
        return toVO(roleRepository.findById(id).get());
    }

    /**
     * <p>权限更新策略：先调用 {@link RolePermissionService#deleteByRoleId} 清空旧关联，
     */
    @Override
    public void update(Role r, Set<Permission> permissions) {
        Optional<Role> optional = roleRepository.findById(r.getId());
        Role po = optional.orElse(new Role());
            po.setName(r.getName());
        po.setDescription(r.getDescription());
        po.setStatus(r.getStatus());

        roleRepository.save(po);

        // 先清空旧的角色权限关联
        rolePermissionService.deleteByRoleId(po.getId());

        if (permissions != null && permissions.size() > 0) {
            // 批量构建新的角色权限关联并写入
            Set<RolePermission> rps = new HashSet<>();
            long roleId = po.getId();
            permissions.forEach(p -> {
                RolePermission rp = new RolePermission();
                rp.setRoleId(roleId);
                rp.setPermissionId(p.getId());
                rps.add(rp);
            });

            rolePermissionService.add(rps);
        }
    }

    /**
     * 删除角色
     * <p>删除前校验角色是否已被用户授权使用，已被使用的角色不允许删除，避免悬挂授权。</p>
     * <p>级联清理：删除角色主体后同步清理角色-权限关联记录。</p>
     */
    @Override
    public boolean delete(long id) {
        List<UserRole> urs = userRoleRepository.findAllByRoleId(id);
        Assert.state(urs == null || urs.size() == 0, "该角色已经被使用,不能被删除");
        roleRepository.deleteById(id);
        // 级联清理角色权限关联，避免悬挂关联记录
        rolePermissionService.deleteByRoleId(id);
        return true;
    }

    /**
     * 激活或停用角色
     * <p>停用后角色不会被分配给新用户，但已授权用户保留授权关系。</p>
     */
    @Override
    public void activate(long id, boolean active) {
        Role po = roleRepository.findById(id).get();
        po.setStatus(active ? Role.STATUS_NORMAL : Role.STATUS_CLOSED);
    }

    /**
     * 实体转 VO（含权限明细）
     */
    private Role toVO(Role po) {
        Role r = new Role();
        r.setId(po.getId());
        r.setName(po.getName());
        r.setDescription(po.getDescription());
        r.setStatus(po.getStatus());

        r.setPermissions(rolePermissionService.findPermissions(r.getId()));
        return r;
    }
}
