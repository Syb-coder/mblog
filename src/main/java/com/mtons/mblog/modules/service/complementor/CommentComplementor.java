package com.mtons.mblog.modules.service.complementor;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mtons.mblog.base.utils.BeanMapUtils;
import com.mtons.mblog.base.utils.SpringUtils;
import com.mtons.mblog.modules.data.CommentVO;
import com.mtons.mblog.modules.data.PostVO;
import com.mtons.mblog.modules.data.UserVO;
import com.mtons.mblog.modules.entity.Comment;
import com.mtons.mblog.modules.service.CommentService;
import com.mtons.mblog.modules.service.PostService;
import com.mtons.mblog.modules.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * </p>
 * <p>
 * 使用方式（链式调用）：
 * <pre>{@code
 * CommentComplementor.of(comments)
 *     .getComments();       // 取结果
 * }</pre>
 * <p>
 * 实现要点：
 * <ul>
 *   <li>通过 {@link SpringUtils#getBean} 在非 Spring 管理的对象中获取 Service Bean</li>
 * </ul>
 * </p>
 *
 * @version : 1.0
 * @date : 2020/3/26
 */
public class CommentComplementor {
    private List<CommentVO> comments = Lists.newArrayList();
    private Set<Long> userIds = Sets.newHashSet();
    private Set<Long> postIds = Sets.newHashSet();
    private Set<Long> parentIds = Sets.newHashSet();

    /**
     * <ul>
     *   <li>所有作者 ID（必填）</li>
     * </ul>
     * 实体转 VO 由 {@link BeanMapUtils#copy} 完成。</p>
     *
     * @param entities 评论实体列表
     */
    public static CommentComplementor of(List<Comment> entities) {
        CommentComplementor builder = new CommentComplementor();

        entities.forEach(po -> {
            if (po.getPid() > 0) {
                builder.parentIds.add(po.getPid());
            }
            builder.userIds.add(po.getAuthorId());
            builder.postIds.add(po.getPostId());
            builder.comments.add(BeanMapUtils.copy(po));
        });

        return builder;
    }

    public CommentComplementor flutBuildUser() {
        Map<Long, UserVO> map = SpringUtils.getBean(UserService.class).findMapByIds(this.userIds);
        comments.forEach(p -> p.setAuthor(map.get(p.getAuthorId())));
        return this;
    }

    public CommentComplementor flutBuildPost() {
        Map<Long, PostVO> map = SpringUtils.getBean(PostService.class).findMapByIds(this.postIds);
        comments.forEach(p -> p.setPost(map.get(p.getPostId())));
        return this;
    }

    public CommentComplementor flutBuildParent() {
        if (!parentIds.isEmpty()) {
            Map<Long, CommentVO> pm = SpringUtils.getBean(CommentService.class).findByIds(parentIds);

            comments.forEach(c -> {
                if (c.getPid() > 0) {
                    c.setParent(pm.get(c.getPid()));
                }
            });
        }
        return this;
    }

    /* *
     * @return 评论 VO 列表
     */
    public List<CommentVO> getComments() {
        return comments;
    }

    /**
     * 转换为以评论 ID 为 key 的映射
     *
     * @return 以评论 id 为 key 的映射
     */
    public Map<Long, CommentVO> toMap() {
        return comments.stream().collect(Collectors.toMap(CommentVO::getId, n-> n));
    }

}
