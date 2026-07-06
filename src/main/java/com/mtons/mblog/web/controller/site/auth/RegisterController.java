package com.mtons.mblog.web.controller.site.auth;

import com.mtons.mblog.base.lang.Consts;
import com.mtons.mblog.base.lang.Result;
import com.mtons.mblog.modules.data.AccountProfile;
import com.mtons.mblog.modules.data.UserVO;
import com.mtons.mblog.web.controller.BaseController;
import com.mtons.mblog.web.controller.site.Views;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 用户注册 Controller。
 * <p>
 * 通过 {@code site.controls.register} 配置项控制是否启用注册入口；
 * 默认启用，便于在禁用注册的部署环境下移除相关路由。
 * </p>
 *
 */
@Controller
@ConditionalOnProperty(name = "site.controls.register", havingValue = "true", matchIfMissing = true)
public class RegisterController extends BaseController {

    /**
     * 渲染注册页面。
     * <p>
     * </p>
     *
     * @return 视图名称或重定向地址
     */
    @GetMapping("/register")
    public String view() {
        AccountProfile profile = getProfile();
        if (profile != null) {
            return String.format(Views.REDIRECT_USER_HOME, profile.getId());
        }
        return view(Views.REGISTER);
    }

    /**
     * 处理用户注册请求。
     * <p>
     * 注册成功后立即执行登录流程，并将用户重定向到个人主页；
     * 失败时回显注册表单数据，便于用户修正后重新提交。
     * </p>
     *
     * @param post  注册表单数据
     * @param model 视图模型
     * @return 视图名称或重定向地址
     */
    @PostMapping("/register")
    public String register(UserVO post, ModelMap model) {
        String view = view(Views.REGISTER);
        try {
            // 设置默认头像，避免新注册用户无头像导致页面异常
            post.setAvatar(Consts.AVATAR);
            userService.register(post);
            // 注册成功后自动完成登录，提升注册转化体验
            Result<AccountProfile> result = executeLogin(post.getUsername(), post.getPassword(), false);
            view = String.format(Views.REDIRECT_USER_HOME, result.getData().getId());
        } catch (Exception e) {
            // 回显表单数据以便用户修正后重新提交
            model.addAttribute("post", post);
            model.put("data", Result.failure(e.getMessage()));
        }
        return view;
    }

}
