// 包声明：角色权限关联服务实现类所在包
package com.sunblog.modules.service.impl;

// 导入权限仓储接口，用于查询权限实体
import com.sunblog.modules.repository.PermissionRepository;
// 导入角色权限关联仓储接口，提供关联表的 CRUD 操作
import com.sunblog.modules.repository.RolePermissionRepository;
// 导入权限实体类
import com.sunblog.modules.entity.Permission;
// 导入角色权限关联实体类
import com.sunblog.modules.entity.RolePermission;
// 导入角色权限关联服务接口，本类实现该接口
import com.sunblog.modules.service.RolePermissionService;
// 导入 Spring 自动注入注解
import org.springframework.beans.factory.annotation.Autowired;
// 导入 Spring Service 注解
import org.springframework.stereotype.Service;
// 导入 Spring 事务注解
import org.springframework.transaction.annotation.Transactional;

// 导入 HashSet 集合类，用于收集权限 ID
import java.util.HashSet;
// 导入 List 列表接口
import java.util.List;
// 导入 Set 集合接口
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
// @Service 注解：将该类注册为 Spring 业务服务组件
@Service
// RolePermissionServiceImpl：角色权限关联服务实现类
public class RolePermissionServiceImpl implements RolePermissionService {
    // @Autowired：Spring 自动注入权限仓储实例
    @Autowired
    // permissionRepository：权限 JPA 仓储，用于根据 ID 批量查询权限实体
    private PermissionRepository permissionRepository;
    // @Autowired：Spring 自动注入角色权限关联仓储实例
    @Autowired
    // rolePermissionRepository：角色权限关联 JPA 仓储，提供关联表的 CRUD 操作
    private RolePermissionRepository rolePermissionRepository;

    /**
     * 避免直接 JOIN 带来的 N+1 性能问题。</p>
     */
    // @Override：实现接口方法
    @Override
    // @Transactional(readOnly = true)：只读事务，提升查询性能
    @Transactional(readOnly = true)
    // findPermissions：查询指定角色拥有的全部权限列表，分两步查询避免 JOIN 的 N+1 问题
    public List<Permission> findPermissions(long roleId) {
        // 第一步：根据角色 ID 查询所有角色权限关联记录
        List<RolePermission> rps = rolePermissionRepository.findAllByRoleId(roleId);

        // 初始化返回结果为 null（无授权时返回 null）
        List<Permission> rets = null;
        // 如果关联记录不为空
        if (rps != null && rps.size() > 0) {
            // 收集所有权限 ID 到 Set 中
            Set<Long> pids = new HashSet<>();
            // 遍历关联记录，提取权限 ID
            rps.forEach(rp -> pids.add(rp.getPermissionId()));
            // 第二步：根据权限 ID 集合批量查询权限实体
            rets = permissionRepository.findAllById(pids);
        }
        // 返回权限列表（可能为 null）
        return rets;
    }

    /**
     * 删除指定角色的全部权限关联
     * <p>用于角色重新授权前的清空，配合 {@link #add} 实现权限关联的全量替换。</p>
     */
    // @Override：实现接口方法
    @Override
    // @Transactional：可写事务，删除操作需要事务保护
    @Transactional
    // deleteByRoleId：清空指定角色的所有权限关联
    public void deleteByRoleId(long roleId) {
        // 调用仓储删除指定角色的全部关联记录
        rolePermissionRepository.deleteByRoleId(roleId);
    }

    /**
     * 批量新增角色权限关联
     * <p>使用 {@link RolePermissionRepository#saveAll(Iterable)} 批量写入，减少 SQL 执行次数。</p>
     */
    // @Override：实现接口方法
    @Override
    // @Transactional：可写事务，批量写入需要事务保护
    @Transactional
    // add：批量写入角色权限关联记录
    public void add(Set<RolePermission> rolePermissions) {
        // 调用仓储的 saveAll 批量写入，减少 SQL 执行次数
        rolePermissionRepository.saveAll(rolePermissions);
    }
}