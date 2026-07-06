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
 * Hibernate Filter 切面
 * <p>
 * 切面职责：在标注了 {@link PostStatusFilter} 注解的 Service 方法执行前，
 * 启用 Hibernate 的 POST_STATUS_FILTER 过滤器，实现文章状态的自动过滤。
 * <p>
 * 切入点表达式含义：匹配所有标注了 {@code @PostStatusFilter} 注解的方法。
 * <p>
 * 通知类型：{@code @Before} 前置通知，在目标方法执行前启用 Hibernate Filter。
 *
 * @see PostStatusFilter
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
