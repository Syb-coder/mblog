package com.mtons.mblog.modules.data;

import com.mtons.mblog.modules.entity.Permission;

import java.util.LinkedList;
import java.util.List;

/**
 * 权限树形结构 VO
 * <p>
 * 职责：继承 Permission Entity，在其基础上扩展子节点列表，
 * 用于将扁平权限数据组装为层级树形结构，便于后台菜单/权限分配页面的层级展示。
 * </p>
 */
public class PermissionTree extends Permission {
    /**
     * 子权限节点列表，懒加载：仅在首次添加子节点时初始化
     */
    private List<PermissionTree> items;

    /**
     * 获取子权限节点列表
     * @return 子节点列表，无子节点时为 null
     */
    public List<PermissionTree> getItems() {
        return items;
    }

    /**
     * 设置子权限节点列表
     * @param items 子节点列表
     */
    public void setItems(List<PermissionTree> items) {
        this.items = items;
    }

    /**
     * 追加单个子权限节点
     * <p>
     * 当 items 为 null 时初始化为 LinkedList，避免每次构造对象都创建空集合带来的内存浪费。
     * </p>
     * @param item 待追加的子节点
     */
    public void addItem(PermissionTree item){
        if(this.items == null){
            this.items = new LinkedList<>();
        }
        this.items.add(item);
    }
}
