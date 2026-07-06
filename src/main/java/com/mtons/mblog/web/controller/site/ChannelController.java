package com.mtons.mblog.web.controller.site;

import com.mtons.mblog.base.lang.Consts;
import com.mtons.mblog.base.utils.MarkdownUtils;
import com.mtons.mblog.modules.data.PostVO;
import com.mtons.mblog.modules.entity.Channel;
import com.mtons.mblog.modules.entity.PostAttribute;
import com.mtons.mblog.modules.service.ChannelService;
import com.mtons.mblog.modules.service.PostService;
import com.mtons.mblog.web.controller.BaseController;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 前台频道与文章详情控制器。
 *
 * <p>URL 前缀：无统一前缀，直接映射 {@code /channel/{id}} 与 {@code /post/{id}}</p>
 * <p>权限要求：公开访问，无需登录</p>
 *
 */
@Controller
public class ChannelController extends BaseController {
	@Autowired
	private ChannelService channelService;
	@Autowired
	private PostService postService;

	/**
	 * 频道文章列表页。 *
	 * @param id      频道 ID
	 * @param model   视图模型
	 * @param request HTTP 请求，用于读取 order、pageNo 参数
	 */
	@RequestMapping("/channel/{id}")
	public String channel(@PathVariable Integer id, ModelMap model,
			HttpServletRequest request) {
		// 提取排序方式，默认按最新排序
		String order = ServletRequestUtils.getStringParameter(request, "order", Consts.order.NEWEST);
		int pageNo = ServletRequestUtils.getIntParameter(request, "pageNo", 1);

		Channel channel = channelService.getById(id);
		model.put("channel", channel);
		model.put("order", order);
		model.put("pageNo", pageNo);
		return view(Views.POST_INDEX);
	}

	/**
	 * 文章详情页。
	 *
	 * <p>加载文章详情，若编辑器为 markdown 则渲染为 HTML 后再返回。
	 *
	 * @param id    文章 ID
	 * @param model 视图模型
	 */
	@RequestMapping("/post/{id:\\d*}")
	public String view(@PathVariable Long id, ModelMap model) {
		PostVO view = postService.get(id);

		Assert.notNull(view, "文章不存在或已被删除");

		// markdown 编辑器的内容需渲染为 HTML 展示
		if ("markdown".endsWith(view.getEditor())) {
			// 复制一份避免污染原始数据
			PostVO post = new PostVO();
			BeanUtils.copyProperties(view, post);
			post.setContent(MarkdownUtils.renderMarkdown(view.getContent()));
			view = post;
		}
		postService.identityViews(id);
		model.put("view", view);
		return view(Views.POST_VIEW);
	}
}
