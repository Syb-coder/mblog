package com.mtons.mblog.config;

import com.mtons.mblog.base.lang.Consts;
import com.mtons.mblog.modules.entity.Options;
import com.mtons.mblog.modules.service.ChannelService;
import com.mtons.mblog.modules.service.OptionsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.ServletContextAware;

import jakarta.servlet.ServletContext;
import java.util.List;
import java.util.Map;

/**
 * 应用启动上下文初始化器 —— 实现 ApplicationRunner 接口
 *
 * <h3>职责</h3>
 * 在 Spring Boot 应用启动完成后自动执行，完成两件核心初始化工作：
 * <ol>
 *   <li>从数据库加载站点配置（mto_options 表），放入 SiteOptions.options Map</li>
 *   <li>将频道列表（Channel 表）加载到 ServletContext，供前台导航栏使用</li>
 * </ol>
 *
 * <h3>为什么需要这个类？</h3>
 * 博客系统允许管理员在后台动态修改配置（如站点名称、注册开关等），
 * 这些配置存在数据库的 mto_options 表中。每次启动时，需要把这些配置
 * 从数据库读到内存里，然后注入到 FreeMarker 模板中，让前台页面能展示
 * 配置后的效果。
 *
 * <h3>执行顺序</h3>
 * 1. ApplicationRunner.run() —— Spring Boot 启动后回调
 * 2. reloadOptions(true) —— 读取数据库配置到 SiteOptions
 * 3. resetChannels() —— 加载频道列表到 ServletContext
 *
 * <h3>特别说明</h3>
 * 如果数据库为空（首次启动），会尝试执行 classpath:scripts/schema.sql
 * 来初始化默认数据。如果初始化失败，说明数据库配置有问题，
 * 系统会直接退出 JVM（System.exit(1)），因为后续操作都会因为没有
 * 数据而报空指针异常。
 */
@Slf4j
@Order(2)
@Component
public class ContextStartup implements ApplicationRunner, ServletContextAware {
    @Autowired
    private OptionsService optionsService;
    @Autowired
    private SiteOptions siteOptions;
    @Autowired
    private ChannelService channelService;

    /** 由 ServletContextAware 回调注入，无需 @Autowired */
    private ServletContext servletContext;

    /**
     * 应用启动入口，按顺序加载配置项与频道数据。
     *
     * @param applicationArguments Spring Boot 启动参数
     * @throws Exception 初始化过程中可能抛出的异常
     */
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        log.info("initialization ...");
        // 启动阶段强制重新加载配置，必要时触发数据库初始化
        reloadOptions(true);
        // 加载频道列表到 ServletContext，供导航栏渲染
        resetChannels();
        log.info("OK, completed");
    }

    /**
     * 由 Spring 容器注入 ServletContext，便于后续向全局作用域写入配置数据。
     *
     * @param servletContext Servlet 全局上下文
     */
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * <p>
     * 当 startup 为 true 且数据库无配置记录时，会尝试从 classpath:scripts/schema.sql
     * 执行初始化；若初始化失败则视为数据库未就绪，直接退出 JVM 以避免后续空指针。
     * </p>
     *
     * @param startup 是否为启动阶段首次加载，true 时触发默认配置初始化
     */
    public void reloadOptions(boolean startup) {
        List<Options> options = optionsService.findAll();

        log.info("find options ({})...", options.size());

        if (startup && CollectionUtils.isEmpty(options)) {
            try {
                log.info("init options...");
                Resource resource = new ClassPathResource("/scripts/schema.sql");
                optionsService.initSettings(resource);
                options = optionsService.findAll();
            } catch (Exception e) {
                // 数据库未初始化属于致命错误，直接终止启动以避免后续空指针异常
                log.error("------------------------------------------------------------");
                log.error("-          ERROR: The database is not initialized          -");
                log.error("------------------------------------------------------------");
                log.error(e.getMessage(), e);
                System.exit(1);
            }
        }

        Map<String, String> map = siteOptions.getOptions();
        options.forEach(opt -> {
            if (StringUtils.isNoneBlank(opt.getKey(), opt.getValue())) {
                map.put(opt.getKey(), opt.getValue());
            }
        });
        servletContext.setAttribute("options", map);
        servletContext.setAttribute("site", siteOptions);

        System.setProperty("site.location", siteOptions.getLocation());
    }

    /**
     * 重新加载正常状态的频道列表到 ServletContext。
     */
    public void resetChannels() {
        // 仅加载状态正常的频道，避免已下线频道出现在前台导航
        servletContext.setAttribute("channels", channelService.findAll(Consts.STATUS_NORMAL));
    }

}
