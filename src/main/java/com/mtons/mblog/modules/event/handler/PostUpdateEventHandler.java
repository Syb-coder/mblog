package com.mtons.mblog.modules.event.handler;

import com.mtons.mblog.modules.event.PostUpdateEvent;
import com.mtons.mblog.modules.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 文章更新事件监听器
 * <p>
 * 职责：监听 {@link PostUpdateEvent} 事件，根据事件动作类型执行相应的后续处理：
 * <ul>
 *     <li>发布动作：更新作者的文章标识</li>
 *     <li>删除动作：清理收藏、评论、标签映射等关联数据</li>
 * </ul>
 * 通过 {@code @Async} 注解实现异步处理，避免阻塞主流程。
 *
 * @see PostUpdateEvent
 */
@Component
public class PostUpdateEventHandler implements ApplicationListener<PostUpdateEvent> {
    @Autowired
    private UserEventService userEventService;
    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private TagService tagService;

    /**
     * 处理文章更新事件
     * <p>
     * 根据事件动作类型分发处理：发布时更新作者文章标识；删除时清理关联数据。
     *
     * @param event 文章更新事件，包含文章 ID、作者 ID 和动作类型
     */
    @Async
    @Override
    public void onApplicationEvent(PostUpdateEvent event) {
        if (event == null) {
            return;
        }

        switch (event.getAction()) {
            case PostUpdateEvent.ACTION_PUBLISH:
                // 发布文章：标记作者有文章
                userEventService.identityPost(event.getUserId(), true);
                break;
            case PostUpdateEvent.ACTION_DELETE:
                // 删除文章：取消作者文章标识，并清理关联数据
                userEventService.identityPost(event.getUserId(), false);
                favoriteService.deleteByPostId(event.getPostId());
                commentService.deleteByPostId(event.getPostId());
                tagService.deteleMappingByPostId(event.getPostId());
                break;
        }
    }
}
