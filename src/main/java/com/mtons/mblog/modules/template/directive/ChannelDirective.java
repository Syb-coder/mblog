package com.mtons.mblog.modules.template.directive;

import com.mtons.mblog.modules.service.ChannelService;
import com.mtons.mblog.modules.template.DirectiveHandler;
import com.mtons.mblog.modules.template.TemplateDirective;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 * FreeMarker 使用方式：
 * <pre>{@code
 * <@channel id=1>
 *     ${result.name}
 * </@channel>
 * }</pre>
 * <p>
 * 支持参数：
 * <ul>
 *     <li>id：栏目 ID（默认 0）</li>
 * </ul>
 * 输出：result - 栏目对象
 */
@Component
public class ChannelDirective extends TemplateDirective {
    /**
     * 单数结果变量名，FreeMarker 模板中通过 <code>${result.name}</code> 引用单个栏目对象
     */
    public static final String RESULT = "result";

    @Autowired
    private ChannelService channelService;

    /**
     * 获取指令名称
     *
     * @return 指令名称 "channel"
     */
    @Override
    public String getName() {
        return "channel";
    }

    /* *
     * @param handler 指令处理器
     * @throws Exception 异常
     */
    @Override
    public void execute(DirectiveHandler handler) throws Exception {
        Integer id = handler.getInteger("id", 0);
        handler.put(RESULT, channelService.getById(id)).render();
    }
}
