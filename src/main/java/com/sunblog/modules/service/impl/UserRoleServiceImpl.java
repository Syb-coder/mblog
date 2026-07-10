// 包声明：用户角色关联服务实现类所在包
package com.sunblog.modules.service.impl;

// 导入用户角色关联仓储接口
import com.sunblog.modules.repository.UserRoleRepository;
// 导入角色实体类
import com.sunblog.modules.entity.Role;
// 导入用户角色关联实体类
import com.sunblog.modules.entity.UserRole;
// 导入角色服务接口，用于查询角色详情
import com.sunblog.modules.service.RoleService;
// 导入用户角色关联服务接口，本类实现该接口
import com.sunblog.modules.service.UserRoleService;
// 导入 Spring 自动注入注解
import org.springframework.beans.factory.annotation.Autowired;
// 导入 Spring Service 注解
import org.springframework.stereotype.Service;
// 导入 Spring 事务注解
import org.springframework.transaction.annotation.Transactional;

// 导入 Java 工具类包
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
// @Service 注解：将该类注册为 Spring 业务服务组件
@Service
// @Transactional(readOnly = true)：类级别只读事务
@Transactional(readOnly = true)
// UserRoleServiceImpl：用户角色关联服务实现类
public class UserRoleServiceImpl implements UserRoleService {
    // @Autowired：Spring 自动注入用户角色关联仓储实例
    @Autowired
    // userRoleRepository：用户-角色关联 JPA 仓储，提供关联表的 CRUD 操作
    private UserRoleRepository userRoleRepository;
    // @Autowired：Spring 自动注入角色服务实例
    @Autowired
    // roleService：角色服务，用于根据角色 ID 查询角色详情
    private RoleService roleService;

    // @Override：实现接口方法
    @Override
    // listRoleIds：查询指定用户的角色 ID 列表
    public List<Long> listRoleIds(long userId) {
        // 根据 userId 查询所有用户角色关联记录
        List<UserRole> list = userRoleRepository.findAllByUserId(userId);
        // 创建角色 ID 返回列表
        List<Long> roleIds = new ArrayList<>();
        // 如果关联记录不为空
        if (null != list) {
            // 遍历关联记录，提取角色 ID
            list.forEach(po -> roleIds.add(po.getRoleId()));
        }
        // 返回角色 ID 列表
        return roleIds;
    }

    // @Override：实现接口方法
    @Override
    // listRoles：查询指定用户的角色详情列表
    public List<Role> listRoles(long userId) {
        // 先获取用户的角色 ID 列表
        List<Long> roleIds = listRoleIds(userId);
        // 通过角色服务批量查询角色详情，转为列表返回
        return new ArrayList<>(roleService.findByIds(new HashSet<>(roleIds)).values());
    }

    // @Override：实现接口方法
    @Override
    // findMapByUserIds：批量查询多用户的角色信息，返回以用户 ID 为 key 的 Map
    public Map<Long, List<Role>> findMapByUserIds(List<Long> userIds) {
        // 根据 userIds 批量查询所有用户角色关联记录
        List<UserRole> list = userRoleRepository.findAllByUserIdIn(userIds);
        // 创建以用户 ID 为 key、角色 ID Set 为 value 的中间映射
        Map<Long, Set<Long>> map = new HashMap<>();

        // 遍历关联记录，按用户 ID 分组收集角色 ID
        list.forEach(po -> {
            // 使用 computeIfAbsent 确保每个用户有一个 Set 容器
            Set<Long> roleIds = map.computeIfAbsent(po.getUserId(), k -> new HashSet<>());
            // 将角色 ID 加入对应用户的 Set 中
            roleIds.add(po.getRoleId());
        });

        // 创建最终返回映射：key 为用户 ID，value 为角色详情列表
        Map<Long, List<Role>> ret = new HashMap<>();
        // 遍历中间映射，将角色 ID 替换为角色详情对象
        map.forEach((k, v) -> {
            // 通过角色服务批量查询角色详情，转为列表存入最终映射
            ret.put(k, new ArrayList<>(roleService.findByIds(v).values()));
        });
        // 返回多用户角色映射
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
    // @Override：实现接口方法
    @Override
    // @Transactional：可写事务，修改授权需要事务保护
    @Transactional
    // updateRole：修改用户角色授权，增量更新策略
    public void updateRole(long userId, Set<Long> roleIds) {
        // 入参为空：清空全部角色授权
        if (null == roleIds || roleIds.isEmpty()) {
            // 删除该用户的所有角色关联记录
            userRoleRepository.deleteByUserId(userId);
        } else {
            // 查询用户当前已有的角色关联记录
            List<UserRole> list = userRoleRepository.findAllByUserId(userId);
            // 已有且仍保留的角色 ID 列表
            List<Long> exitIds = new ArrayList<>();

            // 如果已有角色关联记录
            if (null != list) {
                // 遍历现有关联记录进行增量比较
                list.forEach(po -> {
                    // 如果当前关联的角色不在新的角色列表中，说明要删除
                    if (!roleIds.contains(po.getRoleId())) {
                        // 删除不再需要的角色关联
                        userRoleRepository.delete(po);
                    } else {
                        // 该角色保留，加入 exitIds 列表
                        exitIds.add(po.getRoleId());
                    }
                });
            }

            // 新增不在已有角色中的新角色授权
            roleIds.stream().filter(id -> !exitIds.contains(id)).forEach(roleId -> {
                // 创建新的用户角色关联实体
                UserRole po = new UserRole();
                // 设置用户 ID
                po.setUserId(userId);
                // 设置角色 ID
                po.setRoleId(roleId);
                // 保存新的关联记录
                userRoleRepository.save(po);
            });
        }


    }
}