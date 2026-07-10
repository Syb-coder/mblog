package com.sunblog.config;

import com.sunblog.shiro.AccountRealm;
import com.sunblog.shiro.AccountSubjectFactory;
import com.sunblog.shiro.AuthenticatedFilter;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.mgt.SubjectFactory;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import jakarta.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Apache Shiro 安全框架配置类
 *
 * <h3>Shiro 是什么？</h3>
 * Apache Shiro 是一个 Java 安全框架，提供认证（你是谁？）、授权（你能做什么？）、
 * 会话管理、加密等功能。相比 Spring Security，Shiro 配置更简洁。
 *
 * <h3>本类的作用</h3>
 * 装配 Shiro 的三个核心组件：
 * <ol>
 *   <li><b>Realm</b>（AccountRealm）：告诉 Shiro 如何从数据库查用户信息来做登录验证和权限判断</li>
 *   <li><b>SubjectFactory</b>：定制 Subject（当前用户）的创建过程</li>
 *   <li><b>过滤器链（shiroFilterFactoryBean）</b>：定义哪些 URL 需要登录、哪些需要特定权限</li>
 * </ol>
 *
 * <h3>过滤器链规则（重点）</h3>
 * 在 shiroFilterFactoryBean() 方法中定义了详细的 URL 拦截规则，大致的策略是：
 * <ul>
 *   <li>静态资源（/dist/**, /theme/**）→ anon（允许所有访问）</li>
 *   <li>登录页（/login）→ anon（允许未登录访问）</li>
 *   <li>用户设置（/settings/**）、发文章（/post/editing）→ authc（必须登录）</li>
 *   <li>后台管理 → authc + perms（必须登录且拥有特定权限）</li>
 * </ul>
 *
 * <h3>权限配置"字典"</h3>
 * <pre>
 * URL                     → 需要的权限
 * /admin/channel/list     → channel:list    查看频道列表
 * /admin/channel/update   → channel:update  修改频道
 * /admin/post/list        → post:list       查看文章列表
 * /admin/post/delete      → post:delete     删除文章
 * /admin/user/pwd         → user:pwd        重置用户密码
 * /admin/options/update   → options:update  修改站点配置
 * </pre>
 *
 * 这些权限值（如 "channel:list"）对应 Permission 表的 name 字段，
 * 在 AccountRealm.doGetAuthorizationInfo() 中加载并交给 Shiro 做匹配。
 */
@Configuration
@ConditionalOnProperty(name = "shiro.web.enabled", matchIfMissing = true)
public class ShiroConfiguration {
    /**
     * 自定义 Subject 工厂 Bean
     * <p>
     * 用于在 Subject 创建阶段注入定制逻辑（如保留请求体、覆盖会话创建策略）。
     * </p>
     *
     * @return AccountSubjectFactory 实例
     */
    @Bean
    public SubjectFactory subjectFactory() {
        return new AccountSubjectFactory();
    }

    /**
     * 自定义 Realm Bean
     * <p>
     * 承载用户认证（登录校验）与授权（权限解析）的核心逻辑。
     * </p>
     *
     * @return AccountRealm 实例
     */
    @Bean
    public Realm accountRealm() {
        return new AccountRealm();
    }

    /**
     * <p>
     * </p>
     *
     * @return net.sf.ehcache.CacheManager 实例
     */
    @Bean
    public net.sf.ehcache.CacheManager ehCacheManager() {
        net.sf.ehcache.CacheManager cacheManager = net.sf.ehcache.CacheManager.newInstance();
        try {
            cacheManager = net.sf.ehcache.CacheManager.create(
                new ClassPathResource("ehcache.xml").getInputStream());
        } catch (Exception e) {
            // ehcache.xml 缺失或格式异常时回退到默认配置，避免启动中断
            cacheManager = net.sf.ehcache.CacheManager.create();
        }
        return cacheManager;
    }

    /**
     * <p>
     * </p>
     *
     * @return Shiro EhCacheManager 实例
     */
    @Bean
    public CacheManager shiroCacheManager(net.sf.ehcache.CacheManager cacheManager) {
        EhCacheManager ehCacheManager = new EhCacheManager();
        ehCacheManager.setCacheManager(cacheManager);
        return ehCacheManager;
    }

    /**
     * Shiro 过滤器链 Bean
     * <p>
     * 通过自定义 AuthenticatedFilter 替换默认 authc 过滤器，支持 AJAX 请求的友好响应。
     * </p>
     *
     * @param securityManager Shiro SecurityManager，由自动装配提供
     * @return ShiroFilterFactoryBean 实例
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        // 未登录访问受保护资源时跳转到登录页
        shiroFilter.setLoginUrl("/login");
        // 登录成功后的默认跳转地址
        shiroFilter.setSuccessUrl("/");
        // 权限不足时的拒绝访问页面
        shiroFilter.setUnauthorizedUrl("/error/reject.html");

        HashMap<String, Filter> filters = new HashMap<>();
        // 使用自定义 AuthenticatedFilter，以支持 AJAX 与页面请求的差异化响应
        filters.put("authc", new AuthenticatedFilter());
        shiroFilter.setFilters(filters);

        /**
         * 配置 shiro 拦截器链
         *
         * anon  不需要认证
         * authc 需要认证
         * user  验证通过或 RememberMe 登录的都可以
         *
         * 顺序从上到下,优先级依次降低
         *
         */
        Map<String, String> hashMap = new LinkedHashMap<>();
        // 静态资源与登录入口允许匿名访问，避免资源加载被拦截
        hashMap.put("/dist/**", "anon");
        hashMap.put("/theme/**", "anon");
        hashMap.put("/storage/**", "anon");
        hashMap.put("/login", "anon");
        // 用户中心、设置与文章操作需要登录
        hashMap.put("/user/**", "authc");
        hashMap.put("/settings/**", "authc");
        hashMap.put("/post/editing", "authc");
        hashMap.put("/post/submit", "authc");
        hashMap.put("/post/delete/*", "authc");
        hashMap.put("/post/upload", "authc");

        // 后台频道管理：认证 + 细粒度权限校验
        hashMap.put("/admin/channel/list", "authc,perms[channel:list]");
        hashMap.put("/admin/channel/update", "authc,perms[channel:update]");
        hashMap.put("/admin/channel/delete", "authc,perms[channel:delete]");

        // 后台文章管理：认证 + 文章操作权限
        hashMap.put("/admin/post/list", "authc,perms[post:list]");
        hashMap.put("/admin/post/update", "authc,perms[post:update]");
        hashMap.put("/admin/post/delete", "authc,perms[post:delete]");

        // 后台评论管理：认证 + 评论操作权限
        hashMap.put("/admin/comment/list", "authc,perms[comment:list]");
        hashMap.put("/admin/comment/delete", "authc,perms[comment:delete]");

        // 后台用户管理：认证 + 用户相关权限
        hashMap.put("/admin/user/list", "authc,perms[user:list]");
        hashMap.put("/admin/user/update_role", "authc,perms[user:role]");
        hashMap.put("/admin/user/pwd", "authc,perms[user:pwd]");
        hashMap.put("/admin/user/open", "authc,perms[user:open]");
        hashMap.put("/admin/user/close", "authc,perms[user:close]");

        // 后台站点配置：认证 + 配置项读写权限
        hashMap.put("/admin/options/index", "authc,perms[options:index]");
        hashMap.put("/admin/options/update", "authc,perms[options:update]");

        hashMap.put("/admin", "authc,perms[admin]");
        hashMap.put("/admin/*", "authc,perms[admin]");

        shiroFilter.setFilterChainDefinitionMap(hashMap);
        return shiroFilter;
    }

}
