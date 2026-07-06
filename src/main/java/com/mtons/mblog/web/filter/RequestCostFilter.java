package com.mtons.mblog.web.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 请求耗时统计过滤器
 * <p>
 * 职责说明：
 * <ul>
 *   <li>在请求进入前启动 Spring 提供的 {@link StopWatch} 计时器，在请求执行完成后停止计时；</li>
 * </ul>
 *
 * 工作流程：
 * <ol>
 *   <li>将 ServletRequest 强转为 HttpServletRequest，以便获取请求 URI；</li>
 *   <li>启动 StopWatch，调用 {@link FilterChain#doFilter} 放行请求；</li>
 *   <li>请求执行完毕后停止 StopWatch，输出 DEBUG 日志。</li>
 * </ol>
 *
 * @see Filter
 * @see StopWatch
 */
@Slf4j
public class RequestCostFilter implements Filter {

    /**
     * 过滤器初始化方法
     * <p>
     * 当前过滤器无需额外初始化逻辑，保留空实现以满足 {@link Filter} 接口约定。
     *
     * @param filterConfig 过滤器配置对象
     * @throws ServletException 初始化过程中发生的 Servlet 异常
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /**
     * 执行过滤器逻辑：统计请求耗时
     * <p>
     * 在调用链前后分别启动 / 停止 StopWatch，从而计算本次请求的总耗时（毫秒），
     * 并通过 DEBUG 日志输出，便于在调试阶段定位性能瓶颈。
     *
     * @param request  ServletRequest 对象
     * @param response ServletResponse 对象
     * @param chain    FilterChain，用于将请求传递给下一个过滤器或目标资源
     * @throws IOException      IO 异常
     * @throws ServletException Servlet 异常
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		StopWatch stopWatch = new StopWatch();
		// 请求处理前启动计时
		stopWatch.start();
		chain.doFilter(request, response);
		// 请求处理后停止计时
		stopWatch.stop();

		// 输出请求 URI 与耗时（毫秒）到 DEBUG 日志
		log.debug("{} -> request code - {}",
                httpRequest.getRequestURI(),
                stopWatch.getTotalTimeMillis());
    }

    /**
     * 过滤器销毁方法
     * <p>
     * 当前过滤器无资源需要释放，保留空实现以满足 {@link Filter} 接口约定。
     */
    @Override
    public void destroy() {

    }
}
