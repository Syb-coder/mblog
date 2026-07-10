package com.sunblog.modules.template.layout;

import com.sunblog.modules.template.DirectiveHandler;
import com.sunblog.modules.template.TemplateDirective;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;
import org.springframework.stereotype.Component;

import java.io.Writer;

/**
 * 布局区块指令：在父布局模板中定义可被子页面替换/追加/前置的区块。
 * <p>
 * 与 {@link PutDirective} 配合实现模板继承的"插槽"（slot）机制：
 * <ul>
 *   <li>父布局中用 {@code <@layout.block name="content">...default...</@layout.block>} 定义区块；</li>
 *   <li>子页面用 {@code <@layout.put block="content">...override...</@layout.put>} 注入内容；</li>
 *   <li>注入策略由 {@link PutType} 决定：REPLACE（替换默认）、APPEND（追加）、PREPEND（前置）。</li>
 * </ul>
 * </p>
 * <p>
 * 实现机制：{@link #execute} 通过 FreeMarker 变量空间读取 Put 指令写入的注入内容，
 * 若无注入则输出 block 体中的默认内容。
 * </p>
 * <p>
 * FreeMarker 使用方式：
 * <pre>{@code
 * <@layout.block name="content">
 *     <!-- 默认内容，当子页面未注入时显示 -->
 * </@layout.block>
 * }</pre>
 * </p>
 *
 * @since 4.0.0
 */
@Component
public class BlockDirective extends TemplateDirective {
    /**
     * block 名称参数名，FreeMarker 模板中通过 <code>&lt;@layout.block name="content"&gt;</code> 传入
     */
    public static final String BLOCK_NAME_PARAMETER = "name";

    /**
     * 获取指令名称
     *
     * @return 指令名称 "layout.block"
     */
    @Override
    public String getName() {
        return "layout.block";
    }

    /**
     * 执行区块逻辑
     * <p>
     * 1. 从参数中获取 blockName；
     * 2. 从 FreeMarker 变量空间读取 Put 指令写入的注入策略与注入内容；
     * 3. 按策略组合输出默认内容与注入内容。
     * </p>
     *
     * @param handler 指令处理器
     * @throws Exception 异常
     */
    @Override
    public void execute(DirectiveHandler handler) throws Exception {
        String blockName = handler.getString(BLOCK_NAME_PARAMETER);
        PutType putType = getPutType(handler, blockName);
        String bodyResult = handler.bodyResult();

        Writer out = handler.getEnv().getOut();
        String putContents = getPutContents(handler, blockName);
        putType.write(out, bodyResult, putContents);
    }

    /**
     * 获取当前 block 的注入策略
     * <p>从 FreeMarker 变量空间读取 Put 指令写入的 type 变量。</p>
     *
     * @param handler 指令处理器
     * @return 写入策略，未配置时默认 APPEND
     */
    private PutType getPutType(DirectiveHandler handler, String blockName) throws TemplateException {
        SimpleScalar putTypeScalar = (SimpleScalar) handler.getEnvModel(getBlockTypeVarName(blockName));
        if (putTypeScalar == null) {
            return PutType.APPEND;
        }

        return PutType.valueOf(putTypeScalar.getAsString());
    }

    /**
     * 获取当前 block 的注入内容
     * <p>从 FreeMarker 变量空间读取 Put 指令写入的 contents 变量。</p>
     *
     * @param handler 指令处理器
     * @return 注入内容，未注入时返回空字符串
     */
    private String getPutContents(DirectiveHandler handler, String blockName) throws TemplateModelException {
        SimpleScalar putContentsModel = (SimpleScalar) handler.getEnvModel(getBlockContentsVarName(blockName));
        String putContents = "";
        if (putContentsModel != null) {
            putContents = putContentsModel.getAsString();
        }
        return putContents;
    }

    public static String getBlockContentsVarName(String blockName) {
        return PutDirective.PUT_DATA_PREFIX + blockName + ".contents";
    }

    public static String getBlockTypeVarName(String blockName) {
        return PutDirective.PUT_DATA_PREFIX + blockName + ".type";
    }
}
