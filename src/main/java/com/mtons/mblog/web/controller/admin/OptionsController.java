package com.mtons.mblog.web.controller.admin;

package com.mtons.mblog.web.controller.admin;

import com.mtons.mblog.base.lang.Result;
import com.mtons.mblog.config.ContextStartup;
import com.mtons.mblog.modules.service.OptionsService;
import com.mtons.mblog.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 后台系统配置管理控制器。
 *
 * <p>URL 前缀：{@code /admin/options}</p>
 * <p>权限要求：由后台拦截器统一鉴权</p>
 */
@Controller
@RequestMapping("/admin/options")
public class OptionsController extends BaseController {
	@Autowired
	private OptionsService optionsService;
	@Autowired
	private ContextStartup contextStartup;

	@RequestMapping("/index")
	public String index(ModelMap model) {
		return "/admin/options/index";
	}

	@RequestMapping("/update")
	public String update(@RequestParam Map<String, String> body, ModelMap model) {
		optionsService.update(body);
		contextStartup.reloadOptions(false);
		model.put("data", Result.success());
		return "/admin/options/index";
	}

	@RequestMapping("/reload_options")
	@ResponseBody
	public Result reloadOptions() {
		contextStartup.reloadOptions(false);
		contextStartup.resetChannels();
		return Result.success();
	}
}
