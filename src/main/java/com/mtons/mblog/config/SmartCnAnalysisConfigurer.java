package com.mtons.mblog.config;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurationContext;
import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurer;
import org.springframework.stereotype.Component;

/**
 * Hibernate Search 中文分词配置器
 * <p>
 * 实现 {@link LuceneAnalysisConfigurer}，向 Hibernate Search 注册名为 {@code smartcn}
 * 的中文分词器，供全文索引与检索使用。基于 Lucene 的 SmartChineseAnalyzer，
 * 适用于中文文章内容的分词场景。
 * </p>
 * <p>
 * 注册后，可在实体 {@code @FullTextField} 注解中通过 {@code analyzer = "smartcn"} 引用。
 * </p>
 *
 */
@Component
public class SmartCnAnalysisConfigurer implements LuceneAnalysisConfigurer {

    /**
     * 注册自定义分词器到 Hibernate Search。
     *
     * @param context Lucene 分析器配置上下文
     */
    @Override
    public void configure(LuceneAnalysisConfigurationContext context) {
        // 注册名为 smartcn 的中文分词器，供全文索引字段引用
        context.analyzer("smartcn").instance(new SmartChineseAnalyzer());
    }
}
