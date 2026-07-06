package com.mtons.mblog.modules.event;

import org.springframework.context.ApplicationEvent;

/**
 * 文章更新事件
 * <p>
 * 事件载荷：文章 ID（postId）、作者 ID（userId）、动作类型（action）。
 * <p>
 * 发布时机：文章发布或删除时由业务层发布，用于触发后续异步处理流程。
 * <p>
 * 监听器处理逻辑（见 {@link com.mtons.mblog.modules.event.handler.PostUpdateEventHandler}）：
 * <ul>
 *     <li>发布动作：标记作者的文章统计标识</li>
 *     <li>删除动作：清理收藏、评论、标签关联数据</li>
 * </ul>
 */
public class PostUpdateEvent extends ApplicationEvent {
    /** 动作类型：发布文章 */
    public final static int ACTION_PUBLISH = 1;
    /** 动作类型：删除文章 */
    public final static int ACTION_DELETE = 2;

    /** 文章 ID */
    private long postId;
    /** 作者 ID */
    private long userId;
    /** 动作类型，默认为发布 */
    private int action = ACTION_PUBLISH;

    /**
     * 构造事件对象
     *
     * @param source 事件源，通常为发布事件的对象
     */
    public PostUpdateEvent(Object source) {
        super(source);
    }

    /**
     * 获取文章 ID
     *
     * @return 文章 ID
     */
    public long getPostId() {
        return postId;
    }

    /**
     * 设置文章 ID
     *
     * @param postId 文章 ID
     */
    public void setPostId(long postId) {
        this.postId = postId;
    }

    /**
     * 获取作者 ID
     *
     * @return 作者 ID
     */
    public long getUserId() {
        return userId;
    }

    /**
     * 设置作者 ID
     *
     * @param userId 作者 ID
     */
    public void setUserId(long userId) {
        this.userId = userId;
    }

    /**
     * 获取动作类型
     *
     * @return 动作类型（{@link #ACTION_PUBLISH} 或 {@link #ACTION_DELETE}）
     */
    public int getAction() {
        return action;
    }

    /**
     * 设置动作类型
     *
     * @param action 动作类型
     */
    public void setAction(int action) {
        this.action = action;
    }
}
