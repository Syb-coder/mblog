package com.sunblog.web.controller.admin;

import com.sunblog.base.lang.Consts;
import com.sunblog.base.lang.Result;
import com.sunblog.modules.data.AccountProfile;
import com.sunblog.modules.data.PostVO;
import com.sunblog.modules.service.ChannelService;
import com.sunblog.modules.service.PostService;
import com.sunblog.web.controller.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 后台文章管理控制器
 *
 * <h3>功能</h3>
 * 后台文章 CRUD：列表查询（支持按栏目和标题筛选）、编辑页面、新增/更新提交、批量删除。
 *
 * <h3>和前台 ChannelController 的区别</h3>
 * PostController（admin）→ 使用 postService.paging4Admin → 不走 @PostStatusFilter，能看到所有状态
 * ChannelController（site）→ 使用 postService.paging → 走 @PostStatusFilter，只看正常文章
 *
 * <h3>@Controller("adminPostController")</h3>
 * 这里指定了 Bean 名称，避免和前台同名 Controller（如果有的话）冲突。
 */
@Controller("adminPostController")
@RequestMapping("/admin/post")
public class PostController extends BaseController {
	@Autowired
	private PostService postService;
	@Autowired
	private ChannelService channelService;

	/**
	 * 文章列表页面（支持按标题、ID、频道筛选）。
	 *
	 * <ul>
	 *   <li>channelId：频道筛选</li>
	 * </ul>
	 *
	 * @param title   标题关键字
	 * @param model   视图模型
	 * @param request HTTP 请求，用于读取 id、channelId 参数
	 * @return 视图名 {@code /admin/post/list}
	 */
	@RequestMapping("/list")
	public String list(String title, ModelMap model, HttpServletRequest request) {
		long id = ServletRequestUtils.getLongParameter(request, "id", Consts.ZERO);
		int channelId = ServletRequestUtils.getIntParameter(request, "channelId", Consts.ZERO);

		Pageable pageable = wrapPageable(Sort.by(Sort.Direction.DESC, "created"));
		Page<PostVO> page = postService.paging4Admin(pageable, channelId, title);
		model.put("page", page);
		model.put("title", title);
		model.put("id", id);
		model.put("channelId", channelId);
		model.put("channels", channelService.findAll(Consts.IGNORE));
		return "/admin/post/list";
	}

	/**
	 * 跳转到文章编辑页面。
	 *
	 * <p>当传入有效 id 时加载文章详情用于编辑；未传 id 时进入新增页面。
	 * 编辑器类型优先级：文章自带 editor > 站点配置 editor。</p>
	 *
	 * @param id    文章 ID，为 null 或 <=0 时为新增
	 * @param model 视图模型
	 * @return 视图名 {@code /admin/post/view}
	 */
	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public String toUpdate(Long id, ModelMap model) {
		// 读取站点默认编辑器配置
		String editor = siteOptions.getValue("editor");
		if (null != id && id > 0) {
			PostVO view = postService.get(id);
			// 文章已指定编辑器时优先使用文章自身的编辑器类型
			if (StringUtils.isNoneBlank(view.getEditor())) {
				editor = view.getEditor();
			}
			model.put("view", view);
		}
		model.put("editor", editor);
		model.put("channels", channelService.findAll(Consts.IGNORE));
		return "/admin/post/view";
	}

	/**
	 * 提交文章新增或更新。
	 *
	 * <p>判断逻辑：</p>
	 * <ul>
	 *   <li>id > 0：更新已有文章</li>
	 *   <li>id <= 0：新增文章，需设置当前登录用户为作者</li>
	 * </ul>
	 *
	 * @param post 文章表单对象
	 * @return 重定向到文章列表
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String subUpdate(PostVO post) {
		if (post != null) {
			if (post.getId() > 0) {
				postService.update(post);
			} else {
				// 新增文章：需绑定当前登录用户为作者
				AccountProfile profile = getProfile();
				post.setAuthorId(profile.getId());
				postService.post(post);
			}
		}
		// 重定向避免重复提交
		return "redirect:/admin/post/list";
	}

	/**
	 * 批量删除文章。
	 *
	 * <p>支持传入多个文章 ID 进行批量删除，捕获异常返回失败信息。</p>
	 *
	 * @param id 文章 ID 列表
	 * @return 操作结果 JSON
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public Result delete(@RequestParam("id") List<Long> id) {
		Result data = Result.failure("操作失败");
		if (id != null) {
			try {
				postService.delete(id);
				data = Result.success();
			} catch (Exception e) {
				data = Result.failure(e.getMessage());
			}
		}
		return data;
	}
}
