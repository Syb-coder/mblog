package com.mtons.mblog.modules.template.directive;

import com.mtons.mblog.modules.template.DirectiveHandler;
import com.mtons.mblog.modules.template.TemplateDirective;
import org.springframework.stereotype.Component;

/**
 * 数字格式化指令
 * <p>
 * FreeMarker 使用方式：
 * <pre>{@code
 * <@num value=1500 />
 * }</pre>
 * <p>
 * 支持参数：
 * <ul>
 *     <li>value：待格式化的数字（默认 1）</li>
 * </ul>
 * 输出：格式化后的字符串
 * <ul>
 *     <li>大于 10000 显示为 "xm"（如 15000 -> 1m）</li>
 *     <li>大于 1000 显示为 "xk"（如 1500 -> 1k）</li>
 *     <li>其他显示原值</li>
 * </ul>
 */
@Component
public class NumberDirective extends TemplateDirective {
    /**
     * 获取指令名称
     *
     * @return 指令名称 "num"
     */
    @Override
    public String getName() {
        return "num";
    }

    /**
     * 执行数字格式化
     *
     * @param handler 指令处理器
     * @throws Exception 异常
     */
    @Override
    public void execute(DirectiveHandler handler) throws Exception {
        Integer value = handler.getInteger("value", 1);
        String out = value.toString();

        if (value > 1000) {
            out = value / 1000 + "k";
        } else if (value > 10000) {
            out = value / 10000 + "m";
        }
        handler.renderString(out);
    }

}
