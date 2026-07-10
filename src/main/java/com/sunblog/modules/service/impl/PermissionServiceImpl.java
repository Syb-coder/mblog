// 包声明：权限服务实现类所在包
package com.sunblog.modules.service.impl;

// 导入权限树 VO，用于组装树形菜单结构
import com.sunblog.modules.data.PermissionTree;
// 导入权限实体类
import com.sunblog.modules.entity.Permission;
// 导入权限仓储接口，提供权限表的 CRUD 操作
import com.sunblog.modules.repository.PermissionRepository;
// 导入权限服务接口，本类实现该接口
import com.sunblog.modules.service.PermissionService;
// 导入 Apache Commons 字符串工具类，用于判断字符串非空
import org.apache.commons.lang3.StringUtils;
// 导入 Spring Bean 属性拷贝工具
import org.springframework.beans.BeanUtils;
// 导入 Spring 自动注入注解
import org.springframework.beans.factory.annotation.Autowired;
// 导入 Spring Data 分页结果封装类
import org.springframework.data.domain.Page;
// 导入 Spring Data 分页请求参数接口
import org.springframework.data.domain.Pageable;
// 导入 Spring Data 排序类
import org.springframework.data.domain.Sort;
// 导入 Spring Service 注解
import org.springframework.stereotype.Service;
// 导入 Spring 事务注解
import org.springframework.transaction.annotation.Transactional;

// 导入 JPA Criteria 断言（谓词）接口，用于动态拼接查询条件
import jakarta.persistence.criteria.Predicate;
// 导入 Java 工具类包
import java.util.*;

/**
 * 权限（菜单）管理 Service 实现
 * <p>
 * 关键依赖：
 * <ul>
 *   <li>{@link PermissionRepository}：权限项 JPA 仓储</li>
 * </ul>
 * </p>
 * <p>
 * 排序策略：默认按 weight 倒序 + id 升序排序，保证高权重菜单靠前、同级菜单按 id 稳定排序。
 * </p>
 *
 */
// @Service 注解：将该类注册为 Spring 业务服务组件
@Service
// @Transactional(readOnly = true)：类级别只读事务
@Transactional(readOnly = true)
// PermissionServiceImpl：权限（菜单）管理服务实现类
public class PermissionServiceImpl implements PermissionService {
    // @Autowired：Spring 自动注入权限仓储实例
    @Autowired
    // permissionRepository：权限 JPA 仓储，提供权限表的数据库操作
    private PermissionRepository permissionRepository;

    // sort：默认排序规则，weight 倒序（高权重在前）+ id 升序（同级按 id 排序）
    private Sort sort = Sort.by(
            // weight 倒序：权重高的菜单排在前面
            new Sort.Order(Sort.Direction.DESC, "weight"),
            // id 升序：同级菜单按 id 稳定排序
            new Sort.Order(Sort.Direction.ASC, "id")
    );

    // @Override：实现接口方法
    @Override
    // paging：分页查询权限项，支持按名称模糊搜索
    public Page<Permission> paging(Pageable pageable, String name) {
        // 使用 JPA Criteria 动态构建查询条件
        Page<Permission> page = permissionRepository.findAll((root, query, builder) -> {
            // 创建一个空 conjunction（AND 连接的空条件）
            Predicate predicate = builder.conjunction();

            // 如果 name 参数非空，添加模糊匹配条件
            if (StringUtils.isNoneBlank(name)) {
                // 添加 LIKE 模糊查询条件：name LIKE '%name%'
                predicate.getExpressions().add(
                        builder.like(root.get("name"), "%" + name + "%"));
            }

            // 按 id 倒序排序
            query.orderBy(builder.desc(root.get("id")));
            // 返回查询条件
            return predicate;
        }, pageable);
        // 返回分页结果
        return page;
    }

    /**
     * 组装完整的菜单树
     */
    // @Override：实现接口方法
    @Override
    // tree：组装完整的菜单树，返回根节点列表
    public List<PermissionTree> tree() {
        // 查询所有权限项并按默认排序规则排序
        List<Permission> data = permissionRepository.findAll(sort);
        // 根节点结果列表
        List<PermissionTree> results = new LinkedList<>();
        // 以 ID 为 key 的权限树映射，用于快速查找父节点
        Map<Long, PermissionTree> map = new LinkedHashMap<>();

        // 第一遍遍历：将所有权限项转换为树节点并放入 Map
        for (Permission po : data) {
            // 创建树节点对象
            PermissionTree m = new PermissionTree();
            // 将权限实体属性拷贝到树节点
            BeanUtils.copyProperties(po, m);
            // 以权限 ID 为 key 存入 Map
            map.put(po.getId(), m);
        }

        // 第二遍遍历：根据 parentId 组装树形结构
        for (PermissionTree m : map.values()) {
            // parentId = 0 表示根节点
            if (m.getParentId() == 0) {
                // 根节点直接挂入结果列表
                results.add(m);
            } else {
                // 非根节点：查找父节点并挂入其 children 列表
                PermissionTree p = map.get(m.getParentId());
                // 如果父节点存在，将当前节点加入其子节点列表
                if (p != null) {
                    p.addItem(m);
                }
            }
        }

        // 返回根节点列表（包含完整的树形结构）
        return results;
    }

    // @Override：实现接口方法
    @Override
    // tree：根据父节点 ID 查询子菜单列表
    public List<PermissionTree> tree(int parentId) {
        // 根据 parentId 查询子权限项列表
        List<Permission> list = permissionRepository.findAllByParentId(parentId, sort);
        // 创建返回结果列表
        List<PermissionTree> results = new ArrayList<>();

        // 遍历查询结果，将实体转换为树节点 VO
        list.forEach(po -> {
            // 创建树节点对象
            PermissionTree menu = new PermissionTree();
            // 将权限实体属性拷贝到树节点
            BeanUtils.copyProperties(po, menu);
            // 加入结果列表
            results.add(menu);
        });
        // 返回子菜单树节点列表
        return results;
    }

    // @Override：实现接口方法
    @Override
    // list：查询所有权限项平铺列表，按 id 倒序
    public List<Permission> list() {
        // 查询所有权限项，按 id 倒序排序
        return permissionRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    // @Override：实现接口方法
    @Override
    // get：根据 ID 查询权限详情
    public Permission get(long id) {
        // 通过仓储按 ID 查询，get() 直接获取值
        return permissionRepository.findById(id).get();
    }

}