package com.mtons.mblog.shiro.tags;

import freemarker.core.Environment;
import freemarker.log.Logger;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.Map;


/**
 * 才渲染标签体内容，通常用于渲染"登录/注册"入口。
 * <p>用法：<code>&lt;@shiro.guest&gt;...&lt;/@shiro.guest&gt;</code></p>
 * <p>对应 Shiro 的 {@link org.apache.shiro.web.tags.GuestTag} 能力。</p>
 * <p>逻辑相反的标签为 {@link UserTag}，二者语义差异参见
 * {@link AuthenticatedTag} 与 {@link NotAuthenticatedTag} 的说明。</p>
 *
 * @since 0.9
 */
public class GuestTag extends SecureTag {
    private static final Logger log = Logger.getLogger("AuthenticatedTag");

    /* *
     * @param env   FreeMarker 环境
     * @param params 标签参数
     * @param body   标签体
     * @throws IOException      IO 异常
     */
    @Override
    public void render(Environment env, Map params, TemplateDirectiveBody body) throws IOException, TemplateException {
        if (getSubject() == null || getSubject().getPrincipal() == null) {
            if (log.isDebugEnabled()) {
                log.debug("Subject does not exist or does not have a known identity (aka 'principal').  " +
                        "Tag body will be evaluated.");
            }

            renderBody(env, body);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Subject exists or has a known identity (aka 'principal').  " +
                        "Tag body will not be evaluated.");
            }
        }
    }
}
