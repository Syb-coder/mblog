package com.sunblog.shiro;

import com.sunblog.modules.data.AccountProfile;
import com.sunblog.modules.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.mgt.DefaultSubjectFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.subject.WebSubjectContext;
import org.apache.shiro.web.subject.support.WebDelegatingSubject;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

/**
 * 自定义 Subject 工厂：在 Shiro 默认的 {@link DefaultSubjectFactory} 基础上，
 * 为每次创建的 Web Subject 注入登录态自愈逻辑。
 * <p>
 * 用途：当用户通过 RememberMe 重新进入系统，或因服务重启等原因导致 Session 中
 * 的 profile 丢失时，主动从数据库重新加载 {@link AccountProfile} 并写回 Session，
 * </p>
 * <p>等价于 Shiro 默认工厂 + Session 内登录态自愈能力。</p>
 */
@Slf4j
public class AccountSubjectFactory extends DefaultSubjectFactory {
    @Autowired
    private UserService userService;

    /**
     * 创建 Subject：非 Web 环境委托父类处理；Web 环境下手工组装
     * {@link WebDelegatingSubject}，并在创建后执行 Session 自愈。
     *
     * @param context Subject 上下文，包含 principals/session/request/response 等
     * @return 创建好的 {@link Subject}
     */
    @Override
    public Subject createSubject(SubjectContext context) {
        if (!(context instanceof WebSubjectContext)) {
            return super.createSubject(context);
        } else {
            WebSubjectContext wsc = (WebSubjectContext)context;
            SecurityManager securityManager = wsc.resolveSecurityManager();
            Session session = wsc.resolveSession();
            boolean sessionEnabled = wsc.isSessionCreationEnabled();
            PrincipalCollection principals = wsc.resolvePrincipals();
            boolean authenticated = wsc.resolveAuthenticated();
            String host = wsc.resolveHost();
            ServletRequest request = wsc.resolveServletRequest();
            ServletResponse response = wsc.resolveServletResponse();

            Subject subject =  new WebDelegatingSubject(principals, authenticated, host, session, sessionEnabled, request, response, securityManager);
            // 创建 Subject 后立即尝试恢复 Session 中的登录信息
            handlerSession(subject);
            return subject;
        }
    }

    /**
     * Session 自愈：当用户已认证或处于 RememberMe 状态，但 Session 中
     * 缺失 profile 时，从数据库重新加载并写回。
     *
     * @param subject 当前 Subject
     */
    private void handlerSession(Subject subject) {
        Session session = subject.getSession(true);
        // 已登录或 RememberMe 且 Session 缺失 profile，触发重载
        if ((subject.isAuthenticated() || subject.isRemembered()) && session.getAttribute("profile") == null) {
            AccountProfile profile = (AccountProfile) subject.getPrincipal();
            log.debug("reload session - " + profile.getUsername());
            session.setAttribute("profile", userService.findProfile(profile.getId()));
        }
    }

}
