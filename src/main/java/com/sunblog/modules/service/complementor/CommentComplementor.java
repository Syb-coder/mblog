// 包声明：评论补全器所在包
package com.sunblog.modules.service.complementor;

// 导入 Guava Lists 工具类，快速创建 ArrayList 实例
import com.google.common.collect.Lists;
// 导入 Guava Sets 工具类，快速创建 HashSet 实例
import com.google.common.collect.Sets;
// 导入 Bean 属性拷贝工具类，用于实体转 VO
import com.sunblog.base.utils.BeanMapUtils;
// 导入 Spring 上下文工具类，非 Spring 管理对象获取 Bean 用
import com.sunblog.base.utils.SpringUtils;
// 导入评论 VO，补全后的评论视图对象
import com.sunblog.modules.data.CommentVO;
// 导入文章 VO，补全文章信息用
import com.sunblog.modules.data.PostVO;
// 导入用户 VO，补全作者信息用
import com.sunblog.modules.data.UserVO;
// 导入评论实体类，of() 方法的输入参数
import com.sunblog.modules.entity.Comment;
// 导入评论服务接口，补全父评论信息用
import com.sunblog.modules.service.CommentService;
// 导入文章服务接口，补全文章信息用
import com.sunblog.modules.service.PostService;
// 导入用户服务接口，补全作者信息用
import com.sunblog.modules.service.UserService;

// 导入 List 列表接口
import java.util.List;
// 导入 Map 映射接口
import java.util.Map;
// 导入 Set 集合接口
import java.util.Set;
// 导入 Stream 收集器，用于 toMap 转换
import java.util.stream.Collectors;

/**
 * 评论补全器 —— 把评论实体转换成完整的 VO（含作者、文章、父评论）
 *
 * <h3>为什么需要这个类？</h3>
 * Comment Entity 只存了 authorId、postId、pid（父评论 ID）这些外键，
 * 前端展示时需要的却是作者的昵称头像、文章的标题链接、被回复的内容。
 * CommentComplementor 通过链式调用（Builder 模式），把一次查询出的评论列表，
 * 批量补全作者信息、文章信息、父评论信息。
 *
 * <h3>使用示例</h3>
 * <pre>
 * List&lt;Comment&gt; entities = commentRepository.findAllByPostId(pageable, postId);
 * List&lt;CommentVO&gt; vos = CommentComplementor.of(entities)
 *     .flutBuildUser()     // 批量补全作者
 *     .flutBuildPost()     // 批量补全文章
 *     .flutBuildParent()   // 批量补全父评论
 *     .getComments();      // 获取结果
 * </pre>
 *
 * <h3>为什么用 SpringUtils.getBean 而不是 @Autowired？</h3>
 * CommentComplementor 不是 Spring Bean，它是由 static of() 工厂方法创建的普通对象。
 * 所以不能使用 @Autowired 注入依赖，只能通过 SpringUtils.getBean()（ApplicationContext 的静态封装）获取 Service。
 * 这是一种"非 Spring 管理对象获取 Spring Bean"的常见模式。
 */
// CommentComplementor：评论补全器，采用 Builder 链式调用模式批量补全评论 VO 的关联信息
public class CommentComplementor {
    // comments：补全后的评论 VO 列表，最终返回给调用方
    private List<CommentVO> comments = Lists.newArrayList();
    // userIds：所有评论的作者 ID 集合，用于批量查询用户信息
    private Set<Long> userIds = Sets.newHashSet();
    // postIds：所有评论所属文章的 ID 集合，用于批量查询文章信息
    private Set<Long> postIds = Sets.newHashSet();
    // parentIds：所有父评论的 ID 集合，用于批量查询父评论信息
    private Set<Long> parentIds = Sets.newHashSet();

    /**
     * <ul>
     *   <li>所有作者 ID（必填）</li>
     * </ul>
     * 实体转 VO 由 {@link BeanMapUtils#copy} 完成。</p>
     *
     * @param entities 评论实体列表
     */
    // of：静态工厂方法，将评论实体列表转换为补全器实例，收集所有外键 ID
    public static CommentComplementor of(List<Comment> entities) {
        // 创建补全器实例
        CommentComplementor builder = new CommentComplementor();

        // 遍历评论实体，收集外键 ID 并转换为 VO
        entities.forEach(po -> {
            // 如果 pid > 0，说明是回复评论，收集父评论 ID
            if (po.getPid() > 0) {
                builder.parentIds.add(po.getPid());
            }
            // 收集评论作者 ID，用于批量查询作者信息
            builder.userIds.add(po.getAuthorId());
            // 收集评论所属文章 ID，用于批量查询文章信息
            builder.postIds.add(po.getPostId());
            // 将实体转换为 VO 并加入结果列表
            builder.comments.add(BeanMapUtils.copy(po));
        });

        // 返回补全器实例，支持链式调用
        return builder;
    }

    // flutBuildUser：批量补全评论的作者信息，通过 UserService 批量查询用户
    public CommentComplementor flutBuildUser() {
        // 通过 SpringUtils 获取 UserService，批量查询用户信息返回 Map
        Map<Long, UserVO> map = SpringUtils.getBean(UserService.class).findMapByIds(this.userIds);
        // 遍历评论列表，将作者信息设置到每条评论 VO 中
        comments.forEach(p -> p.setAuthor(map.get(p.getAuthorId())));
        // 返回 this 支持链式调用
        return this;
    }

    // flutBuildPost：批量补全评论所属文章信息，通过 PostService 批量查询文章
    public CommentComplementor flutBuildPost() {
        // 通过 SpringUtils 获取 PostService，批量查询文章信息返回 Map
        Map<Long, PostVO> map = SpringUtils.getBean(PostService.class).findMapByIds(this.postIds);
        // 遍历评论列表，将文章信息设置到每条评论 VO 中
        comments.forEach(p -> p.setPost(map.get(p.getPostId())));
        // 返回 this 支持链式调用
        return this;
    }

    // flutBuildParent：批量补全评论的父评论信息，通过 CommentService 批量查询父评论
    public CommentComplementor flutBuildParent() {
        // 仅当存在父评论 ID 时才查询，避免无意义的数据库调用
        if (!parentIds.isEmpty()) {
            // 通过 SpringUtils 获取 CommentService，批量查询父评论信息返回 Map
            Map<Long, CommentVO> pm = SpringUtils.getBean(CommentService.class).findByIds(parentIds);

            // 遍历评论列表，将父评论信息设置到有父评论的评论 VO 中
            comments.forEach(c -> {
                // pid > 0 表示这是一条回复评论，需要设置父评论引用
                if (c.getPid() > 0) {
                    c.setParent(pm.get(c.getPid()));
                }
            });
        }
        // 返回 this 支持链式调用
        return this;
    }

    /**
     * @return 评论 VO 列表
     */
    // getComments：获取补全后的评论 VO 列表
    public List<CommentVO> getComments() {
        return comments;
    }

    /**
     * 转换为以评论 ID 为 key 的映射
     *
     * @return 以评论 id 为 key 的映射
     */
    // toMap：将评论列表转换为以评论 ID 为 key 的 Map，便于按 ID 快速查找
    public Map<Long, CommentVO> toMap() {
        return comments.stream().collect(Collectors.toMap(CommentVO::getId, n-> n));
    }

}