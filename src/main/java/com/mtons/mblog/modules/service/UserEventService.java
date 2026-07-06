package com.mtons.mblog.modules.service;

import com.mtons.mblog.base.lang.Consts;
import org.springframework.cache.annotation.CacheEvict;

import java.util.Set;

/**
 * 用户事件计数 Service
 * <p>
 * 负责维护用户的文章数、评论数等聚合计数。
 * </p>
 *
 */
public interface UserEventService {
    /* *
     * @param userId 用户 ID
     * @param plus   true 为累加，false 为减少
     */
    @CacheEvict(value = {Consts.CACHE_USER, Consts.CACHE_POST}, allEntries = true)
    void identityPost(Long userId, boolean plus);

    /* *
     * @param userId 用户 ID
     * @param plus   true 为累加，false 为减少
     */
    @CacheEvict(value = {Consts.CACHE_USER, Consts.CACHE_POST}, allEntries = true)
    void identityComment(Long userId, boolean plus);

    /**
     * 批量累加/减少多用户评论数 - 用于文章删除时级联清理评论计数场景
     * <p>批量场景下需一次性同步所有受影响作者的计数，避免逐条调用产生 N+1。</p>
     *
     * @param userIds 用户 ID 集合
     * @param plus   true 为累加，false 为减少
     */
    @CacheEvict(value = {Consts.CACHE_USER, Consts.CACHE_POST}, allEntries = true)
    void identityComment(Set<Long> userIds, boolean plus);
}
