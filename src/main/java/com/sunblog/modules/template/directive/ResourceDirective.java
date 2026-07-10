/**
 */
package com.sunblog.modules.template.directive;

import com.sunblog.modules.template.DirectiveHandler;
import com.sunblog.modules.template.TemplateDirective;
import org.springframework.stereotype.Component;

/**
 * 资源路径处理指令
 * <p>
 * FreeMarker 使用方式：
 * <pre>{@code
 * <@resource src="/storage/test.png" />
 * }</pre>
 * <p>
 * 支持参数：
 * <ul>
 *     <li>src：资源路径（默认 "#"）</li>
 * </ul>
 * 输出：当路径以 /storage 或 /theme 开头时，自动拼接上下文路径前缀；
 * 否则原样输出资源路径。
 */
@Component
public class ResourceDirective extends TemplateDirective {
    /**
     * 获取指令名称
     *
     * @return 指令名称 "resource"
     */
    @Override
    public String getName() {
        return "resource";
    }

    /**
     * 执行资源路径处理
     *
     * @param handler 指令处理器
     * @throws Exception 异常
     */
    @Override
    public void execute(DirectiveHandler handler) throws Exception {
        String src = handler.getString("src", "#");
        if (src.startsWith("/storage") || src.startsWith("/theme")) {
            // 站内资源路径，拼接上下文路径
            String base = handler.getContextPath();
            handler.renderString(base + src);
        } else {
            // 外部资源路径，原样输出
            handler.renderString(src);
        }
    }

}