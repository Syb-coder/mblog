package com.mtons.mblog.modules.template;

import com.mtons.mblog.base.lang.Consts;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.util.Map;

/**
 * FreeMarker 自定义指令基类 —— 让模板能调用 Java Service 查询数据
 *
 * <h3>为什么需要自定义 FreeMarker 指令？</h3>
 * FreeMarker 模板默认只能展示 Controller 传入的 Model 数据。
 * 但如果想在任意页面直接查询数据库（比如 "侧边栏最新文章"、"标签云"、"热门评论"），
 * 就需要自定义指令。
 *
 * 项目通过继承 TemplateDirective 实现这些插值：
 * <pre>
 * &lt;@posts userId=1 pn=1&gt;    ← 调用 PostsDirective 查询文章列表
 *   &lt;#list results as row&gt;    ← results 是查询结果
 *     ${row.title}
 *   &lt;/#list&gt;
 * &lt;/@posts&gt;
 * </pre>
 *
 * 这样每个页面都可以用 &lt;@xxx&gt; 标签来加载需要的数据，而不需要每个 Controller
 * 都去查询并放入 Model。
 *
 * <h3>执行流程</h3>
 * <pre>
 * FreeMarker 遇到 &lt;@posts&gt;
 *   → 调用 execute(Environment, parameters, ...)
 *     → 包装成 DirectiveHandler
 *       → 调用子类实现的 execute(DirectiveHandler)
 *         → 中间通常调用 Service 层查询数据
 *         → 通过 handler.put(RESULTS, data) 设置结果
 *           → 模板中就可以通过 #list results as row 遍历数据
 * </pre>
 *
 * <h3>分页</h3>
 * wrapPageable() 方法从指令参数中提取 pageNo 和 size，封装成 Spring Data 的 PageRequest。
 * 默认按 id 降序，也可传入自定义 Sort。
 */
public abstract class TemplateDirective implements TemplateDirectiveModel {
    /**
     * 指令结果变量名，FreeMarker 模板中通过 <code>&lt;#list results as row&gt;</code> 引用
     */
    public static final String RESULTS = "results";

    /**
     * FreeMarker 指令执行入口
     * <p>
     * 异常统一包装为 {@link TemplateException}。
     *
     * @param env        FreeMarker 环境
     * @param parameters 指令参数
     * @param body       指令体
     * @throws IOException      IO 异常
     */
    @Override
    public void execute(Environment env, Map parameters,
                        TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        try {
            execute(new DirectiveHandler(env, parameters, loopVars, body));
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new TemplateException(e, env);
        }
    }

    /**
     * 获取指令名称
     *
     * @return 指令名称
     */
    public abstract String getName();

    /**
     * 执行指令逻辑
     *
     * @param handler 指令处理器
     * @throws Exception 异常
     */
    public abstract void execute(DirectiveHandler handler) throws Exception;

    /**
     * 封装分页对象，默认按 id 降序排序
     *
     * @param handler 指令处理器
     * @return 分页对象
     * @throws Exception 异常
     */
    public PageRequest wrapPageable(DirectiveHandler handler) throws Exception {
        return wrapPageable(handler, Sort.by(Sort.Direction.DESC, "id"));
    }

    /**
     * 封装分页对象
     *
     * @param handler DirectiveHandler
     * @param sort    排序对象
     * @return PageRequest
     * @throws Exception 异常
     */
    public PageRequest wrapPageable(DirectiveHandler handler, Sort sort) throws Exception {
        int pageNo = handler.getInteger("pageNo", 1);
        int size = handler.getInteger("size", Consts.PAGE_DEFAULT_SIZE);
        if (null == sort) {
            return PageRequest.of(pageNo - 1, size);
        } else {
            return PageRequest.of(pageNo - 1, size, sort);
        }
    }
}
