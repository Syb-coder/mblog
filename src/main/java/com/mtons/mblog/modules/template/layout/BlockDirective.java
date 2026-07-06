package com.mtons.mblog.modules.template.layout;

import com.mtons.mblog.modules.template.DirectiveHandler;
import com.mtons.mblog.modules.template.TemplateDirective;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;
import org.springframework.stereotype.Component;

import java.io.Writer;

/**
 * <p>
 * FreeMarker 使用方式：
 * <pre>{@code
 * <@layout.block name="content">
 *     <!-- 默认内容 -->
 * </@layout.block>
 * }</pre>
 * <p>
 * 支持参数：
 * <ul>
 * </ul>
 * 输出：根据 {@link PutDirective} 注入的内容和 {@link PutType} 策略，
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

    /* *
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

    /* *
     * @param handler   指令处理器
     * @return 写入策略，未配置时默认 APPEND
     */
    private PutType getPutType(DirectiveHandler handler, String blockName) throws TemplateException {
        SimpleScalar putTypeScalar = (SimpleScalar) handler.getEnvModel(getBlockTypeVarName(blockName));
        if (putTypeScalar == null) {
            return PutType.APPEND;
        }

        return PutType.valueOf(putTypeScalar.getAsString());
    }

    /* *
     * @param handler   指令处理器
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
