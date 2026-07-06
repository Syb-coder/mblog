package com.mtons.mblog.modules.service.impl;

import com.mtons.mblog.base.lang.Consts;
import com.mtons.mblog.modules.repository.UserRepository;
import com.mtons.mblog.modules.service.UserEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;

/**
 * 用户事件计数 Service 实现
 * <p>
 * 关键依赖：
 * <ul>
 *   <li>{@link UserRepository}：用户 JPA 仓储，提供原子计数 update 方法</li>
 * </ul>
 * </p>
 * <p>
 * 事务策略：类级别 {@code @Transactional(rollbackFor = Throwable.class)}，
 * 计数更新失败时回滚，避免计数与业务数据不一致。
 * </p>
 * <p>
 * </p>
 *
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class UserEventServiceImpl implements UserEventService {
    @Autowired
    private UserRepository userRepository;

    /**
     * 累加/减少用户文章数
     */
    @Override
    public void identityPost(Long userId, boolean plus) {
        userRepository.updatePosts(userId, (plus) ? Consts.IDENTITY_STEP : Consts.DECREASE_STEP);
    }

    /**
     * 累加/减少单用户评论数
     * <p>单用户场景封装为单元素 Set 调用批量方法，统一走批量 SQL 路径。</p>
     */
    @Override
    public void identityComment(Long userId, boolean plus) {
        userRepository.updateComments(Collections.singleton(userId), (plus) ? Consts.IDENTITY_STEP : Consts.DECREASE_STEP);
    }

    /**
     * 批量累加/减少多用户评论数
     * <p>批量场景下一次性 update 所有受影响用户，避免逐条调用产生 N 次 SQL。</p>
     */
    @Override
    public void identityComment(Set<Long> userIds, boolean plus) {
        userRepository.updateComments(userIds, (plus) ? Consts.IDENTITY_STEP : Consts.DECREASE_STEP);
    }

}
