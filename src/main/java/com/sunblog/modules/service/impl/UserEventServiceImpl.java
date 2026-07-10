// 包声明：用户事件计数服务实现类所在包
package com.sunblog.modules.service.impl;

// 导入常量定义类，包含缓存名称和计数步长常量
import com.sunblog.base.lang.Consts;
// 导入用户仓储接口，提供原子计数更新方法
import com.sunblog.modules.repository.UserRepository;
// 导入用户事件计数服务接口，本类实现该接口
import com.sunblog.modules.service.UserEventService;
// 导入 Spring 自动注入注解
import org.springframework.beans.factory.annotation.Autowired;
// 导入 Spring Service 注解，标识该类为业务服务组件
import org.springframework.stereotype.Service;
// 导入 Spring 事务注解，控制数据库事务行为
import org.springframework.transaction.annotation.Transactional;

// 导入 Collections 工具类，用于创建单元素集合
import java.util.Collections;
// 导入 Set 集合接口
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
// @Service 注解：将该类注册为 Spring 业务服务组件
@Service
// @Transactional(rollbackFor = Throwable.class)：类级别可写事务，任意异常都回滚
@Transactional(rollbackFor = Throwable.class)
// UserEventServiceImpl：用户事件计数服务实现类
public class UserEventServiceImpl implements UserEventService {
    // @Autowired：Spring 自动注入用户仓储实例
    @Autowired
    // userRepository：用户 JPA 仓储，提供原子计数更新方法
    private UserRepository userRepository;

    /**
     * 累加/减少用户文章数
     */
    // @Override：实现接口方法
    @Override
    // identityPost：累加或减少用户的文章计数
    public void identityPost(Long userId, boolean plus) {
        // 调用仓储的原子更新方法，plus 为 true 时步长 +1，false 时步长 -1
        userRepository.updatePosts(userId, (plus) ? Consts.IDENTITY_STEP : Consts.DECREASE_STEP);
    }

    /**
     * 累加/减少单用户评论数
     * <p>单用户场景封装为单元素 Set 调用批量方法，统一走批量 SQL 路径。</p>
     */
    // @Override：实现接口方法
    @Override
    // identityComment：累加或减少单用户的评论计数
    public void identityComment(Long userId, boolean plus) {
        // 将单个用户 ID 封装为单元素 Set，统一走批量更新 SQL 路径
        userRepository.updateComments(Collections.singleton(userId), (plus) ? Consts.IDENTITY_STEP : Consts.DECREASE_STEP);
    }

    /**
     * 批量累加/减少多用户评论数
     * <p>批量场景下一次性 update 所有受影响用户，避免逐条调用产生 N 次 SQL。</p>
     */
    // @Override：实现接口方法
    @Override
    // identityComment（批量版本）：批量累加/减少多用户评论计数
    public void identityComment(Set<Long> userIds, boolean plus) {
        // 调用仓储的批量更新方法，一次性更新所有受影响用户的评论计数
        userRepository.updateComments(userIds, (plus) ? Consts.IDENTITY_STEP : Consts.DECREASE_STEP);
    }

}