// 包声明：用户事件计数服务接口所在包
package com.mtons.mblog.modules.service;

// 导入缓存常量类，定义缓存名称
import com.mtons.mblog.base.lang.Consts;
// 导入缓存驱逐注解，写操作时清除相关缓存
import org.springframework.cache.annotation.CacheEvict;

// 导入 Set 集合接口，用于批量操作用户 ID
import java.util.Set;

/**
 * 用户事件计数 Service
 *
 * <h3>为什么需要单独的 UserEventService？</h3>
 * User 实体中有 posts（文章数）和 comments（评论数）字段，
 * 这些是聚合统计字段。当用户发表/删除文章或评论时需要更新。
 * 把计数更新放在 UserEventService 而非 Post/Comment Service 中，
 * 是为了职责分离：PostService 只关心文章本身，计数更新委托给 UserEventService。
 *
 * <h3>identityPost / identityComment</h3>
 * 这些方法内部调用 UserRepository 的原子 update，直接操作数据库更新，
 * 然后通过 @CacheEvict 清除 User 和 Post 的缓存。
 *
 * <h3>为什么有批量 identityComment(Set Long, boolean)？</h3>
 * 删除文章时，该文章下所有的评论作者都需要扣减计数。
 * 如果逐个调用 identityComment，会产生 N+1 次数据库更新。
 * 批量版本一次完成。
 */
// UserEventService 接口：定义用户事件计数的业务操作契约
public interface UserEventService {
    /**
     * @param userId 用户 ID
     * @param plus   true 为累加，false 为减少
     */
    // identityPost：累加或减少用户的文章计数，@CacheEvict 清除用户和文章缓存
    @CacheEvict(value = {Consts.CACHE_USER, Consts.CACHE_POST}, allEntries = true)
    void identityPost(Long userId, boolean plus);

    /**
     * @param userId 用户 ID
     * @param plus   true 为累加，false 为减少
     */
    // identityComment：累加或减少单用户的评论计数，@CacheEvict 清除用户和文章缓存
    @CacheEvict(value = {Consts.CACHE_USER, Consts.CACHE_POST}, allEntries = true)
    void identityComment(Long userId, boolean plus);

    /**
     * 批量累加/减少多用户评论数 - 用于文章删除时级联清理评论计数场景
     * <p>批量场景下需一次性同步所有受影响作者的计数，避免逐条调用产生 N+1。</p>
     *
     * @param userIds 用户 ID 集合
     * @param plus   true 为累加，false 为减少
     */
    // identityComment（批量版本）：批量累加/减少多用户评论计数，避免 N+1 问题
    @CacheEvict(value = {Consts.CACHE_USER, Consts.CACHE_POST}, allEntries = true)
    void identityComment(Set<Long> userIds, boolean plus);
}