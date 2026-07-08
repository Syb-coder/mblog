package com.mtons.mblog.modules.repository;

import com.mtons.mblog.modules.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * 角色-权限关联（RolePermission）数据访问层
 * <p>
 * 对应 Entity：{@link RolePermission}，主键类型 Long。
 * 业务职责：管理 Shiro 角色与权限的多对多关联关系，
 * </p>
 *
 * @create - 2018/5/18
 */
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long>, JpaSpecificationExecutor<RolePermission> {

    /**
     * 按角色 ID 删除该角色的所有权限关联（用于角色权限重新分配前的清空）
     *
     * @param roleId 角色 ID
     * @return 被删除的关联记录数
     */
    int deleteByRoleId(long roleId);

    /**
     * @param roleId 角色 ID
     * @return 关联记录列表
     */
    List<RolePermission> findAllByRoleId(long roleId);
}