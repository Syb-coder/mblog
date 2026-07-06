package com.mtons.mblog.shiro.tags;

/**
 * FreeMarker 模板标签：当前用户不具备指定角色时，渲染标签体。
 * <p>用法：<code>&lt;@shiro.lacksRole name="admin"&gt;...&lt;/@shiro.lacksRole&gt;</code></p>
 * <p>对应 Shiro 的 {@link org.apache.shiro.web.tags.LacksRoleTag} 能力。</p>
 * <p>与 {@link HasRoleTag} 逻辑相反。</p>
 */
public class LacksRoleTag extends RoleTag {
    /**
     * 判断是否展示标签体：当前用户不具备指定角色即展示。
     *
     * @param roleName 角色名
     * @return 不拥有角色返回 true；否则返回 false
     */
    protected boolean showBody(String roleName) {
        boolean hasRole = getSubject() != null && getSubject().hasRole(roleName);
        return !hasRole;
    }
}
