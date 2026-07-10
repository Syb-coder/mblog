package com.sunblog.base.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring 容器工具 —— 在非 Spring Bean 中获取 Spring Bean
 *
 * <h3>为什么需要这个类？</h3>
 * 在 Java Spring 项目中，只有被 @Component/@Service/@Controller 标注的类
 * 才能使用 @Autowired 注入其他 Bean。但有些工具类、工厂类不是 Spring Bean，
 * 它们也需要访问 Service 层的方法。
 *
 * SpringUtils 实现了 ApplicationContextAware 接口，在 Spring 启动时拿到
 * ApplicationContext 并缓存为静态变量，供任何地方调用。
 *
 * <h3>典型使用场景</h3>
 * CommentComplementor 不是 Spring Bean（由 static of() 创建），
 * 但需要调用 UserService.findMapByIds()，使用方式：
 * SpringUtils.getBean(UserService.class).findMapByIds(userIds)
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
