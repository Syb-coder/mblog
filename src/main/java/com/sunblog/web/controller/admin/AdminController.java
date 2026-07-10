// 包声明：该类位于后台管理控制器包下
package com.sunblog.web.controller.admin;

// 导入频道服务接口，用于统计频道数量
import com.sunblog.modules.service.ChannelService;
// 导入评论服务接口，用于统计评论数量
import com.sunblog.modules.service.CommentService;
// 导入文章服务接口，用于统计文章数量
import com.sunblog.modules.service.PostService;
// 导入用户服务接口，用于统计用户数量
import com.sunblog.modules.service.UserService;
// 导入 Spring 自动注入注解，实现依赖注入
import org.springframework.beans.factory.annotation.Autowired;
// 导入 Spring MVC 控制器注解，标识该类为控制器
import org.springframework.stereotype.Controller;
// 导入 Spring MVC 模型映射类，用于向视图传递数据
import org.springframework.ui.ModelMap;
// 导入 Spring MVC 请求映射注解，用于绑定 URL 路径
import org.springframework.web.bind.annotation.RequestMapping;

// 导入 Jakarta Servlet 的 HTTP 请求对象，用于获取请求信息
import jakarta.servlet.http.HttpServletRequest;

/**
 * 后台管理首页控制器，提供后台仪表盘页面。
 *
 * <p>URL 前缀：{@code /admin}</p>
 * <p>权限要求：需登录后台管理员账号访问（由 Shiro 拦截器统一控制）</p>
 *
 */
// @Controller 注解：将该类注册为 Spring MVC 控制器，由 Spring 容器管理
@Controller
// AdminController：后台管理首页控制器，负责仪表盘数据的加载与展示
public class AdminController {

    // @Autowired 注解：Spring 自动注入频道服务实例，无需手动创建
    @Autowired
    // channelService：频道服务，提供频道相关的业务操作（如统计频道总数）
    private ChannelService channelService;

    // @Autowired 注解：Spring 自动注入文章服务实例
    @Autowired
    // postService：文章服务，提供文章相关的业务操作（如统计文章总数）
    private PostService postService;

    // @Autowired 注解：Spring 自动注入评论服务实例
    @Autowired
    // commentService：评论服务，提供评论相关的业务操作（如统计评论总数）
    private CommentService commentService;

    // @Autowired 注解：Spring 自动注入用户服务实例
    @Autowired
    // userService：用户服务，提供用户相关的业务操作（如统计用户总数）
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
	// @RequestMapping("/admin")：将 /admin 路径映射到此方法，访问后台首页时触发
	@RequestMapping("/admin")
	// index 方法：处理后台首页请求，加载仪表盘所需的统计数据
	public String index(HttpServletRequest request, ModelMap model) {
		// 调用 pushSystemStatus 方法，将 JVM 内存和系统信息推送到模型中
		pushSystemStatus(request, model);

		// 查询频道总数，放入模型供前端页面展示
		model.put("channelCount", channelService.count());
		// 查询文章总数，放入模型供前端页面展示
        model.put("postCount", postService.count());
		// 查询评论总数，放入模型供前端页面展示
        model.put("commentCount", commentService.count());
		// 查询用户总数，放入模型供前端页面展示
        model.put("userCount", userService.count());

		// 返回视图名，渲染 /admin/index 模板页面（后台仪表盘）
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
	// pushSystemStatus 方法：采集 JVM 内存和操作系统信息，推送到视图模型
	private void pushSystemStatus(HttpServletRequest request, ModelMap model) {
		// 获取 JVM 当前空闲内存（字节），转为 float 类型便于后续计算百分比
        float freeMemory = (float) Runtime.getRuntime().freeMemory();
		// 获取 JVM 当前已分配的总内存（字节），即堆内存总量
        float totalMemory = (float) Runtime.getRuntime().totalMemory();
		// 计算已使用内存 = 总内存 - 空闲内存
        float usedMemory = (totalMemory - freeMemory);
		// 计算空闲内存占总内存的百分比，四舍五入取整
        float memPercent = Math.round(freeMemory / totalMemory * 100) ;
		// 获取操作系统名称，如 "Windows 11"、"Linux" 等
        String os = System.getProperty("os.name");
		// 获取当前运行的 Java 版本号，如 "17"、"21" 等
        String javaVersion = System.getProperty("java.version");

		// 将空闲内存（原始字节值）放入模型，供前端展示
        model.addAttribute("freeMemory", freeMemory);
		// 将总内存转换为 MB 单位（除以 1024 两次：字节→KB→MB），放入模型
        model.addAttribute("totalMemory", totalMemory / 1024 / 1024);
		// 将已使用内存转换为 MB 单位，放入模型
        model.addAttribute("usedMemory", usedMemory / 1024 / 1024);
		// 将空闲内存百分比放入模型，供前端展示进度条或文字
        model.addAttribute("memPercent", memPercent);
		// 将操作系统名称放入模型，供前端展示
        model.addAttribute("os", os);
		// 将 Java 版本号放入模型，供前端展示
        model.addAttribute("javaVersion", javaVersion);
	}
}