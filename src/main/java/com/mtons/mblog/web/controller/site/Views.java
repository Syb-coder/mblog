package com.mtons.mblog.web.controller.site;

/**
 * 前台视图名常量定义接口。 *
 * {@link #TAG_VIEW} 表示标签详情页。</p>
 *
 */
public interface Views {
    String LOGIN = "/auth/login";

    String REGISTER = "/auth/register";

    String INDEX = "/index";

    /**
     * <p>占位符 %s 由 {@link #METHOD_POSTS}、{@link #METHOD_COMMENTS}、
     */
    String USER_METHOD_TEMPLATE = "/user/method_%s";

    /**
     * 用户主页方法标识 - 文章列表
     */
    String METHOD_POSTS = "posts";

    /**
     * 用户主页方法标识 - 评论列表
     */
    String METHOD_COMMENTS = "comments";

    /**
     * 用户主页方法标识 - 收藏列表
     */
    String METHOD_FAVORITES = "favorites";

    String SETTINGS_AVATAR = "/settings/avatar";

    String SETTINGS_PASSWORD = "/settings/password";

    String SETTINGS_PROFILE = "/settings/profile";

    String TAG_INDEX = "/tag/index";

    String TAG_VIEW = "/tag/view";

    String SEARCH = "/search";

    String POST_EDITING = "/channel/editing";

    String POST_INDEX = "/channel/index";

    String POST_VIEW = "/channel/view";

    /**
     * 重定向到用户主页：{@code redirect:/users/{id}}
     * <p>占位符 %d 为用户 ID。</p>
     */
    String REDIRECT_USER_HOME = "redirect:/users/%d";

    /**
     * 重定向到首页：{@code redirect:/index}
     */
    String REDIRECT_INDEX = "redirect:/index";
}
