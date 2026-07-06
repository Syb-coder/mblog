package com.mtons.mblog.modules.hook.interceptor;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 拦截器钩子接口
 * <p>
 * 设计意图：为 Spring MVC 拦截器流程提供扩展点，业务模块可实现该接口，
 * 在不修改全局拦截器的前提下注入自定义的请求处理逻辑。
 * <p>
 * 扩展点用途：通过 {@link #getInterceptor()} 声明所拦截的 Controller 类或方法，
 * 由 {@link InterceptorHookManager} 统一管理并调度。
 */
public interface InterceptorHook {

    /**
     * 获取拦截目标
     * <p>
     * 可同时拦截多个目标，格式为 Controller 类全限定名，例如：
     * {@code return new String[]{"mblog.web.controller.impl.group.GroupVidewController"};}
     * <p>
     * 也可以精确到 Controller 中的方法，格式为：
     * {@code return new String[]{"mblog.web.controller.impl.group.GroupVidewController#view"};}
     *
     * @return 拦截目标数组，元素为类全限定名或类全限定名#方法名
     */
    String[] getInterceptor();

    /**
     * 控制器方法执行前回调
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param handler  处理器方法
     * @throws Exception 异常
     */
    void preHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) throws Exception;

    /**
     * 控制器方法执行后回调
     *
     * @param request       HTTP 请求
     * @param response      HTTP 响应
     * @param handler       处理器方法
     * @param modelAndView  视图模型
     * @throws Exception 异常
     */
    void postHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler, ModelAndView modelAndView) throws Exception;

    /**
     * 请求完成后回调
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param handler  处理器方法
     * @param ex       异常
     * @throws Exception 异常
     */
    void afterCompletion(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler, Exception ex) throws Exception;

    /**
     * 异步处理开始时回调
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param handler  处理器方法
     * @throws Exception 异常
     */
    void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) throws Exception;
}
