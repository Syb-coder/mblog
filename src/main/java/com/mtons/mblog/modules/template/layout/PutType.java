package com.mtons.mblog.modules.template.layout;

import java.io.IOException;
import java.io.Writer;

/**
 * 布局内容写入策略枚举
 * <p>
 * 定义 {@link PutDirective} 注入的内容与 {@link BlockDirective} 默认内容
 * 组合输出的策略，参考 FreeMarker 布局插件的常见实现。
 * </p>
 *
 * @since 4.0.0
 */
public enum PutType {
    /**
     * 替换策略：用注入内容完全覆盖 block 默认内容
     */
    REPLACE {
        @Override
        public void write(Writer out, String bodyResult, String putContents) throws IOException {
            out.write(putContents);
        }
    },

    /**
     * 追加策略：先输出 block 默认内容，再追加注入内容
     */
    APPEND {
        @Override
        public void write(Writer out, String bodyResult, String putContents) throws IOException {
            out.write(bodyResult);
            out.write(putContents);
        }
    },

    /**
     * 前置策略：先输出注入内容，再输出 block 默认内容
     */
    PREPEND {
        @Override
        public void write(Writer out, String bodyResult, String putContents) throws IOException {
            out.write(putContents);
            out.write(bodyResult);
        }
    };

    /**
     * 按策略写入内容到输出流
     *
     * @param out         输出流
     * @param bodyResult  block 默认内容
     * @param putContents 注入内容
     * @throws IOException IO 异常
     */
    public abstract void write(Writer out, String bodyResult, String putContents) throws IOException;
}
