package com.sunblog.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.support.config.FastJsonConfig;
import com.alibaba.fastjson2.support.spring6.http.converter.FastJsonHttpMessageConverter;
import com.google.common.collect.Maps;
import com.sunblog.modules.template.TemplateDirective;
import com.sunblog.modules.template.method.TimeAgoMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 站点级综合配置类
 * <p>
 * 集中装配与站点运行相关的组件，包括：
 * <ul>
 *   <li>异步任务线程池（用于日志、消息等异步处理）</li>
 *   <li>FastJson2 HTTP 消息转换器</li>
 * </ul>
 * </p>
 * <p>
 * 关键依赖说明：
 * <ul>
 *   <li>{@link ApplicationContext}：用于扫描所有 {@link TemplateDirective} 实现</li>
 * </ul>
 * </p>
 *
 * @since 3.0
 */
@Slf4j
@Configuration
@EnableAsync
public class SiteConfiguration {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private freemarker.template.Configuration configuration;
    @Autowired
    private SiteOptions siteOptions;

    /**
     * <p>
     * </p>
     */
    @PostConstruct
    public void setSharedVariable() {
        Map<String, Object> vars = Maps.newHashMap();
        Map<String, TemplateDirective> map = applicationContext.getBeansOfType(TemplateDirective.class);

        map.forEach((k, v) -> {
            String name = v.getName();
            if (name.indexOf(".") > 0) {
                String[] names = name.split("\\.");
                Map<String, Object> child = (Map<String, Object>) vars.computeIfAbsent(names[0], key -> new HashMap<>());
                child.put(names[1], v);
            } else {
                vars.put(v.getName(), v);
            }
        });

        try {
            configuration.setSharedVariables(vars);
//          map.forEach((k, v) -> configuration.setSharedVariable(v.getName(), v));
            configuration.setSharedVariable("timeAgo", new TimeAgoMethod());
            configuration.setSharedVariable("site", siteOptions);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 异步任务线程池 Bean
     * <p>
     * 配置项说明：
     * <ul>
     *   <li>核心线程数：2</li>
     *   <li>最大线程数：8</li>
     *   <li>队列容量：100</li>
     *   <li>拒绝策略：CallerRunsPolicy（由调用线程直接执行，避免任务丢失）</li>
     * </ul>
     * 用于日志埋点等异步处理，关闭时等待任务完成避免丢失。
     * </p>
     *
     * @return ThreadPoolTaskExecutor 实例
     */
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("sunblog.logThread-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 关闭时等待已提交任务完成，保证日志等数据不丢失
        executor.setWaitForTasksToCompleteOnShutdown(true);
        return executor;
    }

    /**
     * FastJson2 HTTP 消息转换器 Bean
     * <p>
     * </p>
     *
     * @return FastJsonHttpMessageConverter 实例
     */
    @Bean
    @ConditionalOnClass({JSON.class})
    public FastJsonHttpMessageConverter fastJsonHttpMessageConverter() {
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();

        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        // 统一空值序列化行为，避免前端处理 undefined 报错
        fastJsonConfig.setWriterFeatures(
                JSONWriter.Feature.WriteNulls,
                JSONWriter.Feature.WriteNullStringAsEmpty,
                JSONWriter.Feature.WriteNullListAsEmpty
        );
        fastConverter.setFastJsonConfig(fastJsonConfig);
        return fastConverter;
    }

//    @Bean
//    public HttpMessageConverters httpMessageConverters(){
//        FastJsonHttpMessageConverter jsonHttpMessageConverter = fastJsonHttpMessageConverter();
//        return new HttpMessageConverters(jsonHttpMessageConverter);
//    }
}
