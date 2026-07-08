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
 * 布局继承指令：子页面通过此指令声明继承哪个父布局模板。
 * <p>
 * 与 FreeMarker 标准 {@code <#include>} 的区别：继承后子页面可通过
 * {@link PutDirective} 和 {@link BlockDirective} 注入内容到父布局的指定区块，
 * 实现类似 Java 继承的模板复用模式 — 父布局定义通用框架（header/footer/sidebar），
 * 子页面只需提供差异化内容。
 * </p>
 * <p>
 * FreeMarker 使用方式：
 * <pre>{@code
 * <@layout.extends name="layout/main">
 *   <@layout.put block="content" type="replace">
 *     <!-- 子页面内容 -->
 *   </@layout.put>
 * </@layout.extends>
 * }</pre>
 * </p>
 * <p>
 * 实现细节：{@link #execute} 先执行 body 体（即子页面的 Put 指令），
 * 再将 body 结果丢弃，转而 include 父布局模板。父布局中的 Block 指令
 * 会读取 Put 指令注入到 FreeMarker 变量空间的内容并组合输出。
 * </p>
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
