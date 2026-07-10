package com.sunblog.web.controller.site.auth;

import com.sunblog.web.controller.BaseController;
import com.sunblog.web.controller.site.Views;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 登出 Controller
 * <p>
 * </p>
 */
@Controller
public class LogoutController extends BaseController {

    /**
     * 退出登录
     * <p>
     * 流程：
     * 1. 调用 Shiro Subject.logout() 销毁会话与认证信息；
     * 3. 重定向到站点首页。
     * </p>
     * @return 重定向首页视图名称
     */
    @RequestMapping("/logout")
    public String logout(HttpServletResponse response) {
        SecurityUtils.getSubject().logout();
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        response.setDateHeader("Expires", 0); // Proxies.
        return Views.REDIRECT_INDEX;
    }

}
