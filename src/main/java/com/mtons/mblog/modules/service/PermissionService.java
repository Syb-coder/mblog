package com.mtons.mblog.modules.service;

import com.mtons.mblog.modules.data.PermissionTree;
import com.mtons.mblog.modules.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 权限（菜单）管理 Service
 * <p>
 * </p>
 *
 */
public interface PermissionService {
    /* *
     * @param pageable 分页参数
     * @return 权限分页结果
     */
    Page<Permission> paging(Pageable pageable, String name);

    /**
     * 列出所有菜单项并组装为树形结构（根节点列表）
     *
     * @return 树形菜单列表
     */
    List<PermissionTree> tree();

    /* *
     * @param parentId 根目录 ID
     * @return 子菜单列表
     */
    List<PermissionTree> tree(int parentId);

    /* *
     * @return 权限项列表
     */
    List<Permission> list();

    /**
     * 根据权限 ID 获取权限项信息
     *
     * @param id 权限 ID
     * @return 权限实体
     */
    Permission get(long id);

}
