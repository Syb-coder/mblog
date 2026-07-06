package com.mtons.mblog.modules.template.directive;

import com.mtons.mblog.modules.service.CommentService;
import com.mtons.mblog.modules.service.PostService;
import com.mtons.mblog.modules.template.DirectiveHandler;
import com.mtons.mblog.modules.template.TemplateDirective;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 侧边栏数据指令
 * <p>
 * FreeMarker 使用方式：
 * <pre>{@code
 * <@sidebar method="latest_posts" size=6>
 *     <#list results as row>
 *         ${row.title}
 *     </#list>
 * </@sidebar>
 * }</pre>
 * <p>
 * 支持参数：
 * <ul>
 *     <li>method：数据获取方式，可选值 latest_posts（最新文章）、hottest_posts（最热文章）、latest_comments（最新评论）</li>
 *     <li>size：返回数据数量（默认 6）</li>
 * </ul>
 * 输出：results - 文章或评论列表
 */
@Component
public class SidebarDirective extends TemplateDirective {
    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;

    /**
     * 获取指令名称
     *
     * @return 指令名称 "sidebar"
     */
    @Override
    public String getName() {
        return "sidebar";
    }

    /**
     * 执行侧边栏数据查询
     *
     * @param handler 指令处理器
     * @throws Exception 异常
     */
    @Override
    public void execute(DirectiveHandler handler) throws Exception {
        int size = handler.getInteger("size", 6);
        String method = handler.getString("method", "post_latests");
        switch (method) {
            case "latest_posts":
                // 最新文章列表
                handler.put(RESULTS, postService.findLatestPosts(size));
                break;
            case "hottest_posts":
                // 最热文章列表
                handler.put(RESULTS, postService.findHottestPosts(size));
                break;
            case "latest_comments":
                // 最新评论列表
                handler.put(RESULTS, commentService.findLatestComments(size));
                break;
        }
        handler.render();
    }
}
