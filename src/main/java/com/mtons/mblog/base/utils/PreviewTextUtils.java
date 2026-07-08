package com.mtons.mblog.base.utils;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * 富文本预览处理工具 —— 基于 Jsoup 的 HTML 清洗与摘要生成
 *
 * <h3>功能</h3>
 * - getText：从 HTML 中提取纯文本（去掉所有标签），用于文章列表页的摘要字段
 * - getSimpleHtml：只保留 b/em/i/strong/u 等基础文本格式标签
 * - removeHideHtml：移除除 <hide> 之外的所有标签（用于折叠内容预览）
 * - extractImage：抽取文章中所有 img 标签的 src，用于缩略图展示
 *
 * <h3>为什么用 Jsoup 而不是正则表达式？</h3>
 * Jsoup 是 HTML 解析器，能正确处理各种格式不规范的 HTML，
 * 比正则表达式更安全、更可靠。
 * 而且 Jsoup 的 Safelist 提供了白名单过滤机制，可以防止 XSS 攻击。
 */
public class PreviewTextUtils {
    /**
     * 提取纯文本
     * @param html 代码
     * @return string
     */
    public static String getText(String html) {
        if (html == null)
            return null;
        return Jsoup.clean(html, Safelist.none()).trim();
    }

    /**
     * 提取纯文本
     * @param html 代码
     * @param length 提取文本长度
     * @return string
     */
    public static String getText(String html, int length){
        String text = getText(html);
        text = StringUtils.abbreviate(text, length);
        return text;
    }

    /**
     * 以下标签可以通过 (b, em, i, strong, u. 纯文本)
     * @param html 代码
     * @return string
     */
    public static String getSimpleHtml(String html) {
        if (html == null)
            return null;
        return Jsoup.clean(html, Safelist.simpleText());
    }

    /**
     * 移除隐藏标签以外的全部 HTML 标签
     * <p>仅保留 hide 标签，用于内容折叠场景的纯文本预览</p>
     *
     * @param html 原始 HTML
     * @return 清洗后的 HTML
     */
    public static String removeHideHtml(String html) {
        if (html == null)
            return null;
        return Jsoup.clean(html, Safelist.none().addTags("hide"));
    }

    /**
     * 获取文章中的img url
     * @param html 代码
     * @return string
     */
    public static List<String> extractImage(String html) {
        List<String> urls = new ArrayList<>();
        if (html == null)
            return urls;
        Document doc = Jsoup.parseBodyFragment(html);
        Elements images = doc.select("img");
        if (null != images) {
            for(Element el : images) {
                urls.add(el.attr("src"));
            }
        }
        return urls;
    }

}
