// 包声明：角色服务实现类所在包
package com.sunblog.modules.service.impl;

// 导入权限实体类
import com.sunblog.modules.entity.Permission;
// 导入角色实体类
import com.sunblog.modules.entity.Role;
// 导入角色权限关联实体类
import com.sunblog.modules.entity.RolePermission;
// 导入用户角色关联实体类
import com.sunblog.modules.entity.UserRole;
// 导入权限仓储接口
import com.sunblog.modules.repository.PermissionRepository;
// 导入角色仓储接口
import com.sunblog.modules.repository.RoleRepository;
// 导入用户角色关联仓储接口
import com.sunblog.modules.repository.UserRoleRepository;
// 导入角色服务接口，本类实现该接口
import com.sunblog.modules.service.RoleService;
// 导入角色权限关联服务接口
import com.sunblog.modules.service.RolePermissionService;
// 导入 Apache Commons 字符串工具类
import org.apache.commons.lang3.StringUtils;
// 导入 Spring 自动注入注解
import org.springframework.beans.factory.annotation.Autowired;
// 导入 Spring Data 分页相关类
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
// 导入 Spring Service 注解
import org.springframework.stereotype.Service;
// 导入 Spring 事务注解
import org.springframework.transaction.annotation.Transactional;
// 导入 Spring 断言工具
import org.springframework.util.Assert;

// 导入 JPA Criteria 断言接口
import jakarta.persistence.criteria.Predicate;
// 导入 Java 工具类包
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
// @Service 注解：将该类注册为 Spring 业务服务组件
@Service
// @Transactional：类级别可写事务
@Transactional
// RoleServiceImpl：角色管理服务实现类
public class RoleServiceImpl implements RoleService {
    // @Autowired：Spring 自动注入角色仓储实例
    @Autowired
    // roleRepository：角色 JPA 仓储，提供角色表的 CRUD 操作
    private RoleRepository roleRepository;
    // @Autowired：Spring 自动注入权限仓储实例
    @Autowired
    // permissionRepository：权限 JPA 仓储（本类中未直接使用，保留以防扩展）
    private PermissionRepository permissionRepository;
    // @Autowired：Spring 自动注入角色权限关联服务实例
    @Autowired
    // rolePermissionService：角色权限关联服务，用于维护角色与权限的关联关系
    private RolePermissionService rolePermissionService;
    // @Autowired：Spring 自动注入用户角色关联仓储实例
    @Autowired
    // userRoleRepository：用户-角色关联仓储，用于删除前的占用校验
    private UserRoleRepository userRoleRepository;

    /**
     * <p>使用 JPA Criteria 动态拼接 name 模糊匹配条件。</p>
     */
    // @Override：实现接口方法
    @Override
    // paging：分页查询角色，支持按名称模糊搜索
    public Page<Role> paging(Pageable pageable, String name) {
        // 使用 JPA Criteria 动态构建查询条件
        Page<Role> page = roleRepository.findAll((root, query, builder) -> {
            // 创建空 conjunction 条件
            Predicate predicate = builder.conjunction();

            // 如果 name 参数非空，添加模糊匹配条件
            if (StringUtils.isNoneBlank(name)) {
                // 添加 LIKE 模糊查询条件
                predicate.getExpressions().add(
                        builder.like(root.get("name"), "%" + name + "%"));
            }

            // 按 id 倒序排序
            query.orderBy(builder.desc(root.get("id")));
            // 返回查询条件
            return predicate;
        }, pageable);
        // 返回分页结果
        return page;
    }

    // @Override：实现接口方法
    @Override
    // list：查询所有正常状态的角色列表
    public List<Role> list() {
        // 查询状态为正常（STATUS_NORMAL）的所有角色
        List<Role> list = roleRepository.findAllByStatus(Role.STATUS_NORMAL);
        // 返回角色列表
        return list;
    }

    // @Override：实现接口方法
    @Override
    // findByIds：根据 ID 集合批量查询角色，返回以 ID 为 key 的 Map
    public Map<Long, Role> findByIds(Set<Long> ids) {
        // 根据 ID 集合查询角色列表
        List<Role> list = roleRepository.findAllById(ids);
        // 创建以角色 ID 为 key 的有序映射
        Map<Long, Role> ret = new LinkedHashMap<>();
        // 遍历角色列表，将实体转为 VO（含权限明细）并存入映射
        list.forEach(po -> {
            // 将角色实体转为 VO
            Role vo = toVO(po);
            // 以角色 ID 为 key 存入映射
            ret.put(vo.getId(), vo);
        });
        // 返回角色映射
        return ret;
    }

    // @Override：实现接口方法
    @Override
    // get：根据 ID 查询角色详情（含权限明细）
    public Role get(long id) {
        // 查询角色实体并转为 VO
        return toVO(roleRepository.findById(id).get());
    }

    /**
     * <p>权限更新策略：先调用 {@link RolePermissionService#deleteByRoleId} 清空旧关联，
     */
    // @Override：实现接口方法
    @Override
    // update：更新角色基本信息及其权限关联，采用"先删后增"全量替换策略
    public void update(Role r, Set<Permission> permissions) {
        // 根据 ID 查询已有角色记录
        Optional<Role> optional = roleRepository.findById(r.getId());
        // 如果存在则使用已有记录，否则创建新记录
        Role po = optional.orElse(new Role());
        // 更新角色名称
            po.setName(r.getName());
        // 更新角色描述
        po.setDescription(r.getDescription());
        // 更新角色状态
        po.setStatus(r.getStatus());

        // 保存角色基本信息
        roleRepository.save(po);

        // 先清空旧的角色权限关联
        rolePermissionService.deleteByRoleId(po.getId());

        // 如果权限集合不为空
        if (permissions != null && permissions.size() > 0) {
            // 批量构建新的角色权限关联
            Set<RolePermission> rps = new HashSet<>();
            // 获取角色 ID
            long roleId = po.getId();
            // 遍历权限集合，构建关联实体
            permissions.forEach(p -> {
                // 创建角色权限关联实体
                RolePermission rp = new RolePermission();
                // 设置角色 ID
                rp.setRoleId(roleId);
                // 设置权限 ID
                rp.setPermissionId(p.getId());
                // 加入关联集合
                rps.add(rp);
            });

            // 批量写入新的角色权限关联
            rolePermissionService.add(rps);
        }
    }

    /**
     * 删除角色
     * <p>删除前校验角色是否已被用户授权使用，已被使用的角色不允许删除，避免悬挂授权。</p>
     * <p>级联清理：删除角色主体后同步清理角色-权限关联记录。</p>
     */
    // @Override：实现接口方法
    @Override
    // delete：删除角色，已被用户使用的角色不允许删除
    public boolean delete(long id) {
        // 查询使用该角色的用户角色关联记录
        List<UserRole> urs = userRoleRepository.findAllByRoleId(id);
        // 断言该角色未被任何用户使用，否则抛出异常阻止删除
        Assert.state(urs == null || urs.size() == 0, "该角色已经被使用,不能被删除");
        // 删除角色主体
        roleRepository.deleteById(id);
        // 级联清理角色权限关联，避免悬挂关联记录
        rolePermissionService.deleteByRoleId(id);
        // 返回删除成功
        return true;
    }

    /**
     * 激活或停用角色
     * <p>停用后角色不会被分配给新用户，但已授权用户保留授权关系。</p>
     */
    // @Override：实现接口方法
    @Override
    // activate：激活或停用角色
    public void activate(long id, boolean active) {
        // 根据 ID 查询角色记录
        Role po = roleRepository.findById(id).get();
        // 根据参数设置角色状态：true 为正常，false 为停用
        po.setStatus(active ? Role.STATUS_NORMAL : Role.STATUS_CLOSED);
    }

    /**
     * 实体转 VO（含权限明细）
     */
    // toVO：将角色实体转为 VO，包含权限明细列表
    private Role toVO(Role po) {
        // 创建角色 VO 对象
        Role r = new Role();
        // 拷贝 ID
        r.setId(po.getId());
        // 拷贝名称
        r.setName(po.getName());
        // 拷贝描述
        r.setDescription(po.getDescription());
        // 拷贝状态
        r.setStatus(po.getStatus());

        // 查询该角色的权限明细列表并设置到 VO 中
        r.setPermissions(rolePermissionService.findPermissions(r.getId()));
        // 返回角色 VO
        return r;
    }
}