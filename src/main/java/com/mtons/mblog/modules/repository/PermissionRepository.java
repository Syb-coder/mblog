package com.mtons.mblog.modules.repository;

import com.mtons.mblog.modules.entity.Permission;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

/**
 * 权限（Permission）数据访问层
 * <p>
 * 对应 Entity：{@link Permission}，主键类型 Long。
 * </p>
 *
 */
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {

    /**
     * @param parentId 父权限 ID
     * @param sort     排序规则
     * @return 子权限列表
     */
    List<Permission> findAllByParentId(int parentId, Sort sort);

    /**
     * 统计指定权限被角色引用的次数（即被多少角色分配了该权限）
     *
     * @param permId 权限 ID
     * @return 引用该权限的角色数量
     */
    @Query(value = "select count(role_id) from shiro_role_permission where permission_id=:permId", nativeQuery = true)
    int countUsed(@Param("permId") long permId);

    /**
     * <p>
     * 使用 coalesce 处理空表场景，无记录时返回 0，
     * 用于新增权限时计算下一个权重序号。
     * </p>
     *
     * @return 当前最大权重值，无记录返回 0
     */
    @Query("select coalesce(max(weight), 0) from Permission")
    int maxWeight();
}