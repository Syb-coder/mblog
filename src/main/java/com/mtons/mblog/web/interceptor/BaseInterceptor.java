package com.mtons.mblog.web.interceptor;

import com.mtons.mblog.config.SiteOptions;
import com.mtons.mblog.modules.hook.interceptor.InterceptorHookManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Web 层拦截器基类
 * <p>
 * 职责说明：
 * <ul>
 *   <li>实现 Spring MVC 的 {@link HandlerInterceptor} 接口，作为全站通用的拦截器入口；</li>
 *   <li>委托 {@link InterceptorHookManager} 执行具体的钩子逻辑（如用户态注入、权限校验等扩展点）；</li>
 *       <ul>
 *         <li>{@code site}：站点配置 {@link SiteOptions}，供视图层直接访问站点配置项。</li>
 *       </ul>
 *   </li>
 * </ul>
 *
 * 通用能力：
 * <ul>
 *   <li>拦截器钩子扩展点（preHandle / postHandle / afterCompletion 全阶段委托）；</li>
 *   <li>为后续子类或钩子提供统一的执行链路。</li>
 * </ul>
 *
 * @see HandlerInterceptor
 * @see InterceptorHookManager
 * @see SiteOptions
 */
@Component
public class BaseInterceptor implements HandlerInterceptor {

	@Autowired
	private InterceptorHookManager interceptorHookManager;
	@Autowired
	private SiteOptions siteOptions;

	/**
	 * 请求前置处理
	 * <p>
	 * 委托 {@link InterceptorHookManager#preHandle} 执行前置钩子逻辑，
	 * 始终返回 true 以放行请求进入后续拦截器与目标 Controller。
	 *
	 * @param request  当前 HTTP 请求
	 * @param response 当前 HTTP 响应
	 * @param handler  目标处理器
	 * @return 始终返回 true，放行请求
	 * @throws Exception 钩子执行过程中抛出的异常
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		interceptorHookManager.preHandle(request, response, handler);
		return true;
	}

	/**
	 * 请求后置处理（Controller 执行完毕，视图渲染前）
	 * <p>
	 * 主要完成两件事：
	 * <ol>
	 *   <li>若 ModelAndView 不为空，则注入 {@code site} 站点配置，供视图层访问；</li>
	 *   <li>委托 {@link InterceptorHookManager#postHandle} 执行后置钩子逻辑。</li>
	 * </ol>
	 *
	 * @param request      当前 HTTP 请求
	 * @param response     当前 HTTP 响应
	 * @param handler      目标处理器
	 * @param modelAndView 视图模型对象（可能为 null）
	 * @throws Exception 处理过程中抛出的异常
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		request.setAttribute("base", request.getContextPath());
		if (modelAndView != null) {
			// 注入站点配置，供视图层直接访问
			modelAndView.addObject("site", siteOptions);
		}
		interceptorHookManager.postHandle(request,response,handler,modelAndView);
	}

	/**
	 * 请求完成后的回调（视图渲染完成后）
	 * <p>
	 * 委托 {@link InterceptorHookManager#afterCompletion} 执行完成阶段的钩子逻辑，
	 * 用于资源清理、日志记录等收尾工作。
	 *
	 * @param request  当前 HTTP 请求
	 * @param response 当前 HTTP 响应
	 * @param handler  目标处理器
	 * @param ex       Controller 执行过程中抛出的异常（无异常时为 null）
	 * @throws Exception 处理过程中抛出的异常
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
		interceptorHookManager.afterCompletion(request, response, handler, ex);
	}

}
