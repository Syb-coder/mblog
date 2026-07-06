package com.mtons.mblog.shiro.tags;

/**
 * FreeMarker 模板标签：当前用户拥有指定权限时，渲染标签体。
 * <p>用法：<code>&lt;@shiro.hasPermission name="user:create"&gt;...&lt;/@shiro.hasPermission&gt;</code></p>
 * <p>对应 Shiro 的 {@link org.apache.shiro.web.tags.HasPermissionTag} 能力。</p>
 * <p>权限校验逻辑参见 {@link PermissionTag#isPermitted}：超级管理员默认放行。</p>
 *
 * @since 0.1
 */
public class HasPermissionTag extends PermissionTag {
    /**
     * 判断是否展示标签体：当前用户具备指定权限即展示。
     *
     * @param p 权限代码
     * @return 拥有权限返回 true；否则返回 false
     */
    protected boolean showTagBody(String p) {
        return isPermitted(p);
    }
}
