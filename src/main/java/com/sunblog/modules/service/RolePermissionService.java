// 包声明：角色权限关联服务接口所在包
package com.sunblog.modules.service;

// 导入权限实体类，findPermissions 方法的返回类型
import com.sunblog.modules.entity.Permission;
// 导入角色权限关联实体类，add 方法的参数类型
import com.sunblog.modules.entity.RolePermission;

// 导入 List 列表接口
import java.util.List;
// 导入 Set 集合接口，用于批量新增关联
import java.util.Set;

/**
 * 角色-权限关联 Service
 *
 * <h3>功能</h3>
 * 角色和权限是多对多关系（通过 RolePermission 关联表）。
 * 这个 Service 负责：
 * - findPermissions(roleId)：查询某角色有哪些权限（用于 Show 授权时展示已有权限）
 * - deleteByRoleId(roleId)：清空角色所有权限（更新权限前的清理）
 * - add(Set)：批量新增关联（更新权限后的写入）
 *
 * update 角色权限 = deleteByRoleId + add（两步操作）
 */
// RolePermissionService 接口：定义角色与权限关联关系的业务操作契约
public interface RolePermissionService {
    /**
     * @param roleId 角色 ID
     * @return 权限列表，无授权时返回 null
     */
    // findPermissions：查询指定角色拥有的全部权限列表
    List<Permission> findPermissions(long roleId);

    /**
     * 删除指定角色的全部权限关联（用于角色重新授权前的清空）
     *
     * @param roleId 角色 ID
     */
    // deleteByRoleId：清空指定角色的所有权限关联，配合 add 实现权限全量替换
    void deleteByRoleId(long roleId);

    /**
     * 批量新增角色权限关联
     *
     * @param rolePermissions 角色权限关联集合
     */
    // add：批量写入角色权限关联记录，减少 SQL 执行次数
    void add(Set<RolePermission> rolePermissions);

}