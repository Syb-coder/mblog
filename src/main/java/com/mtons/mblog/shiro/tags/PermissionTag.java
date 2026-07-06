package com.mtons.mblog.shiro.tags;

import com.mtons.mblog.base.lang.Consts;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;

import java.io.IOException;
import java.util.Map;

/**
 * <p>对应 Shiro 的 {@link org.apache.shiro.web.tags.PermissionTag} 能力。</p>
 * <p>子类需实现 {@link #showTagBody} 决定是否展示标签体；
 * 内置的 {@link #isPermitted} 统一处理超级管理员绕过权限校验的逻辑。</p>
 */
public abstract class PermissionTag extends SecureTag {
    /**
     * 从标签参数中获取权限名。
     *
     * @param params 标签参数集
     * @return 权限代码字符串
     */
    String getName(Map params) {
        return getParam(params, "name");
    }

    /**
     * 参数校验：name 属性必填，缺失抛出 {@link TemplateModelException}。
     *
     * @param params 标签参数集
     * @throws TemplateModelException 当 name 缺失或为空时抛出
     */
    @Override
    protected void verifyParameters(Map params) throws TemplateModelException {
        String permission = getName(params);

        if (permission == null || permission.length() == 0) {
            throw new TemplateModelException("The 'name' tag attribute must be set.");
        }
    }

    /**
     * 渲染入口：取出权限名，调用 {@link #showTagBody} 决定是否渲染标签体。
     *
     * @param env   FreeMarker 环境
     * @param params 标签参数
     * @param body   标签体
     * @throws IOException      IO 异常
     */
    @Override
    public void render(Environment env, Map params, TemplateDirectiveBody body) throws IOException, TemplateException {
        String p = getName(params);

        boolean show = showTagBody(p);
        if (show) {
            renderBody(env, body);
        }
    }

    /**
     * 判断是否拥有指定权限
     * - 超级管理员例外, 拥有所有
     * @param p 权限代码
     * @return true/false
     */
    protected boolean isPermitted(String p) {
        return getSubject() != null && (getSubject().hasRole(Consts.ROLE_ADMIN) || getSubject().isPermitted(p));
    }

    /**
     * 子类实现：根据权限代码决定是否渲染标签体。
     *
     * @param p 权限代码
     * @return true 展示；false 不展示
     */
    protected abstract boolean showTagBody(String p);
}
