package com.mtons.mblog.shiro.tags;

/**
 * FreeMarker 模板标签：当前用户拥有指定角色时，渲染标签体。
 * <p>用法：<code>&lt;@shiro.hasRole name="admin"&gt;...&lt;/@shiro.hasRole&gt;</code></p>
 * <p>对应 Shiro 的 {@link org.apache.shiro.web.tags.HasRoleTag} 能力。</p>
 */
public class HasRoleTag extends RoleTag {
    /**
     * 判断是否展示标签体：当前用户具备指定角色即展示。
     *
     * @param roleName 角色名
     * @return 拥有角色返回 true；否则返回 false
     */
    protected boolean showBody(String roleName) {
        return getSubject() != null && getSubject().hasRole(roleName);
    }
}
