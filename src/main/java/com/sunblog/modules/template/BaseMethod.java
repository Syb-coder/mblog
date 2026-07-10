package com.sunblog.modules.template;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 参数读取的便捷封装，统一委托 {@link TemplateModelUtils} 完成类型转换。
 */
public abstract class BaseMethod implements TemplateMethodModelEx {

    /**
     * 按索引获取字符串参数
     *
     * @param arguments 参数列表
     * @param index     参数索引
     */
    public String getString(List<TemplateModel> arguments, int index) throws TemplateModelException {
        return TemplateModelUtils.converString(getModel(arguments, index));
    }

    /**
     * 按索引获取 Integer 参数
     *
     * @param arguments 参数列表
     * @param index     参数索引
     */
    public Integer getInteger(List<TemplateModel> arguments, int index) throws TemplateModelException {
        return TemplateModelUtils.converInteger(getModel(arguments, index));
    }

    /**
     * 按索引获取 Long 参数
     *
     * @param arguments 参数列表
     * @param index     参数索引
     */
    public Long getLong(List<TemplateModel> arguments, int index) throws TemplateModelException {
        return TemplateModelUtils.converLong(getModel(arguments, index));
    }

    /**
     * 按索引获取 Date 参数
     *
     * @param arguments 参数列表
     * @param index     参数索引
     */
    public Date getDate(List<TemplateModel> arguments, int index) throws TemplateModelException {
        return TemplateModelUtils.converDate(getModel(arguments, index));
    }

    /**
     * @param arguments 参数列表
     * @param index     参数索引
     * @return 参数模型，索引越界时返回 null
     */
    public TemplateModel getModel(List<TemplateModel> arguments, int index) {
        if (index < arguments.size()) {
            return arguments.get(index);
        }
        return null;
    }
}