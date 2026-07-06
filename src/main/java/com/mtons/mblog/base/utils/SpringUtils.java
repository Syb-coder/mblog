package com.mtons.mblog.base.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring 容器工具
 * <p>
 * 通过实现 {@link ApplicationContextAware} 持有 Spring 应用上下文，
 * 提供静态方法在非 Spring 管理的类中获取 Bean，便于工具类或动态实例访问容器。
 * </p>
 *
 * @version : 1.0
 * @date : 2020/3/26
 */
@Component
public class SpringUtils implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    /**
     * 注入应用上下文
     * <p>仅首次注入时赋值，避免被后续覆盖</p>
     *
     * @param applicationContext Spring 应用上下文
     */
    @SuppressWarnings("NullableProblems")
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        if (SpringUtils.applicationContext == null) {
            SpringUtils.applicationContext = applicationContext;
        }
    }

    /**
     * 获取applicationContext
     *
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    //通过name获取 Bean.

    /**
     * 通过name获取 Bean
     *
     * @param <T> Bean类型
     * @param name Bean名称
     * @return Bean
     */
    public static <T> T getBean(String name) {
        //noinspection unchecked
        return (T) applicationContext.getBean(name);
    }

    /**
     * 通过class获取Bean
     *
     * @param <T> Bean类型
     * @param clazz Bean类
     * @return Bean对象
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     *
     * @param <T> bean类型
     * @param name Bean名称
     * @param clazz bean类型
     * @return Bean对象
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }

    /**
     * 对非 Spring 管理的实例执行自动装配
     *
     * @param bean 待注入依赖的对象
     */
    public static void autowireBean(Object bean) {
        applicationContext.getAutowireCapableBeanFactory().autowireBean(bean);
    }
}
