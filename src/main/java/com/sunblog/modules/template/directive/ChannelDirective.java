package com.sunblog.modules.template.directive;

import com.sunblog.modules.service.ChannelService;
import com.sunblog.modules.template.DirectiveHandler;
import com.sunblog.modules.template.TemplateDirective;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 栏目查询指令 —— 按 ID 获取单个栏目
 *
 * <h3>使用场景</h3>
 * 文章详情页通常需要显示文章所属的栏目名称，
 * 通过 &lt;@channel id=channelId&gt; 就能直接拿到 Channel 对象。
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

    /**
     * @param handler 指令处理器
     * @throws Exception 异常
     */
    @Override
    public void execute(DirectiveHandler handler) throws Exception {
        Integer id = handler.getInteger("id", 0);
        handler.put(RESULT, channelService.getById(id)).render();
    }
}