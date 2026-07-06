package com.mtons.mblog.web.controller.admin;

import com.mtons.mblog.modules.service.ChannelService;
import com.mtons.mblog.modules.service.CommentService;
import com.mtons.mblog.modules.service.PostService;
import com.mtons.mblog.modules.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 后台管理首页控制器，提供后台仪表盘页面。
 *
 * <p>URL 前缀：{@code /admin}</p>
 * <p>权限要求：需登录后台管理员账号访问（由 Shiro 拦截器统一控制）</p>
 *
 */
@Controller
public class AdminController {
    @Autowired
    private ChannelService channelService;
    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;

	/**
	 * 后台首页 / 仪表盘页面。
	 *
	 * <p>向模型中注入系统统计数据（频道数、文章数、评论数、用户数）以及
	 *
	 * @param request HTTP 请求对象
	 * @param model   视图模型
	 * @return 视图名 {@code /admin/index}
	 */
	@RequestMapping("/admin")
	public String index(HttpServletRequest request, ModelMap model) {
		// 推送 JVM 与系统运行状态信息到模型
		pushSystemStatus(request, model);
		model.put("channelCount", channelService.count());
        model.put("postCount", postService.count());
        model.put("commentCount", commentService.count());
        model.put("userCount", userService.count());
		return "/admin/index";
	}

	/**
	 * 推送系统运行状态到视图模型。
	 *
	 * <p>采集以下信息：</p>
	 * <ul>
	 * </ul>
	 *
	 * @param request HTTP 请求对象
	 * @param model   视图模型
	 */
	private void pushSystemStatus(HttpServletRequest request, ModelMap model) {
        float freeMemory = (float) Runtime.getRuntime().freeMemory();
        float totalMemory = (float) Runtime.getRuntime().totalMemory();
        float usedMemory = (totalMemory - freeMemory);
        float memPercent = Math.round(freeMemory / totalMemory * 100) ;
        String os = System.getProperty("os.name");
        String javaVersion = System.getProperty("java.version");

        model.addAttribute("freeMemory", freeMemory);
        // 转换为 MB 单位展示
        model.addAttribute("totalMemory", totalMemory / 1024 / 1024);
        model.addAttribute("usedMemory", usedMemory / 1024 / 1024);
        model.addAttribute("memPercent", memPercent);
        model.addAttribute("os", os);
        model.addAttribute("javaVersion", javaVersion);
	}
}
