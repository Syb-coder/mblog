package com.mtons.mblog.web.controller.admin;

import com.mtons.mblog.base.lang.Result;
import com.mtons.mblog.config.ContextStartup;
import com.mtons.mblog.modules.service.OptionsService;
import com.mtons.mblog.modules.service.PostSearchService;
import com.mtons.mblog.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * 后台系统配置管理控制器。
 *
 * 配置重载、搜索索引重置</p>
 * <p>URL 前缀：{@code /admin/options}</p>
 * <p>权限要求：由后台拦截器统一鉴权</p>
 *
 */
@Controller
@RequestMapping("/admin/options")
public class OptionsController extends BaseController {
	@Autowired
	private OptionsService optionsService;
	@Autowired
	private PostSearchService postSearchService;
	@Autowired
	private ContextStartup contextStartup;

	/**
	 * 配置首页页面。
	 *
	 * @param model 视图模型
	 * @return 视图名 {@code /admin/options/index}
	 */
	@RequestMapping("/index")
	public String index(ModelMap model) {
		return "/admin/options/index";
	}

	/**
	 * 更新站点配置项。
	 *
	 * <p>接收表单提交的键值对配置，更新后调用 {@link ContextStartup#reloadOptions(boolean)}
	 *
	 * @param body  配置键值对
	 * @param model 视图模型
	 * @return 视图名 {@code /admin/options/index}
	 */
	@RequestMapping("/update")
	public String update(@RequestParam Map<String, String> body, ModelMap model) {
		optionsService.update(body);
		contextStartup.reloadOptions(false);
		model.put("data", Result.success());
		return "/admin/options/index";
	}

	/* *
	 * @return 操作结果 JSON
	 */
	@RequestMapping("/reload_options")
	@ResponseBody
	public Result reloadOptions() {
		contextStartup.reloadOptions(false);
		contextStartup.resetChannels();
		return Result.success();
	}

	/**
	 * 重置文章搜索索引。
	 *
	 * <p>调用 {@link PostSearchService#resetIndexes()} 重建全量搜索索引，
	 * 适用于索引损坏或数据不一致时恢复。</p>
	 *
	 * @return 操作结果 JSON
	 */
	@RequestMapping("/reset_indexes")
	@ResponseBody
	public Result resetIndexes() {
		postSearchService.resetIndexes();
		return Result.success();
	}
}
