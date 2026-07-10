package com.sunblog.web.controller.site;

import jakarta.servlet.http.HttpServletRequest;

import com.sunblog.base.lang.Consts;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunblog.web.controller.BaseController;

/**
 * 前台首页控制器。
 *
 * <p>URL 前缀：映射根路径 {@code /} 与 {@code /index}</p>
 * <p>权限要求：公开访问，无需登录</p>
 *
 */
@Controller
public class IndexController extends BaseController{

	/**
	 * 站点首页。 *
	 * @param model   视图模型
	 * @param request HTTP 请求，用于读取 order、pageNo 参数
	 */
	@RequestMapping(value= {"/", "/index"})
	public String root(ModelMap model, HttpServletRequest request) {
		// 排序方式默认为最新
		String order = ServletRequestUtils.getStringParameter(request, "order", Consts.order.NEWEST);
		int pageNo = ServletRequestUtils.getIntParameter(request, "pageNo", 1);
		model.put("order", order);
		model.put("pageNo", pageNo);
		return view(Views.INDEX);
	}

}
