package com.sunblog.web.controller.site;

/**
 * 前台视图名常量 —— 所有 FreeMarker 页面路径集中管理
 *
 * <h3>为什么需要这个接口？</h3>
 * 项目用了 FreeMarker 模板引擎渲染前端页面，Controller 方法返回的字符串
 * 就是模板文件的路径（如 "/auth/login" 对应 templates/auth/login.ftl）。
 *
 * 如果不集中管理，这些路径字符串会散落在各个 Controller 里：
 * <pre>
 * // 散落写法
 * return "/auth/login";
 * return "/index";
 * return "/user/method_posts";
 *
 * // 集中管理后
 * return Views.LOGIN;
 * return Views.INDEX;
 * return String.format(Views.USER_METHOD_TEMPLATE, Views.METHOD_POSTS);
 * </pre>
 *
 * <h3>模板文件在哪里？</h3>
 * src/main/resources/templates/ 目录下，与这些常量路径一一对应。
 * 例如 LOGIN = "/auth/login" → templates/auth/login.ftl
 *
 * <h3>REDIRICT_ 前缀的常量</h3>
 * "redirect:" 开头的是 Spring 重定向指令，不是模板路径。
 * Controller 返回 "redirect:/index" 时，浏览器会跳转到 /index 这个 URL。
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
