package com.mtons.mblog.modules.service;

import com.mtons.mblog.modules.entity.Permission;
import com.mtons.mblog.modules.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 角色管理 Service
 * <p>
 * 删除前需校验角色是否被用户授权使用，已被使用的角色不允许删除。
 * </p>
 *
 */
public interface RoleService {
    /* *
     * @param pageable 分页参数
     * @return 角色分页结果
     */
    Page<Role> paging(Pageable pageable, String name);

    /* *
     * @return 角色列表
     */
    List<Role> list();

    /* *
     * @param ids 角色 ID 集合
     * @return 以角色 id 为 key 的映射
     */
    Map<Long, Role> findByIds(Set<Long> ids);

    /* *
     * @param id 角色 ID
     * @return 角色实体
     */
    Role get(long id);

    /**
     * <p>更新策略：先清空旧权限关联，再批量写入新权限关联。</p>
     *
     * @param r           角色对象
     * @param permissions 角色权限集合
     */
    void update(Role r, Set<Permission> permissions);

    /**
     * 删除角色（已被授权的角色不允许删除）
     *
     * @param id 角色 ID
     * @return true 表示删除成功
     */
    boolean delete(long id);

    /**
     * 激活或停用角色
     *
     * @param id     角色 ID
     * @param active true：激活，false：停用
     */
    void activate(long id, boolean active);

}
