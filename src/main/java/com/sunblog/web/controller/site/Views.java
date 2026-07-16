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
    // → templates/classic/auth/login.ftl     登录页面
    String LOGIN = "/auth/login";

    // → templates/classic/auth/register.ftl  注册页面
    String REGISTER = "/auth/register";

    // → templates/classic/index.ftl          首页（文章列表）
    String INDEX = "/index";

    // → templates/classic/user/method_posts.ftl 或 method_comments.ftl
    // 占位符 %s 由 METHOD_POSTS 或 METHOD_COMMENTS 替换
    String USER_METHOD_TEMPLATE = "/user/method_%s";

    // 替换 USER_METHOD_TEMPLATE 中的 %s → /user/method_posts
    // → templates/classic/user/method_posts.ftl  用户主页-文章列表
    String METHOD_POSTS = "posts";

    // 替换 USER_METHOD_TEMPLATE 中的 %s → /user/method_comments
    // → templates/classic/user/method_comments.ftl  用户主页-评论列表
    String METHOD_COMMENTS = "comments";

    // → templates/classic/settings/avatar.ftl   个人设置-修改头像
    String SETTINGS_AVATAR = "/settings/avatar";

    // → templates/classic/settings/password.ftl 个人设置-修改密码
    String SETTINGS_PASSWORD = "/settings/password";

    // → templates/classic/settings/profile.ftl  个人设置-修改资料
    String SETTINGS_PROFILE = "/settings/profile";

    // → templates/classic/tag/index.ftl         标签列表页
    String TAG_INDEX = "/tag/index";

    // → templates/classic/tag/view.ftl          标签详情页（该标签下的文章）
    String TAG_VIEW = "/tag/view";

    // → templates/classic/search.ftl            搜索结果页（模板可能未创建）
    String SEARCH = "/search";

    // → templates/classic/channel/editing.ftl   文章编辑/发布页
    String POST_EDITING = "/channel/editing";

    // → templates/classic/channel/index.ftl     频道下的文章列表页
    String POST_INDEX = "/channel/index";

    // → templates/classic/channel/view.ftl      文章详情页
    String POST_VIEW = "/channel/view";

    // 不是模板，是浏览器重定向指令
    // 浏览器跳转到 /users/{id}，例如 /users/5 → 用户ID为5的个人主页
    String REDIRECT_USER_HOME = "redirect:/users/%d";

    // 不是模板，是浏览器重定向指令
    // 浏览器跳转到首页 /index
    String REDIRECT_INDEX = "redirect:/index";
}