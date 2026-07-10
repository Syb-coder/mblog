package com.sunblog.modules.repository;

import com.sunblog.modules.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

/**
 * 用户-角色关联（UserRole）数据访问层
 * <p>
 * 对应 Entity：{@link UserRole}，主键类型 Long。
 * 业务职责：管理用户与角色的多对多关联关系，
 * </p>
 */
public interface UserRoleRepository extends JpaRepository<UserRole, Long>, JpaSpecificationExecutor<UserRole> {

    /**
     * @param userId 用户 ID
     * @return 角色 关联记录列表
     */
    List<UserRole> findAllByUserId(long userId);

    /**
     * @param userIds 用户 ID 集合
     * @return 关联记录列表
     */
    List<UserRole> findAllByUserIdIn(Collection<Long> userIds);

    /**
     * <p>用于角色删除前判断是否仍被用户引用。</p>
     *
     * @param roleId 角色 ID
     * @return 关联记录列表
     */
    List<UserRole> findAllByRoleId(long roleId);

    /**
     * 按用户 ID 删除该用户的所有角色关联（用于角色重新分配前的清空）
     *
     * @param userId 用户ID
     * @return 被删除的关联记录数
     */
    int deleteByUserId(long userId);
}