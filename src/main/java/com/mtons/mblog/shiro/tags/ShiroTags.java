package com.mtons.mblog.shiro.tags;

import freemarker.template.SimpleHash;

/**
 * <p>用法：<code>cfg.setSharedVariable("shiro", new ShiroTags());</code></p>
 * 等语法调用各标签。</p>
 * <p>包含的标签：</p>
 * <ul>
 *   <li>authenticated：当前 Session 已认证</li>
 *   <li>guest：访客（未登录、无 RememberMe）</li>
 *   <li>hasAnyRoles：拥有任一角色</li>
 *   <li>hasPermission / lacksPermission：拥有/缺失指定权限</li>
 *   <li>hasRole / lacksRole：拥有/缺失指定角色</li>
 *   <li>notAuthenticated：当前 Session 未认证</li>
 *   <li>principal：输出 principal 信息</li>
 *   <li>user：用户被系统识别（登录或 RememberMe）</li>
 * </ul>
 */
public class ShiroTags extends SimpleHash {

    public ShiroTags() {
        put("authenticated", new AuthenticatedTag());
        put("guest", new GuestTag());
        put("hasAnyRoles", new HasAnyRolesTag());
        put("hasPermission", new HasPermissionTag());
        put("hasRole", new HasRoleTag());
        put("lacksPermission", new LacksPermissionTag());
        put("lacksRole", new LacksRoleTag());
        put("notAuthenticated", new NotAuthenticatedTag());
        put("principal", new PrincipalTag());
        put("user", new UserTag());
    }

}
