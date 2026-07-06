package com.mtons.mblog.web.interceptor;

import com.mtons.mblog.config.SiteOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Web 层拦截器基类
 * <p>
 * 向视图层注入站点配置对象 {@code site}，使模板可直接访问站点配置项。
 * </p>
 */
@Component
public class BaseInterceptor implements HandlerInterceptor {

	@Autowired
	private SiteOptions siteOptions;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) {
		request.setAttribute("base", request.getContextPath());
		if (modelAndView != null) {
			modelAndView.addObject("site", siteOptions);
		}
	}
}
