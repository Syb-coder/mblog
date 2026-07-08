package com.mtons.mblog.modules.template.directive;

import com.mtons.mblog.modules.service.CommentService;
import com.mtons.mblog.modules.service.PostService;
import com.mtons.mblog.modules.template.DirectiveHandler;
import com.mtons.mblog.modules.template.TemplateDirective;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 侧边栏数据指令 —— 最新文章 / 最热文章 / 最新评论
 *
 * <h3>为什么用指令而不是 Controller 传入 Model？</h3>
 * 侧边栏的数据在每个页面都差不多（最新文章、标签云、最新评论），
 * 如果每个 Controller 都要把这些数据放入 Model，重复代码太多。
 * 用 &lt;@sidebar&gt; 指令，任何页面想加侧边栏数据就直接在模板里调用。
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
