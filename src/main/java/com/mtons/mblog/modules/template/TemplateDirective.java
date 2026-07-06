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
 * FreeMarker 自定义指令基类
 * <p>
 * 职责：实现 {@link TemplateDirectiveModel}，将原生 FreeMarker 指令回调
 * 同时提供分页对象封装的便捷方法。
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
