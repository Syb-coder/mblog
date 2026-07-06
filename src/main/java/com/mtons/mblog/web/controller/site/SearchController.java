package com.mtons.mblog.web.controller.site;

import com.mtons.mblog.modules.data.PostVO;
import com.mtons.mblog.modules.service.PostSearchService;
import com.mtons.mblog.web.controller.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 前台文章搜索控制器。
 *
 * <p>URL 前缀：映射 {@code /search}</p>
 * <p>权限要求：公开访问，无需登录</p>
 *
 */
@Controller
public class SearchController extends BaseController {
	@Autowired
	private PostSearchService postSearchService;

	/**
	 * 文章搜索页面。
	 *
	 * <p>根据关键字 kw 调用 {@link PostSearchService#search} 进行全文检索，
	 *
	 * @param kw    搜索关键字
	 * @param model 视图模型
	 */
	@RequestMapping("/search")
	public String search(String kw, ModelMap model) {
		Pageable pageable = wrapPageable();
		try {
			if (StringUtils.isNotEmpty(kw)) {
				Page<PostVO> page = postSearchService.search(pageable, kw);
				model.put("results", page);
			}
		} catch (Exception e) {
			// 搜索异常不中断页面渲染，仅打印堆栈
			e.printStackTrace();
		}
		// 回显关键字供前端展示
		model.put("kw", kw);
		return view(Views.SEARCH);
	}

}
