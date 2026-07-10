// 包声明：权限服务接口所在包
package com.sunblog.modules.service;

// 导入权限树 VO，用于组装树形菜单结构
import com.sunblog.modules.data.PermissionTree;
// 导入权限实体类，对应权限/菜单表
import com.sunblog.modules.entity.Permission;
// 导入 Spring Data 分页结果封装类
import org.springframework.data.domain.Page;
// 导入 Spring Data 分页请求参数接口
import org.springframework.data.domain.Pageable;

// 导入 List 列表接口
import java.util.List;

/**
 * 权限/菜单 Service 接口
 *
 * <h3>权限在项目中起了两个作用</h3>
 * 1. 后台菜单树：Permission 表记录了后台菜单的层级结构（parentId 实现树形）
 * 2. 权限标识：permission.name 字段用于 Shiro 的权限注解 @RequiresPermissions
 *
 * <h3>为什么权限和菜单是同一个表？</h3>
 * 这是一个常见的设计模式：菜单项本身就是一种权限。
 * 如果一个用户没有某个菜单项的权限，该菜单就不显示。
 * 这样不用维护两套独立的树形结构，减少了数据冗余和同步问题。
 */
// PermissionService 接口：定义权限/菜单的业务操作契约
public interface PermissionService {
    /**
     * @param pageable 分页参数
     * @param name     权限名称（模糊查询）
     * @return 权限分页结果
     */
    // paging：分页查询权限项，支持按名称模糊搜索
    Page<Permission> paging(Pageable pageable, String name);

    /**
     * 列出所有菜单项并组装为树形结构（根节点列表）
     *
     * @return 树形菜单列表
     */
    // tree：组装完整的菜单树，返回根节点列表，子节点挂载在 children 属性中
    List<PermissionTree> tree();

    /**
     * @param parentId 根目录 ID
     * @return 子菜单列表
     */
    // tree：根据父节点 ID 查询子菜单列表，用于局部加载菜单
    List<PermissionTree> tree(int parentId);

    /**
     * @return 权限项列表
     */
    // list：查询所有权限项平铺列表，不分页
    List<Permission> list();

    /**
     * 根据权限 ID 获取权限项信息
     *
     * @param id 权限 ID
     * @return 权限实体
     */
    // get：根据 ID 查询权限详情
    Permission get(long id);

}