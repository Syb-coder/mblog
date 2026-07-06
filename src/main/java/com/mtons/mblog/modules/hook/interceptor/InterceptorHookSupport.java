package com.mtons.mblog.modules.hook.interceptor;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.PreDestroy;

/**
 * 拦截器钩子支持类
 * <p>
 * 职责：为 {@link InterceptorHook} 实现类提供生命周期支持，
 * 在 Bean 销毁时自动从 {@link InterceptorHookManager} 中注销自身，
 * 避免持有失效引用。
 */
public abstract class InterceptorHookSupport implements InterceptorHook {
    @Autowired
    protected InterceptorHookManager interceptorHookManager;

    /**
     * Bean 销毁时从管理器中移除当前钩子
     */
    @PreDestroy
    public void destroy() {
        interceptorHookManager.removeInterceptorHook(this);
    }
}
