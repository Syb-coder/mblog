package com.mtons.mblog.modules.template.directive;

import com.mtons.mblog.modules.service.LinksService;
import com.mtons.mblog.modules.template.DirectiveHandler;
import com.mtons.mblog.modules.template.TemplateDirective;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 友情链接指令
 * <p>
 * FreeMarker 使用方式：
 * <pre>{@code
 * <@links>
 *     <#list results as row>
 *         <li><a href="${row.url}">${row.name}</a></li>
 *     </#list>
 * </@links>
 * }</pre>
 * <p>
 * 支持参数：无
 * <p>
 * 输出：results - 友情链接列表
 */
@Component
public class LinksDirective extends TemplateDirective {
    @Autowired
    private LinksService linksService;

    /**
     * 获取指令名称
     *
     * @return 指令名称 "links"
     */
    @Override
    public String getName() {
        return "links";
    }

    /**
     * 执行友情链接查询
     *
     * @param handler 指令处理器
     * @throws Exception 异常
     */
    @Override
    public void execute(DirectiveHandler handler) throws Exception {
        handler.put(RESULTS, linksService.findAll()).render();
    }
}
