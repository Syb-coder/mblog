package com.mtons.mblog.shiro.tags;

import freemarker.core.Environment;
import freemarker.log.Logger;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.Map;


/**
 * 才渲染标签体内容。
 * <p>用法：<code>&lt;@shiro.authenticated&gt;...&lt;/@shiro.authenticated&gt;</code></p>
 * <p>对应 Shiro 的 {@link org.apache.shiro.web.tags.AuthenticatedTag} 能力。</p>
 * <p>本标签比 {@link UserTag} 更严格——UserTag 仅要求用户被系统识别
 * （当前登录或 RememberMe），但不保证当前 Session 中真的执行过认证；
 * 而本标签强调当前 Session 已成功完成认证动作。逻辑相反的标签为
 * {@link NotAuthenticatedTag}。</p>
 *
 * @since 0.2
 */
public class AuthenticatedTag extends SecureTag {
    private static final Logger log = Logger.getLogger("AuthenticatedTag");

    /**
     * 渲染逻辑：满足认证条件则渲染标签体，否则跳过。
     *
     * @param env   FreeMarker 环境
     * @param params 标签参数
     * @param body   标签体
     * @throws IOException      IO 异常
     */
    @Override
    public void render(Environment env, Map params, TemplateDirectiveBody body) throws IOException, TemplateException {
        // 已认证或 RememberMe 都视为通过，便于 RememberMe 用户访问
        if (getSubject() != null && (getSubject().isAuthenticated() || getSubject().isRemembered())) {
            if (log.isDebugEnabled()) {
                log.debug("Subject exists and is authenticated.  Tag body will be evaluated.");
            }

            renderBody(env, body);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Subject does not exist or is not authenticated.  Tag body will not be evaluated.");
            }
        }
    }
}
