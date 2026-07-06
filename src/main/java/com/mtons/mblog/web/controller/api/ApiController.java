package com.mtons.mblog.web.controller.api;

import com.mtons.mblog.base.lang.Consts;
import com.mtons.mblog.base.lang.Result;
import com.mtons.mblog.base.utils.BeanMapUtils;
import com.mtons.mblog.modules.data.CommentVO;
import com.mtons.mblog.modules.data.PostVO;
import com.mtons.mblog.modules.service.CommentService;
import com.mtons.mblog.modules.service.PostService;
import com.mtons.mblog.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 前台 RESTful API 控制器，为前端页面提供数据接口。
 *
 * <p>URL 前缀：{@code /api}</p>
 * <p>权限要求：公开访问，使用 {@link RestController} 直接返回 JSON</p>
 *
 */
@RestController
@RequestMapping("/api")
public class ApiController extends BaseController {
    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;

    /**
     * 用户登录接口。
     *
     * <p>委托给 {@link BaseController#executeLogin} 执行 Shiro 登录流程，
     * 不启用 RememberMe。</p>
     *
     * @param username 用户名
     * @param password 明文密码
     * @return 登录结果，成功时携带 {@link AccountProfile}
     */
    @PostMapping(value = "/login")
    public Result login(String username, String password) {
        return executeLogin(username, password, false);
    }

    /**
     * 文章列表接口（分页）。
     *
     * <p>支持按排序方式（最新/热门）与频道 ID 筛选文章列表。</p>
     *
     * @param request HTTP 请求，用于读取 order、channelId 参数
     * @return 文章分页数据
     */
    @RequestMapping("/posts")
    public Page<PostVO> posts(HttpServletRequest request) {
        // 排序方式默认为最新
        String order = ServletRequestUtils.getStringParameter(request, "order", Consts.order.NEWEST);
        // 频道 ID 默认 0 表示不限频道
        int channelId = ServletRequestUtils.getIntParameter(request, "channelId", 0);
        // 通过 BeanMapUtils 将 order 字符串转换为数据库排序字段
        return postService.paging(wrapPageable(Sort.by(Sort.Direction.DESC, BeanMapUtils.postOrder(order))), channelId, null);
    }

    /**
     * 最新评论列表接口。
     *
     * @param size 返回条数，默认 6 条
     * @return 最新评论列表
     */
    @RequestMapping(value = "/latest_comments")
    public List<CommentVO> latestComments(@RequestParam(name = "size", defaultValue = "6") Integer size) {
        return commentService.findLatestComments(size);
    }
}
