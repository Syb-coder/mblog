package com.sunblog.modules.template;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Map;

/**
 * FreeMarker Directive 处理器
 * <p>
 * 职责：封装 FreeMarker 指令执行时的上下文环境，提供参数读取、
 */
public class DirectiveHandler {
    /** FreeMarker 渲染环境 */
    private Environment env;

    /** 循环变量数组 */
    private TemplateModel[] loopVars;

    /** 指令参数映射 */
    private Map<String, TemplateModel> parameters;

    /** 指令体 */
    private TemplateDirectiveBody body;

    /** 当前命名空间，用于 put 方法向 FreeMarker 变量空间写入数据 */
    private Environment.Namespace namespace;

    /**
     * 构建 DirectiveHandler
     *
     * @param env        FreeMarker 环境
     * @param parameters 指令参数
     * @param body       指令体
     */
    public DirectiveHandler(Environment env, Map<String, TemplateModel> parameters, TemplateModel[] loopVars,
                            TemplateDirectiveBody body) {
        this.env = env;
        this.loopVars = loopVars;
        this.parameters = parameters;
        this.body = body;
        this.namespace = (Environment.Namespace) env.getCurrentNamespace();
    }

    /**
     * 渲染指令体内容到输出流
     *
     * @throws IOException        IO 异常
     */
    public void render() throws IOException, TemplateException {
        Assert.notNull(body, "must have template directive body");
        body.render(env.getOut());
    }

    /**
     * 直接输出字符串内容
     *
     * @param text 待输出的字符串
     * @throws Exception 异常
     */
    public void renderString(String text) throws Exception {
        env.getOut().write(text);
    }

    /**
     * 获取 FreeMarker 环境
     *
     * @return FreeMarker 环境
     */
    public Environment getEnv() {
        return env;
    }

    /**
     * 渲染指令体并返回字符串结果
     *
     * @return 指令体渲染结果，body 为空时返回空字符串
     * @throws IOException        IO 异常
     */
    public String bodyResult() throws IOException, TemplateException {
        if (body == null) {
            return "";
        }

        StringWriter writer = new StringWriter();
        body.render(writer);
        return writer.toString();
    }

    /**
     * @param key   变量名
     * @param value 变量值
     * @return 当前处理器，支持链式调用
     */
    public DirectiveHandler put(String key, Object value) throws TemplateModelException {
        namespace.put(key, wrap(value));
        return this;
    }

    /**
     * 获取字符串参数
     *
     * @param name 参数名
     */
    public String getString(String name) throws TemplateModelException {
        return TemplateModelUtils.converString(getModel(name));
    }

    /**
     * 获取 Integer 参数
     *
     * @param name 参数名
     */
    public Integer getInteger(String name) throws TemplateModelException {
        return TemplateModelUtils.converInteger(getModel(name));
    }

    /**
     * 获取 Short 参数
     *
     * @param name 参数名
     */
    public Short getShort(String name) throws TemplateModelException {
        return TemplateModelUtils.converShort(getModel(name));
    }

    /**
     * 获取 Long 参数
     *
     * @param name 参数名
     */
    public Long getLong(String name) throws TemplateModelException {
        return TemplateModelUtils.converLong(getModel(name));
    }

    /**
     * 获取 Double 参数
     *
     * @param name 参数名
     */
    public Double getDouble(String name) throws TemplateModelException {
        return TemplateModelUtils.converDouble(getModel(name));
    }

    /**
     * 获取字符串数组参数
     *
     * @param name 参数名
     */
    public String[] getStringArray(String name) throws TemplateModelException {
        return TemplateModelUtils.converStringArray(getModel(name));
    }

    /**
     * 获取 Boolean 参数
     *
     * @param name 参数名
     */
    public Boolean getBoolean(String name) throws TemplateModelException {
        return TemplateModelUtils.converBoolean(getModel(name));
    }

    /**
     * 获取 Date 参数
     *
     * @param name 参数名
     */
    public Date getDate(String name) throws TemplateModelException {
        return TemplateModelUtils.converDate(getModel(name));
    }

    /**
     * @param name         参数名
     * @param defaultValue 默认值
     * @return 字符串值
     * @throws Exception 异常
     */
    public String getString(String name, String defaultValue) throws Exception {
        String result = getString(name);
        return null == result ? defaultValue : result;
    }

    /**
     * @param name         参数名
     * @param defaultValue 默认值
     * @return Integer 值
     * @throws Exception 异常
     */
    public Integer getInteger(String name, int defaultValue) throws Exception {
        Integer result = getInteger(name);
        return null == result ? defaultValue : result;
    }

    /**
     * @param name         参数名
     * @param defaultValue 默认值
     * @return Long 值
     * @throws Exception 异常
     */
    public Long getLong(String name, long defaultValue) throws Exception {
        Long result = getLong(name);
        return null == result ? defaultValue : result;
    }


    /**
     * 获取上下文路径
     *
     * @return 上下文路径，获取失败时返回 null
     */
    public String getContextPath() {
        String ret = null;
        try {
            ret =  TemplateModelUtils.converString(getEnvModel("base"));
        } catch (TemplateModelException e) {
        }
        return ret;
    }

    /**
     * 包装对象为 TemplateModel
     *
     * @param object 待包装对象
     * @return 包装后的 TemplateModel
     */
    public TemplateModel wrap(Object object) throws TemplateModelException {
        return env.getObjectWrapper().wrap(object);
    }

    public TemplateModel getEnvModel(String name) throws TemplateModelException {
        return env.getVariable(name);
    }

    /**
     * 根据参数名获取参数模型
     *
     * @param name 参数名
     * @return 参数模型
     */
    private TemplateModel getModel(String name) {
        return parameters.get(name);
    }

}