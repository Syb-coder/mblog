// 包声明：角色服务接口所在包
package com.sunblog.modules.service;

// 导入权限实体类，update 方法的参数中使用
import com.sunblog.modules.entity.Permission;
// 导入角色实体类，方法返回值和参数中使用
import com.sunblog.modules.entity.Role;
// 导入 Spring Data 分页结果封装类
import org.springframework.data.domain.Page;
// 导入 Spring Data 分页请求参数接口
import org.springframework.data.domain.Pageable;

// 导入 List 列表接口
import java.util.List;
// 导入 Map 映射接口，用于以 ID 为 key 批量查找角色
import java.util.Map;
// 导入 Set 集合接口，用于批量查询和权限集合
import java.util.Set;

/**
 * 角色 Service 接口
 *
 * <h3>角色是什么？</h3>
 * 角色是权限的集合。项目中的 RBAC（Role-Based Access Control）模型：
 * User → UserRole → Role → RolePermission → Permission
 *
 * 一个用户可以有多个角色，一个角色可以有多个权限。
 * 角色可以激活/停用（activate()），停用后该角色下的所有用户将失去对应权限。
 *
 * <h3>删除保护</h3>
 * 如果某个角色已经被分配给了用户（UserRole 表有关联记录），
 * 删除操作会失败，防止误删导致权限错乱。
 */
// RoleService 接口：定义角色的业务操作契约
public interface RoleService {
    /**
     * @param pageable 分页参数
     * @param name     角色名称（模糊查询）
     * @return 角色分页结果
     */
    // paging：分页查询角色，支持按名称模糊搜索
    Page<Role> paging(Pageable pageable, String name);

    /**
     * @return 角色列表
     */
    // list：查询所有正常状态的角色列表，不分页
    List<Role> list();

    /**
     * @param ids 角色 ID 集合
     * @return 以角色 id 为 key 的映射
     */
    // findByIds：根据 ID 集合批量查询角色，返回以 ID 为 key 的 Map
    Map<Long, Role> findByIds(Set<Long> ids);

    /**
     * @param id 角色 ID
     * @return 角色实体
     */
    // get：根据 ID 查询角色详情（含权限明细）
    Role get(long id);

    /**
     * <p>更新策略：先清空旧权限关联，再批量写入新权限关联。</p>
     *
     * @param r           角色对象
     * @param permissions 角色权限集合
     */
    // update：更新角色基本信息及其权限关联，采用"先删后增"全量替换策略
    void update(Role r, Set<Permission> permissions);

    /**
     * 删除角色（已被授权的角色不允许删除）
     *
     * @param id 角色 ID
     * @return true 表示删除成功
     */
    // delete：删除角色，已被用户使用的角色不允许删除，返回是否删除成功
    boolean delete(long id);

    /**
     * 激活或停用角色
     *
     * @param id     角色 ID
     * @param active true：激活，false：停用
     */
    // activate：激活或停用角色，停用后该角色下的用户将失去对应权限
    void activate(long id, boolean active);

}