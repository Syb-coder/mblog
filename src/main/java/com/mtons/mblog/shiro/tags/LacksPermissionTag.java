package com.mtons.mblog.shiro.tags;

/**
 * FreeMarker 模板标签：当前用户不具备指定权限时，渲染标签体。
 * <p>用法：<code>&lt;@shiro.lacksPermission name="user:delete"&gt;...&lt;/@shiro.lacksPermission&gt;</code></p>
 * <p>对应 Shiro 的 {@link org.apache.shiro.web.tags.LacksPermissionTag} 能力。</p>
 * <p>与 {@link HasPermissionTag} 逻辑相反。</p>
 */
public class LacksPermissionTag extends PermissionTag {
    /**
     * 判断是否展示标签体：当前用户不具备指定权限即展示。
     *
     * @param p 权限代码
     * @return 不拥有权限返回 true；否则返回 false
     */
    protected boolean showTagBody(String p) {
        return !isPermitted(p);
    }
}
