package com.sunblog.base.utils;

import org.commonmark.Extension;
import org.commonmark.ext.front.matter.YamlFrontMatterExtension;
import org.commonmark.ext.gfm.tables.TableBlock;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.AttributeProvider;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Markdown 渲染工具
 * <p>
 * 基于 commonmark-java 实现 Markdown 转 HTML，集成 YAML Front Matter 与 GFM 表格扩展，
 * 并在渲染表格时注入 Bootstrap 样式类，便于前端直接展示。
 * </p>
 *
 * on 2019/3/8
 */
public class MarkdownUtils {
    private static final List<Extension> EXTENSIONS = Arrays.asList(
            YamlFrontMatterExtension.create(),
            TablesExtension.create()
    );

    /**
     * 解析Markdown文档
     */
    private static final Parser PARSER = Parser.builder().extensions(EXTENSIONS).build();

    /**
     * 渲染HTML文档
     */
    private static final HtmlRenderer RENDERER = HtmlRenderer.builder().extensions(EXTENSIONS).attributeProviderFactory(context -> new BlogAttributeProvider()).build();

    /**
     * 渲染Markdown
     *
     * @param content content
     * @return String
     */
    public static String renderMarkdown(String content) {
        final Node document = PARSER.parse(content);
        return RENDERER.render(document);
    }

    /**
     * 自定义属性提供者，为特定节点注入额外的 HTML 属性
     */
    static class BlogAttributeProvider implements AttributeProvider {

        /**
         * 为表格节点注入 Bootstrap 表格样式类
         *
         * @param node 当前节点
         * @param s    标签名
         */
        @Override
        public void setAttributes(Node node, String s, Map<String, String> map) {
            if (node instanceof TableBlock) {
                map.put("class", "table table-bordered");
            }
        }
    }
}
