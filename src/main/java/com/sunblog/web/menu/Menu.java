package com.sunblog.web.menu;

/**
 * 菜单项数据结构
 * <p>
 * 职责说明：
 * <ul>
 *   <li>描述后台管理界面中一个菜单项的数据结构，与 {@code menu.json} 配置文件中的字段一一对应；</li>
 *   <li>用于在视图层（FreeMarker）渲染后台左侧菜单，并基于 {@link #permission} 字段实现菜单级别的权限过滤；</li>
 *   <li>由 {@link MenuJsonUtils} 解析 JSON 文件后构建，由 {@link MenusDirective} 渲染输出。</li>
 * </ul>
 *
 * 字段说明：
 * <ul>
 *   <li>{@link #icon}：菜单图标样式（CSS 类名或图标资源路径）；</li>
 *   <li>{@link #name}：菜单显示名称；</li>
 *   <li>{@link #url}：菜单点击后跳转的 URL；</li>
 *   <li>{@link #permission}：访问该菜单所需的权限标识，多个权限以英文逗号分隔。</li>
 * </ul>
 *
 * @create - 2018/5/18
 */
public class Menu {
    /** 菜单图标（CSS 类名或图标资源路径） */
    private String icon;
    /** 菜单显示名称 */
    private String name;
    /** 菜单跳转 URL */
    private String url;
    /** 访问权限标识，多个权限以英文逗号分隔 */
    private String permission;

    /**
     * 获取菜单图标
     *
     * @return 菜单图标
     */
    public String getIcon() {
        return icon;
    }

    /**
     * 设置菜单图标
     *
     * @param icon 菜单图标
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * 获取菜单名称
     *
     * @return 菜单名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置菜单名称
     *
     * @param name 菜单名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取菜单跳转 URL
     *
     * @return 菜单 URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置菜单跳转 URL
     *
     * @param url 菜单 URL
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取访问该菜单所需的权限标识
     *
     * @return 权限标识字符串（多个权限以英文逗号分隔）
     */
    public String getPermission() {
        return permission;
    }

    /**
     * 设置访问该菜单所需的权限标识
     *
     * @param permission 权限标识字符串（多个权限以英文逗号分隔）
     */
    public void setPermission(String permission) {
        this.permission = permission;
    }
}
