package com.mtons.mblog.modules.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 文章状态过滤注解
 * <p>
 * 用途：标注在 Service 方法上，配合 {@link HibernateFilterAspect} 切面使用，
 * 在方法执行前自动启用 Hibernate 的 POST_STATUS_FILTER 过滤器，
 * 从而在查询时过滤掉不符合状态条件的数据（如未发布文章）。
 * <p>
 * 作用域：方法级别（{@link ElementType#METHOD}）
 * <p>
 * 保留策略：运行时保留（{@link RetentionPolicy#RUNTIME}），可被 AOP 切面反射读取
 *
 * @see HibernateFilterAspect
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostStatusFilter {

}
