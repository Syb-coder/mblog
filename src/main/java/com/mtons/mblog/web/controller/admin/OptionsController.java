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
 * 后台系统配置管理控制器
 *
 * <h3>功能</h3>
 * 管理站点配置项（站点名称、SEO 描述、存储方案、编辑器类型等）。
 * 配置项以 key-value 形式存储在 Options 表中。
 *
 * <h3>关键流程</h3>
 * update() → 保存配置 → contextStartup.reloadOptions() 重新加载
 * reloadOptions() → 手动触发配置重载（用于前台直接修改数据库后的同步）
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
