package com.mtons.mblog.modules.repository;

import com.mtons.mblog.modules.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Set;

/**
 * 角色（Role）数据访问层
 * <p>
 * 对应 Entity：{@link Role}，主键类型 Long。
 * </p>
 */
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

    /**
     * @param status 角色状态（启用 / 禁用）
     * @return 符合状态条件的角色列表
     */
    List<Role> findAllByStatus(int status);
}