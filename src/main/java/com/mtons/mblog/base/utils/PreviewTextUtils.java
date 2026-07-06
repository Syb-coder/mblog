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
 * 富文本预览处理工具
 * <p>
 * 基于 Jsoup 提供纯文本提取、限定标签白名单清洗以及图片地址抽取等能力，
 * 用于文章摘要生成与列表预览场景。
 * </p>
 *
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
