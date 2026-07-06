package com.mtons.mblog.shiro;

import java.io.IOException;
import java.util.Formatter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import com.alibaba.fastjson2.JSON;
import com.mtons.mblog.base.lang.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.OncePerRequestFilter;
import org.apache.shiro.web.util.WebUtils;

/**
 * 登录校验过滤器：在 Shiro 默认 authc 过滤器的基础上，对未登录请求做差异化处理。
 * <p>
 * 与默认 {@code authc} 过滤器的差异：
 * <ul>
 *   <li>对于 Ajax 请求：直接返回 JSON 格式的失败结果（{@link Result#failure}），
 *       供前端按统一格式处理未登录场景；</li>
 *   <li>对于非 Ajax 请求：输出一段自动跳转到登录页的 JavaScript，
 *   <li>允许 RememberMe 用户直接通过，不再强制重新登录。</li>
 * </ul>
 * </p>
 *
 * @version 1.0.0
 */
public class AuthenticatedFilter extends OncePerRequestFilter {
    private static final String JS = "<script type='text/javascript'>var wp=window.parent; if(wp!=null){while(wp.parent&&wp.parent!==wp){wp=wp.parent;}wp.location.href='%1$s';}else{window.location.href='%1$s';}</script>";

    /**
     * 登录页 URL，由 Shiro 配置注入
     */
    private String loginUrl;
    /**
     * 过滤逻辑：已登录或 RememberMe 直接放行；否则按 Ajax/非 Ajax 分别处理。
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param chain    过滤器链
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    @Override
    protected void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        Subject subject = SecurityUtils.getSubject();
        // 已登录或 RememberMe 用户直接放行
        if (subject.isAuthenticated() || subject.isRemembered()) {
            chain.doFilter(request, response);
        } else {
            WebUtils.saveRequest(request);
            String path = WebUtils.getContextPath((HttpServletRequest) request);
            String url = loginUrl;
            // 拼接 contextPath，避免在非根部署时登录页跳转错误
            if (StringUtils.isNotBlank(path) && path.length() > 1) {
                url = path + url;
            }

            if (isAjaxRequest((HttpServletRequest) request)) {
                // Ajax 请求返回 JSON 失败结果，前端按统一格式处理
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().print(JSON.toJSONString(Result.failure("您还没有登录!")));
            } else {
		response.setContentType("text/html;charset=UTF-8");
                // 非 Ajax 请求输出跳转脚本，自动重定向至登录页
                response.getWriter().write(new Formatter().format(JS, url).toString());
            }
        }
    }

    /**
     * 设置登录页 URL。
     *
     * @param loginUrl 登录页路径
     */
    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }
    
	/**
	 * 判断是否为Ajax请求 <功能详细描述>
	 * 
	 * @param request
	 * @return 是true, 否false
	 * @see [类、类#方法、类#成员]
	 */
	public static boolean isAjaxRequest(HttpServletRequest request) {
		String header = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(header);
	}

}
