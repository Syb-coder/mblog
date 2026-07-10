package com.sunblog.web.controller.admin;

import java.util.List;

import com.sunblog.base.lang.Result;
import com.sunblog.web.controller.BaseController;
import com.sunblog.modules.data.CommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sunblog.modules.service.CommentService;

/**
 * 后台评论管理控制器
 *
 * <h3>功能</h3>
 * 后台评论管理：分页列表 + 批量删除。
 * 评论的发表在前台 CommentController(site) 处理，后台只负责管理和删除。
 */
@Controller("adminCommentController")
@RequestMapping("/admin/comment")
public class CommentController extends BaseController {
	@Autowired
	private CommentService commentService;

	/**
	 * 评论列表页面（分页）。
	 *
	 * <p>从请求参数中提取分页参数，调用 {@link CommentService#paging4Admin} 获取后台评论分页数据。</p>
	 *
	 * @param model 视图模型
	 * @return 视图名 {@code /admin/comment/list}
	 */
	@RequestMapping("/list")
	public String list(ModelMap model) {
		Pageable pageable = wrapPageable();
		Page<CommentVO> page = commentService.paging4Admin(pageable);
		model.put("page", page);
		return "/admin/comment/list";
	}

	/**
	 * 批量删除评论。
	 *
	 * <p>支持传入多个评论 ID 进行批量删除，捕获异常返回失败信息。</p>
	 *
	 * @param id 评论 ID 列表
	 * @return 操作结果 JSON
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public Result delete(@RequestParam("id") List<Long> id) {
		Result data = Result.failure("操作失败");
		if (id != null) {
			try {
				commentService.delete(id);
				data = Result.success();
			} catch (Exception e) {
				data = Result.failure(e.getMessage());
			}
		}
		return data;
	}
}
