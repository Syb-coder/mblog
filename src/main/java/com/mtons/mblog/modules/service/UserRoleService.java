package com.mtons.mblog.modules.service;

import com.mtons.mblog.modules.entity.Role;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户角色关联 Service
 * <p>
 * </p>
 *
 */
public interface UserRoleService {
    /* *
     * @param userId 用户 ID
     * @return 角色 ID 列表
     */
    List<Long> listRoleIds(long userId);

    /* *
     * @param userId 用户 ID
     * @return 角色列表
     */
    List<Role> listRoles(long userId);

    /* *
     * @param userIds 用户 ID 列表
     * @return 以用户 id 为 key、角色列表为 value 的映射
     */
    Map<Long, List<Role>> findMapByUserIds(List<Long> userIds);

    /**
     * 修改用户角色授权
     * <p>策略：清空入参时全部取消授权；否则增量更新（删除不在新列表中的角色，新增不在已有列表中的角色）。</p>
     *
     * @param userId  用户 ID
     * @param roleIds 要授权的角色 ID 集合
     */
    void updateRole(long userId, Set<Long> roleIds);
}
