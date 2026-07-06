package com.mtons.mblog.base.utils;

import com.mtons.mblog.base.lang.Consts;
import com.mtons.mblog.modules.data.*;
import com.mtons.mblog.modules.entity.*;
import org.springframework.beans.BeanUtils;

/**
 * 实体与视图对象转换工具
 * <p>
 * {@link BeanUtils#copyProperties} 实现浅拷贝，并提供针对用户实体的字段忽略策略，
 * 避免敏感字段（如密码、角色）泄露到前端。
 * </p>
 *
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
