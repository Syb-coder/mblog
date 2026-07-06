package com.mtons.mblog.shiro.tags;

import org.apache.shiro.subject.Subject;


/**
 * <p>用法：<code>&lt;@shiro.hasAnyRoles name="roleA,roleB"&gt;...&lt;/@shiro.hasAnyRoles&gt;</code></p>
 * <p>对应 Shiro 的 {@link org.apache.shiro.web.tags.HasAnyRolesTag} 能力。</p>
 * <p>与 {@link HasRoleTag} 的区别：后者要求同时具备单一角色，前者只要满足其一即通过。</p>
 *
 * @since 0.2
 */
public class HasAnyRolesTag extends RoleTag {
    // 角色名分隔符，标签 name 属性中多个角色以逗号分隔
    private static final String ROLE_NAMES_DELIMETER = ",";

    /**
     * 判断是否展示标签体：任一角色命中即展示。
     *
     * @param roleNames 逗号分隔的角色名列表
     * @return 拥有任意一个角色返回 true；否则返回 false
     */
    protected boolean showBody(String roleNames) {
        boolean hasAnyRole = false;
        Subject subject = getSubject();

        if (subject != null) {
            for (String role : roleNames.split(ROLE_NAMES_DELIMETER)) {
                if (subject.hasRole(role.trim())) {
                    hasAnyRole = true;
                    break;
                }
            }
        }

        return hasAnyRole;
    }
}
