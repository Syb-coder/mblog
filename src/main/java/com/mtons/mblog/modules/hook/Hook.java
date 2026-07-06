package com.mtons.mblog.modules.hook;

/**
 * 钩子接口
 * <p>
 * 设计意图：定义系统扩展点接口，允许业务模块在不修改核心流程的前提下，
 * 通过实现钩子接口注入自定义逻辑。当前为空接口占位，预留未来扩展能力。
 * <p>
 * 扩展点用途：可由具体子接口（如 {@link com.mtons.mblog.modules.hook.interceptor.InterceptorHook}）
 * 继承并定义具体的钩子方法。
 */
public interface Hook{

//    boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler);
//
//    void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
//            throws Exception;
//
//    void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
//            throws Exception;
//
//    void afterConcurrentHandlingStarted( HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception;

}
