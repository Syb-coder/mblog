package com.mtons.mblog.shiro.tags;

import freemarker.core.Environment;
import freemarker.log.Logger;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.Map;


/**
 * 才渲染标签体内容。
 * <p>用法：<code>&lt;@shiro.notAuthenticated&gt;...&lt;/@shiro.notAuthenticated&gt;</code></p>
 * <p>对应 Shiro 的 {@link org.apache.shiro.web.tags.NotAuthenticatedTag} 能力。</p>
 * <p>逻辑相反的标签为 {@link AuthenticatedTag}。</p>
 */
public class NotAuthenticatedTag extends SecureTag {
    static final Logger log = Logger.getLogger("NotAuthenticatedTag");

    /**
     * 渲染逻辑：未认证且非 RememberMe 时渲染标签体，否则跳过。
     *
     * @param env   FreeMarker 环境
     * @param params 标签参数
     * @param body   标签体
     * @throws IOException      IO 异常
     */
    @Override
    public void render(Environment env, Map params, TemplateDirectiveBody body) throws IOException, TemplateException {
        if (getSubject() == null || !(getSubject().isAuthenticated() || getSubject().isRemembered())) {
            log.debug("Subject does not exist or is not authenticated.  Tag body will be evaluated.");
            renderBody(env, body);
        } else {
            log.debug("Subject exists and is authenticated.  Tag body will not be evaluated.");
        }
    }
}
