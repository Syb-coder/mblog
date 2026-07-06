package com.mtons.mblog.shiro.tags;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.Map;

/**
 * <p>对应 Shiro 的 {@link org.apache.shiro.web.tags.RoleTag} 能力。</p>
 * <p>子类需实现 {@link #showBody} 决定是否展示标签体。</p>
 */
public abstract class RoleTag extends SecureTag {
    /**
     * 从标签参数中获取角色名。
     *
     * @param params 标签参数集
     * @return 角色名字符串
     */
    String getName(Map params) {
        return getParam(params, "name");
    }

    /**
     * 渲染入口：取出角色名，调用 {@link #showBody} 决定是否渲染标签体。
     *
     * @param env   FreeMarker 环境
     * @param params 标签参数
     * @param body   标签体
     * @throws IOException      IO 异常
     */
    @Override
    public void render(Environment env, Map params, TemplateDirectiveBody body) throws IOException, TemplateException {
        boolean show = showBody(getName(params));
        if (show) {
            renderBody(env, body);
        }
    }

    /**
     * 子类实现：根据角色名决定是否渲染标签体。
     *
     * @param roleName 角色名
     * @return true 展示；false 不展示
     */
    protected abstract boolean showBody(String roleName);
}
