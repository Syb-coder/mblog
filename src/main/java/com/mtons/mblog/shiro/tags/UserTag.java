package com.mtons.mblog.shiro.tags;

import freemarker.core.Environment;
import freemarker.log.Logger;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.Map;

/**
 * 渲染标签体内容。
 * <p>用法：<code>&lt;@shiro.user&gt;...&lt;/@shiro.user&gt;</code></p>
 * <p>对应 Shiro 的 {@link org.apache.shiro.web.tags.UserTag} 能力。</p>
 * <p><b>注意：</b>本标签比 {@link AuthenticatedTag} 宽松——它仅要求用户被系统识别，
 * 不要求当前 Session 真的执行过认证动作；RememberMe 用户同样满足条件。
 * 逻辑相反的标签为 {@link GuestTag}。</p>
 */
public class UserTag extends SecureTag {
    static final Logger log = Logger.getLogger("UserTag");

    /* *
     * @param env   FreeMarker 环境
     * @param params 标签参数
     * @param body   标签体
     * @throws IOException      IO 异常
     */
    @Override
    public void render(Environment env, Map params, TemplateDirectiveBody body) throws IOException, TemplateException {
        if (getSubject() != null && getSubject().getPrincipal() != null) {
            log.debug("Subject has known identity (aka 'principal'). Tag body will be evaluated.");
            renderBody(env, body);
        } else {
            log.debug("Subject does not exist or have a known identity (aka 'principal'). Tag body will not be evaluated.");
        }
    }
}
