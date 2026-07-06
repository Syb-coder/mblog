package com.mtons.mblog.modules.template.layout;

import com.mtons.mblog.config.SiteOptions;
import com.mtons.mblog.modules.template.DirectiveHandler;
import com.mtons.mblog.modules.template.TemplateDirective;
import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;

/**
 * 布局继承指令
 * <p>
 * FreeMarker 使用方式：
 * <pre>{@code
 * <@layout.extends name="layout/main">
 * </@layout.extends>
 * }</pre>
 * <p>
 * 支持参数：
 * <ul>
 * </ul>
 *
 * @since 4.0.0
 */
@Component
public class ExtendsDirective extends TemplateDirective {
    @Autowired
    private SiteOptions siteOptions;

    /**
     * 获取指令名称
     *
     * @return 指令名称 "layout.extends"
     */
    @Override
    public String getName() {
        return "layout.extends";
    }

    /**
     * 执行布局继承
     *
     * @param handler 指令处理器
     * @throws Exception 异常
     */
    @Override
    public void execute(DirectiveHandler handler) throws Exception {
        String theme = siteOptions.getValue("theme");
        String layoutName =  handler.getString("name");
        // 根据路径是否以 / 开头，拼接主题前缀
        layoutName = layoutName.startsWith("/") ? theme + layoutName : theme + "/" + layoutName;
        handler.bodyResult();
        handler.getEnv().include(layoutName, null, true);
    }

}
