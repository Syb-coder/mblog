package com.mtons.mblog.shiro.tags;

import freemarker.core.Environment;
import freemarker.template.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import java.io.IOException;
import java.util.Map;

/**
 * Subject 获取与标签体渲染等通用逻辑。
 * <p>对应 Shiro 的 {@link org.apache.shiro.web.tags.SecureTag} 能力。</p>
 * <p>子类只需实现 {@link #render} 即可，如需自定义参数校验可重写 {@link #verifyParameters}。</p>
 */
public abstract class SecureTag implements TemplateDirectiveModel {
    /* *
     * @param env       FreeMarker 环境
     * @param params    标签参数
     * @param body      标签体
     * @throws IOException      IO 异常
     */
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        verifyParameters(params);
        render(env, params, body);
    }

    /**
     * 子类实现：实际的渲染逻辑。
     *
     * @param env   FreeMarker 环境
     * @param params 标签参数
     * @param body   标签体
     * @throws IOException      IO 异常
     */
    public abstract void render(Environment env, Map params, TemplateDirectiveBody body) throws IOException, TemplateException;

    /**
     * 从标签参数中按名称读取字符串值。
     *
     * @param params 标签参数集
     * @param name   参数名
     */
    protected String getParam(Map params, String name) {
        Object value = params.get(name);

        if (value instanceof SimpleScalar) {
            return ((SimpleScalar) value).getAsString();
        }

        return null;
    }

    /**
     * 获取当前 Subject。
     *
     * @return 当前请求的 {@link Subject}
     */
    protected Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    /**
     * 参数校验钩子：默认空实现，子类可重写。
     *
     * @param params 标签参数集
     * @throws TemplateModelException 参数非法时抛出
     */
    protected void verifyParameters(Map params) throws TemplateModelException {
    }

    /**
     * 渲染标签体：body 非空时将其输出到 FreeMarker 的 Writer。
     *
     * @param env  FreeMarker 环境
     * @param body  标签体
     * @throws IOException      IO 异常
     */
    protected void renderBody(Environment env, TemplateDirectiveBody body) throws IOException, TemplateException {
        if (body != null) {
            body.render(env.getOut());
        }
    }
}
