package com.mtons.mblog.modules.hook.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 拦截器钩子管理器
 * <p>
 * 职责：在容器启动时收集所有 {@link InterceptorHook} 实现，并按拦截目标
 * （Controller 类或方法）建立映射关系，在请求处理时统一调度匹配的钩子。
 */
@Component
public class InterceptorHookManager {
	@Autowired
	private ApplicationContext applicationContext;

    /** 钩子映射表：key 为类全限定名或类全限定名#方法名，value 为对应的钩子集合 */
    private Map<String, Set<InterceptorHook>> map = new HashMap<>();

    /**
     * 初始化：扫描容器中所有 {@link InterceptorHook} Bean 并注册到映射表
     */
    @PostConstruct
    private void init() {
        Map<String, InterceptorHook> map = applicationContext.getBeansOfType(InterceptorHook.class);
        Iterator<Map.Entry<String, InterceptorHook>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, InterceptorHook> entry = it.next();
            InterceptorHook interceptorHook = entry.getValue();
            String[] names = interceptorHook.getInterceptor();
            addInterceptorHook(names, interceptorHook);
        }
    }

    /**
     * 添加钩子到映射表
     *
     * @param names          拦截目标数组
     * @param interceptorHook 钩子实例
     */
    private void addInterceptorHook(String[] names, InterceptorHook interceptorHook) {
        if (names != null) {
            for (String name : names) {
                if (!map.containsKey(name)) {
                    Set<InterceptorHook> list = new HashSet<>();
                    list.add(interceptorHook);
                    map.put(name, list);
                } else {
                    map.get(name).add(interceptorHook);
                }
            }
        }
    }

    /**
     * 删除一个拦截器钩子
     *
     * @param hook 钩子实例
     */
    public void removeInterceptorHook(InterceptorHook hook) {
        Iterator<Map.Entry<String, Set<InterceptorHook>>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Set<InterceptorHook>> entry = it.next();
            entry.getValue().remove(hook);
        }
    }

    /**
     * 根据 HandlerMethod 获取匹配的钩子集合
     *
     * @param handlerMethod 处理器方法
     * @return 匹配的钩子集合
     */
    public Set<InterceptorHook> getInterceptorHook(HandlerMethod handlerMethod) {
        Set<InterceptorHook> interceptorHooks = new HashSet<>();
        String clazz = handlerMethod.getBean().getClass().getName();
        String method = handlerMethod.getMethod().getName();
        Set<InterceptorHook> c = map.get(clazz);
        Set<InterceptorHook> m = map.get(clazz + "#" + method);
        if (m != null) {
            interceptorHooks.addAll(m);
        }
        if (c != null) {
            interceptorHooks.addAll(c);
        }
        return interceptorHooks;
    }

    /**
     * 前置处理回调
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param handler  处理器
     * @throws Exception 异常
     */
    public void preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Set<InterceptorHook> interceptorHookSet = getInterceptorHook(handlerMethod);
            for (InterceptorHook interceptorHook : interceptorHookSet) {
                interceptorHook.preHandle(request, response, handlerMethod);
            }
        }
    }

    /**
     * 后置处理回调
     *
     * @param request       HTTP 请求
     * @param response      HTTP 响应
     * @param handler       处理器
     * @param modelAndView  视图模型
     * @throws Exception 异常
     */
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Set<InterceptorHook> interceptorHookSet = getInterceptorHook(handlerMethod);
            for (InterceptorHook interceptorHook : interceptorHookSet) {
                interceptorHook.postHandle(request, response, handlerMethod, modelAndView);
            }
        }
    }

    /**
     * 完成后回调
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param handler  处理器
     * @param ex       异常
     * @throws Exception 异常
     */
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Set<InterceptorHook> interceptorHookSet = getInterceptorHook(handlerMethod);
            for (InterceptorHook interceptorHook : interceptorHookSet) {
                interceptorHook.afterCompletion(request, response, handlerMethod, ex);
            }
        }
    }

    /**
     * 异步处理开始时回调
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param handler  处理器
     * @throws Exception 异常
     */
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Set<InterceptorHook> interceptorHookSet = getInterceptorHook(handlerMethod);
            for (InterceptorHook interceptorHook : interceptorHookSet) {
                interceptorHook.afterConcurrentHandlingStarted(request, response, handlerMethod);
            }
        }
    }

}
