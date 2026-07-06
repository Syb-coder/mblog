package com.mtons.mblog.web.controller.site.auth;

import com.mtons.mblog.base.lang.Result;
import com.mtons.mblog.modules.data.AccountProfile;
import com.mtons.mblog.web.controller.BaseController;
import com.mtons.mblog.web.controller.site.Views;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 登录 Controller
 * <p>
 * 职责：处理用户登录页面跳转与登录表单提交。
 * 登录成功后跳转至用户个人主页，失败时返回登录页并展示错误提示。
 * </p>
 */
@Controller
public class LoginController extends BaseController {

    /**
     * 跳转登录页
     * <p>
     * 处理 GET /login 请求，返回登录视图名称。
     * </p>
     * @return 登录页面视图名称
     */
	@GetMapping(value = "/login")
	public String view() {
		return view(Views.LOGIN);
	}

    /**
     * 提交登录
     * <p>
     * 处理 POST /login 请求，调用父级登录逻辑执行认证。
     * 成功则重定向到用户主页，失败则回显错误信息到登录页。
     * </p>
     * @param username   用户名
     * @param password   密码
     * @param rememberMe 是否记住登录状态，默认 false
     * @param model       视图模型容器，用于回填错误信息
     * @return 视图名称：成功时为重定向 URL，失败时为登录页视图
     */
	@PostMapping(value = "/login")
	public String login(String username,
                        String password,
                        @RequestParam(value = "rememberMe",defaultValue = "0") Boolean rememberMe,
                        ModelMap model) {
		// 默认返回登录页视图，登录成功时再覆盖为重定向 URL
		String view = view(Views.LOGIN);

        Result<AccountProfile> result = executeLogin(username, password, rememberMe);

        if (result.isOk()) {
            // 登录成功：使用用户 ID 拼装个人主页地址
            view = String.format(Views.REDIRECT_USER_HOME, result.getData().getId());
        } else {
            // 登录失败：将 Result 中的提示信息写入 Model，供页面展示
            model.put("message", result.getMessage());
        }
        return view;
	}

}
