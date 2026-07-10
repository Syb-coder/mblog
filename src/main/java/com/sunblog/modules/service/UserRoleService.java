// 包声明：用户角色关联服务接口所在包
package com.sunblog.modules.service;

// 导入角色实体类，方法返回值中使用
import com.sunblog.modules.entity.Role;

// 导入 List 列表接口
import java.util.List;
// 导入 Map 映射接口，用于批量查询用户角色映射
import java.util.Map;
// 导入 Set 集合接口，用于角色 ID 集合参数
import java.util.Set;

/**
 * 用户-角色关联 Service
 *
 * <h3>桥梁作用</h3>
 * 用户和角色是多对多关系（通过 UserRole 关联表）。
 * 这个 Service 负责：
 * - 查：某用户有哪些角色
 * - 改：修改用户的角色授权（先删旧的，再插入新的）
 *
 * <h3>findMapByUserIds 的用途</h3>
 * 在批量展示用户列表时，一次性查出所有用户的角色信息，
 * 避免 N+1 查询问题。
 */
// UserRoleService 接口：定义用户与角色关联关系的业务操作契约
public interface UserRoleService {
    /**
     * @param userId 用户 ID
     * @return 角色 ID 列表
     */
    // listRoleIds：查询指定用户的角色 ID 列表
    List<Long> listRoleIds(long userId);

    /**
     * @param userId 用户 ID
     * @return 角色列表
     */
    // listRoles：查询指定用户的角色详情列表
    List<Role> listRoles(long userId);

    /**
     * @param userIds 用户 ID 列表
     * @return 以用户 id 为 key、角色列表为 value 的映射
     */
    // findMapByUserIds：批量查询多用户的角色信息，返回以用户 ID 为 key 的 Map，避免 N+1
    Map<Long, List<Role>> findMapByUserIds(List<Long> userIds);

    /**
     * 修改用户角色授权
     * <p>策略：清空入参时全部取消授权；否则增量更新（删除不在新列表中的角色，新增不在已有列表中的角色）。</p>
     *
     * @param userId  用户 ID
     * @param roleIds 要授权的角色 ID 集合
     */
    // updateRole：修改用户角色授权，增量更新策略（删旧的、增新的）
    void updateRole(long userId, Set<Long> roleIds);
}