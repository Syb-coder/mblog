package com.sunblog.web.exceptions;

import com.alibaba.fastjson2.JSON;
import com.sunblog.base.lang.Result;
import com.sunblog.base.lang.MtonsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局默认异常处理器
 * <p>
 * 职责说明：
 * <ul>
 *   <li>实现 Spring MVC 的 {@link HandlerExceptionResolver} 接口，作为统一的异常处理出口；
 *   <li>根据请求类型（AJAX / 普通页面）输出不同的响应形态：
 *       <ul>
 *         <li>AJAX 请求：返回 JSON 格式的 {@link Result} 失败结构体；</li>
 *         <li>普通页面请求：跳转到统一的 error 视图并携带错误信息。</li>
 *       </ul>
 *   </li>
 * </ul>
 *
 * 异常处理策略：
 * <ul>
 *   <li>{@link MtonsException}、{@link IllegalArgumentException}、{@link IllegalStateException}：
 *       视为业务语义异常，仅记录错误消息（不带堆栈），避免日志噪声；</li>
 * </ul>
 *
 * @see HandlerExceptionResolver
 * @see MtonsException
 */
@Slf4j
@Component
public class DefaultExceptionHandler implements HandlerExceptionResolver {
	/**
	 * 错误视图名称，对应 templates/error.ftl 模板
	 */
	private String errorView = "error";

	/**
	 * 解析并处理 Controller 执行过程中抛出的异常
	 * <p>
	 * 处理流程：
	 * <ol>
	 *   <li>根据异常类型选择日志级别（业务异常仅输出消息，系统异常输出完整堆栈）；</li>
	 *   <li>判定当前请求是否为 AJAX 请求；</li>
	 *   <li>AJAX 请求直接写入 JSON 响应，普通请求构建错误 ModelAndView。</li>
	 * </ol>
	 *
	 * @param request  当前 HTTP 请求对象
	 * @param response 当前 HTTP 响应对象
	 * @param handler  发生异常的处理器（通常为 HandlerMethod）
	 * @param ex       实际抛出的异常
	 * @return 视图模型对象（AJAX 时返回空 ModelAndView，普通请求返回 error 视图）
	 */
	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {

		// 业务语义异常仅打印消息，系统级异常打印完整堆栈便于定位
		if (ex instanceof IllegalArgumentException || ex instanceof IllegalStateException || ex instanceof MtonsException) {
			log.error(ex.getMessage());
		} else {
			log.error(ex.getMessage(), ex);
		}

		ModelAndView view = null;
		String ret = ex.getMessage();

		if (isAjax(handler)) {
			// AJAX 请求：直接通过 response 输出 JSON 失败结构体，不渲染视图
			try {
				response.setContentType("application/json;charset=UTF-8");
				response.getWriter().print(JSON.toJSONString(Result.failure(ret)));
			} catch (IOException e) {
				// 输出流异常时忽略，避免在异常处理流程中再次抛出
			}

			view = new ModelAndView();
		} else {
			// 普通页面请求：携带错误信息与上下文路径，跳转到统一 error 视图
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("error", ret);
	        map.put("base", request.getContextPath());
			view = new ModelAndView(errorView, map);
		}
		return view;
	}

	/**
	 * 判断当前 handler 是否为 AJAX 请求处理方法
	 * <p>
	 * 判定依据：handler 是否为 {@link HandlerMethod}，且其对应方法上是否标注了
	 * {@link ResponseBody} 注解。若标注则视为 AJAX 请求，返回 true。
	 *
	 * @param handler 当前请求对应的处理器对象
	 * @return true 表示该请求需要返回 JSON 响应；false 表示为普通页面请求
	 */
	private boolean isAjax(Object handler) {
		if (handler != null && handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			ResponseBody responseBodyAnn = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), ResponseBody.class);
			return responseBodyAnn != null;
		}

		return false;
	}

}
