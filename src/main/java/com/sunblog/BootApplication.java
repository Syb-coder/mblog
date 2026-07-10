/*
 * Sunblog —— 基于 Spring Boot 的开源 Java 博客系统
 *
 * 包名 com.sunblog 的含义：
 *   sun = 太阳，象征光明与活力
 *   blog = 博客
 *   sunblog = Sun + Blog 的组合
 *
 * ===== 项目架构分层（自上而下） =====
 *
 * web/controller     → Spring MVC 控制器（前端页面 + 管理后台 + REST API）
 * web/interceptor    → 全局拦截器（注入站点配置到所有模板）
 * web/filter         → Servlet 过滤器（请求耗时统计）
 * web/exceptions     → 全局异常处理器
 * web/menu           → 管理后台左侧菜单系统
 * web/formatter      → 参数格式化（XSS 防范、JSON 工具）
 *
 * modules/aspect     → AOP 切面（实现文章状态过滤）
 * modules/entity     → JPA 实体类（与数据库表一一映射）
 * modules/repository → Spring Data JPA 仓库（数据访问层）
 * modules/service    → 业务服务层（接口 + 实现）
 * modules/data       → VO（View Object）视图对象，封装展示层数据
 * modules/template   → FreeMarker 模板引擎的自定义指令扩展
 *
 * config             → Spring Boot 配置类（Shiro/WebMVC/站点设置）
 * shiro              → Apache Shiro 安全框架（认证 + 授权）
 * base/lang          → 基础语言组件（常量、异常、统一响应）
 * base/storage       → 文件存储抽象层（可扩展本地/OSS/七牛等）
 * base/utils         → 通用工具类（MD5、Markdown、图片处理等）
 */

package com.sunblog;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;

/**
 * Sunblog 博客系统的启动入口
 *
 * @SpringBootApplication 是以下三个注解的合体：
 *   1. @Configuration         → 声明为配置类，可注册 @Bean
 *   2. @EnableAutoConfiguration → 自动装配 Spring Boot 配置
 *   3. @ComponentScan         → 扫描当前包及其子包中的所有组件
 *
 * @EnableCaching 启用 Spring 声明式缓存（配合 ehcache.xml 使用）
 */
@Slf4j
@SpringBootApplication
@EnableCaching
public class BootApplication {

    public static void main(String[] args) {
        // 第一步：启动 Spring Boot 应用，返回 IOC 容器（ApplicationContext）
        // SpringApplication.run() 会完成：扫描组件 → 创建 Bean → 注入依赖 → 启动内嵌 Tomcat
        ApplicationContext context = SpringApplication.run(BootApplication.class, args);

        // 第二步：从容器环境中获取 server.port 配置值（默认 8080）
        // getEnvironment() 获取环境配置对象，getProperty() 读取 application.properties/yml 中的属性
        String serverPort = context.getEnvironment().getProperty("server.port");

        // 第三步：打印项目启动完成的访问地址，方便开发者点击进入
        log.info("sunblog started at http://localhost:" + serverPort);
    }

}
