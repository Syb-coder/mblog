package com.sunblog.base.utils;

import com.sunblog.base.lang.Consts;
import com.sunblog.modules.data.*;
import com.sunblog.modules.entity.*;
import org.springframework.beans.BeanUtils;

/**
 * 实体 ↔ 视图对象转换工具
 *
 * <h3>为什么需要这个类？</h3>
 * JPA Entity 是持久化层的对象，直接暴露给前端有风险（比如密码字段泄露），
 * 同时 Entity 有些字段不适合直接展示（如 extend 扩展字段）。
 * 这个类负责把 Entity 转换成 VO（View Object），给前端安全的、精简的数据。
 *
 * <h3>核心功能</h3>
 * <pre>
 * Entity（PO）           BeanMapUtils              VO（视图对象）
 * ┌──────────┐                                     ┌──────────┐
 * │ User     │  ──copy()──→ 忽略 password,roles  → │ UserVO   │
 * │          │  ──copyPassport()──→ 提取最小字段  → │ AccountProfile │
 * ├──────────┤                                     ├──────────┤
 * │ Post     │  ──copy()──→ 浅拷贝                → │ PostVO   │
 * ├──────────┤                                     ├──────────┤
 * │ Comment  │  ──copy()──→ 浅拷贝                → │ CommentVO│
 * ├──────────┤                                     ├──────────┤
 * │ Tag      │  ──copy()──→ 浅拷贝                → │ TagVO    │
 * └──────────┘                                     └──────────┘
 * </pre>
 *
 * <h3>示例：为什么 copy(User) 要忽略密码？</h3>
 * 假设首页需要显示文章的作者信息，会调用 userService.findByUserId()
 * 返回 User 实体。如果直接把这个 User 返回给前端，
 * then 作者密码就会暴露在 HTML/JSON 中。
 * 通过 BeanMapUtils.copy(User) 转成 UserVO 再返回，密码字段就被跳过了。
 */
public class BeanMapUtils {
    /**
     * 用户对象复制时需忽略的字段列表
     * <p>密码、扩展信息、角色均属敏感数据，不应透出至前端 VO</p>
     */
    private static String[] USER_IGNORE = new String[]{"password", "extend", "roles"};

    /**
     * 将用户 PO 转换为 UserVO
     * <p>忽略密码等敏感字段，防止敏感信息泄露</p>
     *
     * @param po 用户持久化对象
     * @return 用户视图对象，入参为 null 时返回 null
     */
    public static UserVO copy(User po) {
        if (po == null) {
            return null;
        }
        UserVO ret = new UserVO();
        BeanUtils.copyProperties(po, ret, USER_IGNORE);
        return ret;
    }

    /**
     * 将用户 PO 转换为登录凭证 AccountProfile
     * <p>仅携带登录态所需的最小字段集合，用于会话/Token 场景</p>
     *
     * @param po 用户持久化对象
     * @return 登录凭证对象
     */
    public static AccountProfile copyPassport(User po) {
        AccountProfile passport = new AccountProfile(po.getId(), po.getUsername());
        passport.setName(po.getName());
        passport.setEmail(po.getEmail());
        passport.setAvatar(po.getAvatar());
        passport.setLastLogin(po.getLastLogin());
        passport.setStatus(po.getStatus());
        return passport;
    }

    /**
     * 将评论 PO 转换为 CommentVO
     *
     * @param po 评论持久化对象
     * @return 评论视图对象
     */
    public static CommentVO copy(Comment po) {
        CommentVO ret = new CommentVO();
        BeanUtils.copyProperties(po, ret);
        return ret;
    }

    /**
     * 将文章 PO 转换为 PostVO
     *
     * @param po 文章持久化对象
     * @return 文章视图对象
     */
    public static PostVO copy(Post po) {
        PostVO d = new PostVO();
        BeanUtils.copyProperties(po, d);
        return d;
    }

    /**
     * 将文章-标签关联 PO 转换为 PostTagVO
     *
     * @param po 文章标签关联持久化对象
     * @return 文章标签视图对象
     */
    public static PostTagVO copy(PostTag po) {
        PostTagVO ret = new PostTagVO();
        BeanUtils.copyProperties(po, ret);
        return ret;
    }

    /**
     * 将标签 PO 转换为 TagVO
     *
     * @param po 标签持久化对象
     * @return 标签视图对象
     */
    public static TagVO copy(Tag po) {
        TagVO ret = new TagVO();
        BeanUtils.copyProperties(po, ret);
        return ret;
    }

    /**
     * 根据前端传入的排序标识解析为 JPA 排序字段数组
     *
     * @param order 排序方式标识（newest/hottest）
     * @return JPA 排序字段名数组
     */
    public static String[] postOrder(String order) {
        String[] orders;
        switch (order) {
            case Consts.order.HOTTEST:
                orders = new String[]{"comments", "views", "created"};
                break;
            default:
                orders = new String[]{"created"};
                break;
        }
        return orders;
    }
}
