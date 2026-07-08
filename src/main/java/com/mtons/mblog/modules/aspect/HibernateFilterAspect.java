package com.mtons.mblog.modules.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Hibernate 过滤器 AOP 切面
 *
 * <h3>为什么需要这个切面？</h3>
 * 在 Post 实体上定义了 @Filter(name = "POST_STATUS_FILTER", defaultCondition = "status = 0")，
 * 意思是在查询 Post 表时可以自动加上 WHERE status = 0 条件，只返回正常状态的文章。
 * 但这个过滤器不会自动生效，需要手动开启 Session 的 Filter。
 *
 * 这个切面的作用就是：在标注了 @PostStatusFilter 注解的 Service 方法执行前，
 * 自动从 JPA EntityManager 中获取 Hibernate Session，然后启用这个 Filter。
 *
 * <h3>工作流程</h3>
 * <pre>
 * Service 方法（如 PostServiceImpl.paging()）
 *   ↑ @PostStatusFilter 注解
 *   ↑ HibernateFilterAspect 前置拦截
 *   ↑ 开启 Hibernate Session 的 POST_STATUS_FILTER
 *   ↑ JPA 查询自动追加 WHERE status = 0
 *       效果：前端页面永远看不到 status=1（已删除）的文章
 * </pre>
 *
 * <h3>为什么不用 @Where 注解？</h3>
 * @Where 是 Hibernate 级别的硬编码过滤，动态关闭比较麻烦。
 * 而 @Filter 可以在需要时才启用，管理后台查看所有文章时就不启用这个 Filter。
 * 这样前台和后台可以共用同一个 Service 方法（如 paging()），
 * 前台加了 @PostStatusFilter → 只看正常文章
 * 后台没加 @PostStatusFilter → 能看到所有状态的文章
 */
@Aspect
@Component
public class HibernateFilterAspect {

    /** Hibernate Filter 名称，对应实体配置中的过滤器名称 */
    private static final String FILTER_NAME = "POST_STATUS_FILTER";

    /**
     * JPA EntityManager，由 Spring 容器注入
     */
    @PersistenceContext
    protected EntityManager em;

    /**
     * 切入点：匹配所有标注了 {@code @PostStatusFilter} 注解的方法
     */
    @Pointcut("@annotation(com.mtons.mblog.modules.aspect.PostStatusFilter)")
    public void filter() {
    }

    /**
     * 前置通知：在目标方法执行前启用 Hibernate Filter
     * <p>
     * 通过 EntityManager 解包得到 Hibernate Session，若 Session 已打开，
     * 则启用 POST_STATUS_FILTER 过滤器并校验参数。
     *
     * @param joinPoint 连接点，封装了被拦截方法的信息
     */
    @Before("filter()")
    public void doBefore(JoinPoint joinPoint) {
        Session mfSession = em.unwrap(Session.class);
        if (mfSession.isOpen()) {
            mfSession.enableFilter(FILTER_NAME).validate();
        }
    }
}
