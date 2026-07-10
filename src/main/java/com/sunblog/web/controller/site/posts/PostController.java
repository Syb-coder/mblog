/**
 */
package com.sunblog.web.controller.site.posts;

import com.sunblog.base.lang.Consts;
import com.sunblog.base.lang.Result;
import com.sunblog.modules.data.AccountProfile;
import com.sunblog.modules.data.PostVO;
import com.sunblog.modules.service.ChannelService;
import com.sunblog.modules.service.PostService;
import com.sunblog.web.controller.BaseController;
import com.sunblog.web.controller.site.Views;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

/**
 * 前台文章操作 Controller
 * <p>
 * 处理已登录用户的文章编辑、发布、更新和删除操作。
 * 与后台 {@link com.sunblog.web.controller.admin.PostController} 的区别：
 * 前台 Controller 包含严格的作者身份校验（{@code Assert.isTrue(view.getAuthorId() == profile.getId())}），
 * 管理员后台则无此限制。
 * </p>
 * <p>
 * URL 前缀：{@code /post}
 * </p>
 */
@Controller
@RequestMapping("/post")
public class PostController extends BaseController {
	@Autowired
	private PostService postService;
	@Autowired
	private ChannelService channelService;

	/**
	 * 发布文章页
	 * @param id    文章 ID（编辑场景传入，新建场景为 null）
	 * @param model 视图模型
	 * @return 文章编辑页视图
	 */
	@GetMapping("/editing")
	public String view(Long id, ModelMap model) {
		model.put("channels", channelService.findAll(Consts.STATUS_NORMAL));
		model.put("editing", true);
		String editor = siteOptions.getValue("editor");
		if (null != id && id > 0) {
			AccountProfile profile = getProfile();
			PostVO view = postService.get(id);

			Assert.notNull(view, "该文章已被删除");
			Assert.isTrue(view.getAuthorId() == profile.getId(), "该文章不属于你");

			Assert.isTrue(view.getChannel().getStatus() == Consts.STATUS_NORMAL, "请在后台编辑此文章");
			model.put("view", view);

			if (StringUtils.isNoneBlank(view.getEditor())) {
				editor = view.getEditor();
			}
		}
		model.put("editor", editor);
		return view(Views.POST_EDITING);
	}

	/**
	 * 提交发布
	 * @param post 文章表单对象
	 * @return 重定向到用户主页
	 */
	@PostMapping("/submit")
	public String post(PostVO post) {
		Assert.notNull(post, "参数不完整");
		Assert.state(StringUtils.isNotBlank(post.getTitle()), "标题不能为空");
		Assert.state(StringUtils.isNotBlank(post.getContent()), "内容不能为空");
		Assert.state(post.getTitle().length() <= 64, "标题不能超过64个字符");
		AccountProfile profile = getProfile();
		post.setAuthorId(profile.getId());

		if (post.getId() > 0) {
			PostVO exist = postService.get(post.getId());
			Assert.notNull(exist, "文章不存在");
			Assert.isTrue(exist.getAuthorId() == profile.getId(), "该文章不属于你");

			postService.update(post);
		} else {
			postService.post(post);
		}
		return String.format(Views.REDIRECT_USER_HOME, profile.getId());
	}

	/**
	 * 删除文章
	 * @param id 文章 ID
	 * @return 操作结果
	 */
	@RequestMapping("/delete/{id}")
	@ResponseBody
	public Result<Void> delete(@PathVariable Long id) {
		Result<Void> data;
		try {
			postService.delete(id, getProfile().getId());
			data = Result.success();
		} catch (Exception e) {
			data = Result.failure(e.getMessage());
		}
		return data;
	}

}