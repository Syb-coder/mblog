package com.sunblog.web.controller.site.user;

import com.sunblog.modules.data.AccountProfile;
import com.sunblog.modules.service.UserService;
import com.sunblog.web.controller.BaseController;
import com.sunblog.web.controller.site.Views;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户主页控制器，提供用户文章、评论、收藏等 Tab 页的访问入口。
 *
 */
@Controller
@RequestMapping("/users")
public class UsersController extends BaseController {
    @Autowired
    private UserService userService;

    /**
     * 用户文章列表页。
     *
     * @param userId  用户 ID
     * @param model   ModelMap
     * @param request HTTP 请求
     */
    @GetMapping(value = "/{userId}")
    public String posts(@PathVariable(value = "userId") Long userId,
                        ModelMap model, HttpServletRequest request) {
        return method(userId, Views.METHOD_POSTS, model, request);
    }

    /**
     * 通用方法，访问 users 目录下的页面。
     *
     * @param userId  用户 ID
     * @param method  调用方法标识（如 posts、comments、favorites）
     * @param model   ModelMap
     * @param request HTTP 请求
     */
    @GetMapping(value = "/{userId}/{method}")
    public String method(@PathVariable(value = "userId") Long userId,
                         @PathVariable(value = "method") String method,
                         ModelMap model, HttpServletRequest request) {
        model.put("pageNo", ServletRequestUtils.getIntParameter(request, "pageNo", 1));

        initUser(userId, model);
        return view(String.format(Views.USER_METHOD_TEMPLATE, method));
    }

    /**
     * 初始化用户主页所需的 model 数据。
     *
     * <p>加载目标用户信息，并判断当前登录用户是否为目标用户本人，
     * 用于控制页面元素（如编辑按钮）的显示权限。</p>
     *
     * @param userId 用户 ID
     * @param model  ModelMap
     */
    private void initUser(long userId, ModelMap model) {
        model.put("user", userService.get(userId));
        boolean owner = false;

        AccountProfile profile = getProfile();
        // 仅当前登录用户为目标用户本人时为 owner，避免越权显示编辑入口
        if (null != profile && profile.getId() == userId) {
            owner = true;
            // 刷新当前登录用户信息（头像、昵称等可能已被更新）
            putProfile(userService.findProfile(profile.getId()));
        }
        model.put("owner", owner);
    }

}
