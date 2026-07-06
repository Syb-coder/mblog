package com.mtons.mblog.web.formatter;

import lombok.extern.slf4j.Slf4j;

import java.beans.PropertyEditorSupport;
import java.util.regex.Pattern;

/**
 * XSS 防护字符串转义编辑器
 * <p>
 * 职责说明：
 * <ul>
 *   <li>继承自 Spring 的 {@link PropertyEditorSupport}，作为自定义 {@link java.beans.PropertyEditor}
 *       注册到 Spring 数据绑定流程中；</li>
 *   <li>在表单数据绑定阶段对字符串进行清洗，过滤常见的 XSS 攻击脚本
 *       （如 {@code <script>} 标签、{@code javascript:} 协议、{@code eval()} 表达式等）；</li>
 *   <li>提供两个开关字段 {@link #escapeHTML} 与 {@link #escapeJavaScript}，
 *       支持按需开启 HTML 与 JavaScript 转义（当前实现仅作为配置占位，实际过滤逻辑统一执行）。</li>
 * </ul>
 *
 * 注册说明：
 * <p>
 * 通常通过 {@code @InitBinder} 在 Controller 中注册该 Editor，
 * 使 Spring 在表单数据绑定时自动调用 {@link #setAsText(String)} 方法对入参进行过滤。
 *
 * @see PropertyEditorSupport
 */
@Slf4j
public class StringEscapeEditor extends PropertyEditorSupport {
    /** 是否开启 HTML 转义（当前作为配置占位，过滤逻辑统一执行） */
    private boolean escapeHTML;

    /** 是否开启 JavaScript 转义（当前作为配置占位，过滤逻辑统一执行） */
    private boolean escapeJavaScript;

    /**
     * 默认构造方法
     * <p>
     * 使用默认配置（不开启 HTML / JavaScript 转义开关）创建编辑器实例。
     */
    public StringEscapeEditor() {
        super();
    }

    /**
     * 带参构造方法
     *
     * @param escapeHTML       是否开启 HTML 转义
     * @param escapeJavaScript 是否开启 JavaScript 转义
     */
    public StringEscapeEditor(boolean escapeHTML, boolean escapeJavaScript) {
        super();
        this.escapeHTML = escapeHTML;
        this.escapeJavaScript = escapeJavaScript;
    }

    /**
     * 将当前值转换为字符串形式返回
     *
     * @return 当前值的字符串表示（值为 null 时返回空字符串）
     */
    @Override
    public String getAsText() {
        Object value = getValue();
        return value != null ? value.toString() : "";
    }

    /**
     * 设置文本值，并对输入文本进行 XSS 清洗
     * <p>
     * <ol>
     *   <li>移除独立的 {@code </script>} 标签；</li>
     *   <li>移除独立的 {@code <script ...>} 标签；</li>
     *   <li>移除 {@code eval(...)} 表达式；</li>
     *   <li>移除 {@code expression(...)} 表达式；</li>
     *   <li>移除 {@code javascript:} 协议；</li>
     *   <li>移除 {@code vbscript:} 协议；</li>
     *   <li>移除 {@code onload=...} 等事件绑定表达式。</li>
     * </ol>
     *
     * @param text 待过滤的原始文本
     * @throws IllegalArgumentException 当文本处理失败时抛出
     */
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (text == null) {
            setValue(null);
        } else {
            String value = text;

            Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // 以下为 src='...' 类型表达式的过滤逻辑，当前已注释禁用
//            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
//            value = scriptPattern.matcher(value).replaceAll("");
//
//            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
//            value = scriptPattern.matcher(value).replaceAll("");

            // 移除独立的 </script> 闭合标签
            scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // 移除独立的 <script ...> 起始标签
            scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // 移除 eval(...) 表达式，防止动态代码执行
            scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // 移除 expression(...) 表达式，防止 CSS 注入
            scriptPattern = Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // 移除 javascript: 协议
            scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // 移除 vbscript: 协议
            scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // 移除 onload=... 等事件绑定表达式
            scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            setValue(value);
        }
    }
}
