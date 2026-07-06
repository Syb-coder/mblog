package com.mtons.mblog.modules.service.impl;

import com.mtons.mblog.modules.data.PermissionTree;
import com.mtons.mblog.modules.entity.Permission;
import com.mtons.mblog.modules.repository.PermissionRepository;
import com.mtons.mblog.modules.service.PermissionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
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
@Service
@Transactional(readOnly = true)
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    private PermissionRepository permissionRepository;

    private Sort sort = Sort.by(
            new Sort.Order(Sort.Direction.DESC, "weight"),
            new Sort.Order(Sort.Direction.ASC, "id")
    );

    @Override
    public Page<Permission> paging(Pageable pageable, String name) {
        Page<Permission> page = permissionRepository.findAll((root, query, builder) -> {
            Predicate predicate = builder.conjunction();

            if (StringUtils.isNoneBlank(name)) {
                predicate.getExpressions().add(
                        builder.like(root.get("name"), "%" + name + "%"));
            }

            query.orderBy(builder.desc(root.get("id")));
            return predicate;
        }, pageable);
        return page;
    }

    /**
     * 组装完整的菜单树
     */
    @Override
    public List<PermissionTree> tree() {
        List<Permission> data = permissionRepository.findAll(sort);
        List<PermissionTree> results = new LinkedList<>();
        Map<Long, PermissionTree> map = new LinkedHashMap<>();

        for (Permission po : data) {
            PermissionTree m = new PermissionTree();
            BeanUtils.copyProperties(po, m);
            map.put(po.getId(), m);
        }

        for (PermissionTree m : map.values()) {
            if (m.getParentId() == 0) {
                // parentId = 0 表示根节点，挂入结果列表
                results.add(m);
            } else {
                // 否则挂入对应父节点的 children 列表
                PermissionTree p = map.get(m.getParentId());
                if (p != null) {
                    p.addItem(m);
                }
            }
        }

        return results;
    }

    @Override
    public List<PermissionTree> tree(int parentId) {
        List<Permission> list = permissionRepository.findAllByParentId(parentId, sort);
        List<PermissionTree> results = new ArrayList<>();

        list.forEach(po -> {
            PermissionTree menu = new PermissionTree();
            BeanUtils.copyProperties(po, menu);
            results.add(menu);
        });
        return results;
    }

    @Override
    public List<Permission> list() {
        return permissionRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    @Override
    public Permission get(long id) {
        return permissionRepository.findById(id).get();
    }

}
