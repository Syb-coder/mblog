/**
 * 前台评论 Controller（RESTful JSON 接口）
 * <p>
 * 负责前台用户的评论提交、列表查看和删除操作。
 * 与后台 {@link com.sunblog.web.controller.admin.CommentController} 的区别：
 * 前台 Controller 包含严格的作者身份校验，仅允许删除本人评论。
 * 通过 {@code site.controls.comment} 配置项控制是否启用评论功能。
 * </p>
 * <p>
 * URL 前缀：{@code /comment}
 * </p>
 */
package com.sunblog.web.controller.site.comment;

import com.sunblog.base.lang.Result;
import com.sunblog.modules.data.AccountProfile;
import com.sunblog.modules.data.CommentVO;
import com.sunblog.modules.service.CommentService;
import com.sunblog.web.controller.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import jakarta.servlet.http.HttpServletRequest;

    /**
     * 仅在配置项 {@code site.controls.comment=true}（或缺省时）启用，
     * 用于支撑前台评论功能。
     */
@RestController
@RequestMapping("/comment")
@ConditionalOnProperty(name = "site.controls.comment", havingValue = "true", matchIfMissing = true)
public class CommentController extends BaseController {
    @Autowired
    private CommentService commentService;

    /**
     * 获取某篇文章的评论列表（分页）
     *
     * @param toId 目标文章 ID
     * @return 评论分页数据，按 id 倒序排列
     */
    @RequestMapping("/list/{toId}")
    public Page<CommentVO> view(@PathVariable Long toId) {
        // 按 id 倒序，保证最新评论排在前面
        Pageable pageable = wrapPageable(Sort.by(Sort.Direction.DESC, "id"));
        return commentService.pagingByPostId(pageable, toId);
    }

    /**
     * 提交评论。
     *
     * <p>需登录后才能操作；对评论内容进行 HTML 转义以防止 XSS 攻击。</p>
     *
     * @param toId    目标文章 ID
     * @param text    评论内容
     * @param request HTTP 请求，用于获取父评论 ID（pid）
     * @return 操作结果
     */
    @RequestMapping("/submit")
    public Result post(Long toId, String text, HttpServletRequest request) {
        if (!isAuthenticated()) {
            return Result.failure("请先登录在进行操作");
        }

        // pid 默认为 0 表示该评论为顶层评论（非回复他人）
        long pid = ServletRequestUtils.getLongParameter(request, "pid", 0);

        // 参数校验：文章 ID 必须大于 0 且内容非空
        if (toId <= 0 || StringUtils.isBlank(text)) {
            return Result.failure("操作失败");
        }

        AccountProfile profile = getProfile();

        CommentVO c = new CommentVO();
        c.setPostId(toId);
        // HTML 转义防止 XSS 注入
        c.setContent(HtmlUtils.htmlEscape(text));
        c.setAuthorId(profile.getId());

        c.setPid(pid);

        commentService.post(c);

        return Result.successMessage("发表成功");
    }

    /**
     * 删除评论。
     *
     * <p>仅允许删除本人发布的评论。</p>
     *
     * @param id 评论 ID
     * @return 操作结果
     */
    @RequestMapping("/delete")
    public Result delete(@RequestParam(name = "id") Long id) {
        Result data;
        try {
            // 删除时传入当前登录用户 ID，由 service 层校验作者身份，避免越权删除他人评论
            commentService.delete(id, getProfile().getId());
            data = Result.success();
        } catch (Exception e) {
            data = Result.failure(e.getMessage());
        }
        return data;
    }
}
