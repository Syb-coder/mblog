package com.mtons.mblog.modules.template.layout;

import com.mtons.mblog.modules.template.DirectiveHandler;
import com.mtons.mblog.modules.template.TemplateDirective;
import freemarker.template.SimpleScalar;
import org.springframework.stereotype.Component;

/**
 * 布局内容注入指令
 * <p>
 * FreeMarker 使用方式：
 * <pre>{@code
 * <@layout.put block="content" type="replace">
 * </@layout.put>
 * }</pre>
 * <p>
 * 支持参数：
 * <ul>
 * </ul>
 *
 * @since 4.0.0
 */
@Component
public class PutDirective extends TemplateDirective {
    /**
     * 注入数据在 FreeMarker 变量空间中的前缀，避免与业务变量冲突
     */
    public static final String PUT_DATA_PREFIX = PutDirective.class.getCanonicalName() + ".";

    /**
     * block 参数名：指定要注入内容的 block 名称
     */
    public static final String PUT_BLOCK_NAME_PARAMETER = "block";

    /**
     * type 参数名：指定写入策略（REPLACE/APPEND/PREPEND）
     */
    public static final String PUT_TYPE_PARAMETER = "type";

    /**
     * 获取指令名称
     *
     * @return 指令名称 "layout.put"
     */
    @Override
    public String getName() {
        return "layout.put";
    }

    /**
     * 执行内容注入
     *
     * @param handler 指令处理器
     * @throws Exception 异常
     */
    @Override
    public void execute(DirectiveHandler handler) throws Exception {
        String blockName = handler.getString(PUT_BLOCK_NAME_PARAMETER);
        String type = handler.getString(PUT_TYPE_PARAMETER);
        PutType putType = null;
        if (null != type) {
            putType = PutType.valueOf(type.toUpperCase());
        }
        // 未指定策略时默认 REPLACE
        if (putType == null) {
            putType = PutType.REPLACE;
        }

        String bodyResult = handler.bodyResult();

        handler.getEnv().setVariable(BlockDirective.getBlockContentsVarName(blockName), new SimpleScalar(bodyResult));
        handler.getEnv().setVariable(BlockDirective.getBlockTypeVarName(blockName), new SimpleScalar(putType.name()));
    }

}
