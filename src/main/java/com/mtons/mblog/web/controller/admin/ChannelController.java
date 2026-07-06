package com.mtons.mblog.web.controller.admin;

import com.mtons.mblog.base.lang.Result;
import com.mtons.mblog.base.lang.Consts;
import com.mtons.mblog.config.ContextStartup;
import com.mtons.mblog.modules.entity.Channel;
import com.mtons.mblog.modules.service.ChannelService;
import com.mtons.mblog.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 后台频道管理控制器。
 *
 * <p>URL 前缀：{@code /admin/channel}</p>
 * <p>权限要求：建议配置 {@code @RequiresPermissions("channel:list/update/delete/weight")}，
 * 当前注解已注释，由后台拦截器统一鉴权</p>
 *
 */
@Controller("adminChannelController")
@RequestMapping("/admin/channel")
public class ChannelController extends BaseController {
	@Autowired
	private ChannelService channelService;
	@Autowired
	private ContextStartup contextStartup;

	/**
	 * 频道列表页面。 *
	 * @param model 视图模型
	 * @return 视图名 {@code /admin/channel/list}
	 */
	@RequestMapping("/list")
	public String list(ModelMap model) {
		model.put("list", channelService.findAll(Consts.IGNORE));
		return "/admin/channel/list";
	}

	/* *
	 * @param id    频道 ID，可为 null（新增场景）
	 * @param model 视图模型
	 * @return 视图名 {@code /admin/channel/view}
	 */
	@RequestMapping("/view")
	public String view(Integer id, ModelMap model) {
		if (id != null) {
			Channel view = channelService.getById(id);
			model.put("view", view);
		}
		return "/admin/channel/view";
	}

	/**
	 * 更新或新增频道。 *
	 * @param view 频道表单对象
	 * @return 重定向到频道列表
	 */
	@RequestMapping("/update")
//	@RequiresPermissions("channel:update")  // 所需权限：频道更新
	public String update(Channel view) {
		if (view != null) {
			channelService.update(view);

			contextStartup.resetChannels();
		}
		// 重定向避免重复提交
		return "redirect:/admin/channel/list";
	}

	/**
	 * 调整频道权重（用于控制排序与推荐）。 *
	 * @param id      频道 ID
	 * @param request HTTP 请求，用于读取 weight 参数
	 * @return 操作结果 JSON
	 */
	@RequestMapping("/weight")
	@ResponseBody
	public Result weight(@RequestParam Integer id, HttpServletRequest request) {
		int weight = ServletRequestUtils.getIntParameter(request, "weight", Consts.FEATURED_ACTIVE);
		channelService.updateWeight(id, weight);
		contextStartup.resetChannels();
		return Result.success();
	}

	/**
	 * 删除频道。 *
	 * @param id 频道 ID
	 * @return 操作结果 JSON
	 */
	@RequestMapping("/delete")
	@ResponseBody
//	@RequiresPermissions("channel:delete")  // 所需权限：频道删除
	public Result delete(Integer id) {
		Result data = Result.failure("操作失败");
		if (id != null) {
			try {
				channelService.delete(id);
				data = Result.success();

				contextStartup.resetChannels();
			} catch (Exception e) {
				data = Result.failure(e.getMessage());
			}
		}
		return data;
	}

}
