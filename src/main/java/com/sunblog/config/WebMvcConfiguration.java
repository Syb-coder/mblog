package com.sunblog.config;

import com.alibaba.fastjson2.support.spring6.http.converter.FastJsonHttpMessageConverter;
import com.sunblog.web.interceptor.BaseInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

/**
 * Spring MVC 扩展配置类
 * <p>
 * 实现 {@link WebMvcConfigurer}，自定义拦截器注册、静态资源映射及
 * HTTP 消息转换器装配，是前端请求处理链路的总入口配置。
 * </p>
 *
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Autowired
    private BaseInterceptor baseInterceptor;
    @Autowired
    private SiteOptions siteOptions;
    @Autowired
    private FastJsonHttpMessageConverter fastJsonHttpMessageConverter;

    /**
     * 注册全局拦截器
     * <p>拦截所有路径，但排除静态资源目录，避免静态资源请求也走拦截器造成性能损耗。</p>
     *
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(baseInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/dist/**", "/store/**", "/static/**");
    }

    /**
     * 配置静态资源映射
     * <p>
     * 将打包内置的静态资源路径与运行期上传目录映射到 URL：
     * <ul>
     *   <li>{@code /dist/}双星：classpath 内置前端构建产物</li>
     * </ul>
     * </p>
     *
     * @param registry 资源处理器注册器
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 拼接运行期文件根路径，使用 file:/// 协议访问磁盘文件
        String location = "file:///" + siteOptions.getLocation();
        registry.addResourceHandler("/dist/**")
                .addResourceLocations("classpath:/static/dist/");
        registry.addResourceHandler("/theme/*/dist/**")
                .addResourceLocations("classpath:/templates/")
                .addResourceLocations(location + "/storage/templates/");
        registry.addResourceHandler("/storage/avatars/**")
                .addResourceLocations(location + "/storage/avatars/");
        registry.addResourceHandler("/storage/thumbnails/**")
                .addResourceLocations(location + "/storage/thumbnails/");
    }

    /**
     * 注册自定义 HTTP 消息转换器
     * <p>将 FastJson2 转换器加入转换器链，用于 JSON 序列化与反序列化。</p>
     *
     * @param converters 已注册的转换器列表
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(fastJsonHttpMessageConverter);
    }

}
