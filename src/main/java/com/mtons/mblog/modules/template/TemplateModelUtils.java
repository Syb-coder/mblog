package com.mtons.mblog.modules.template;

import freemarker.template.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * FreeMarker 模型工具类
 * <p>
 * 转换方法，支持字符串、数值、布尔、日期等常用类型的解析。
 */
public class TemplateModelUtils {

    /** 完整日期格式字符串长度，对应 yyyy-MM-dd HH:mm:ss 共 19 位 */
    public static final int FULL_DATE_LENGTH = 19;
    /** 完整日期格式化器，匹配 yyyy-MM-dd HH:mm:ss */
    public static final DateFormat FULL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /** 短日期格式字符串长度，对应 yyyy-MM-dd 共 10 位 */
    public static final int SHORT_DATE_LENGTH = 10;
    /** 短日期格式化器，匹配 yyyy-MM-dd */
    public static final DateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 将 TemplateModel 转换为字符串
     *
     * @return 字符串值，model 为 null 时返回 null
     */
    public static String converString(TemplateModel model) throws TemplateModelException {
        if (null != model) {
            if (model instanceof TemplateScalarModel) {
                return ((TemplateScalarModel) model).getAsString();
            } else if ((model instanceof TemplateNumberModel)) {
                return ((TemplateNumberModel) model).getAsNumber().toString();
            }
        }
        return null;
    }

    /**
     * 将 TemplateModel 转换为 Hash 模型
     *
     * @return Hash 模型，model 为 null 时返回 null
     */
    public static TemplateHashModel converMap(TemplateModel model) throws TemplateModelException {
        if (null != model) {
            if (model instanceof TemplateHashModelEx) {
                return (TemplateHashModelEx) model;
            } else if (model instanceof TemplateHashModel) {
                return (TemplateHashModel) model;
            }
        }
        return null;
    }

    /**
     * 将 TemplateModel 转换为 Integer
     *
     * @return Integer 值，model 为 null 时返回 null
     */
    public static Integer converInteger(TemplateModel model) throws TemplateModelException {
        if (null != model) {
            if (model instanceof TemplateNumberModel) {
                return ((TemplateNumberModel) model).getAsNumber().intValue();
            } else if (model instanceof TemplateScalarModel) {
                String s = ((TemplateScalarModel) model).getAsString();
                if (isNotBlank(s)) {
                    try {
                        return Integer.parseInt(s);
                    } catch (NumberFormatException e) {
                    }
                }
            }
        }
        return null;
    }

    /**
     * 将 TemplateModel 转换为 Short
     *
     * @return Short 值，model 为 null 时返回 null
     */
    public static Short converShort(TemplateModel model) throws TemplateModelException {
        if (null != model) {
            if (model instanceof TemplateNumberModel) {
                return ((TemplateNumberModel) model).getAsNumber().shortValue();
            } else if (model instanceof TemplateScalarModel) {
                String s = ((TemplateScalarModel) model).getAsString();
                if (isNotBlank(s)) {
                    try {
                        return Short.parseShort(s);
                    } catch (NumberFormatException e) {
                    }
                }
            }
        }
        return null;
    }

    /**
     * 将 TemplateModel 转换为 Long
     *
     * @return Long 值，model 为 null 时返回 null
     */
    public static Long converLong(TemplateModel model) throws TemplateModelException {
        if (null != model) {
            if (model instanceof TemplateNumberModel) {
                return ((TemplateNumberModel) model).getAsNumber().longValue();
            } else if (model instanceof TemplateScalarModel) {
                String s = ((TemplateScalarModel) model).getAsString();
                if (isNotBlank(s)) {
                    try {
                        return Long.parseLong(s);
                    } catch (NumberFormatException e) {
                    }
                }
            }
        }
        return null;
    }

    /**
     * 将 TemplateModel 转换为 Double
     *
     * @return Double 值，model 为 null 时返回 null
     */
    public static Double converDouble(TemplateModel model) throws TemplateModelException {
        if (null != model) {
            if (model instanceof TemplateNumberModel) {
                return ((TemplateNumberModel) model).getAsNumber().doubleValue();
            } else if (model instanceof TemplateScalarModel) {
                String s = ((TemplateScalarModel) model).getAsString();
                if (isNotBlank(s)) {
                    try {
                        return Double.parseDouble(s);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        return null;
    }

    /**
     * 将 TemplateModel 转换为字符串数组
     *
     * @return 字符串数组，model 为 null 时返回 null
     */
    public static String[] converStringArray(TemplateModel model) throws TemplateModelException {
        if (model instanceof TemplateSequenceModel) {
            TemplateSequenceModel smodel = (TemplateSequenceModel) model;
            String[] values = new String[smodel.size()];
            for (int i = 0; i < smodel.size(); i++) {
                values[i] = converString(smodel.get(i));
            }
            return values;
        } else {
            String str = converString(model);
            if (isNotBlank(str)) {
                return split(str,',');
            }
        }
        return null;
    }

    /**
     * 将 TemplateModel 转换为 Boolean
     *
     * @return Boolean 值，model 为 null 时返回 null
     */
    public static Boolean converBoolean(TemplateModel model) throws TemplateModelException {
        if (null != model) {
            if (model instanceof TemplateBooleanModel) {
                return ((TemplateBooleanModel) model).getAsBoolean();
            } else if (model instanceof TemplateNumberModel) {
                return !(0 == ((TemplateNumberModel) model).getAsNumber().intValue());
            } else if (model instanceof TemplateScalarModel) {
                String temp = ((TemplateScalarModel) model).getAsString();
                if (isNotBlank(temp)) {
                    return Boolean.valueOf(temp);
                }
            }
        }
        return null;
    }

    /**
     * 将 TemplateModel 转换为 Date
     *
     * @return Date 值，model 为 null 时返回 null
     */
    public static Date converDate(TemplateModel model) throws TemplateModelException {
        if (null != model) {
            if (model instanceof TemplateDateModel) {
                return ((TemplateDateModel) model).getAsDate();
            } else if (model instanceof TemplateScalarModel) {
                String temp = trimToEmpty(((TemplateScalarModel) model).getAsString());
                return parseDate(temp);
            }
        }
        return null;
    }

    /**
     * 解析日期字符串
     * <p>
     * 根据字符串长度自动匹配格式：19 位匹配完整日期格式，10 位匹配短日期格式。
     *
     * @param date 日期字符串
     * @return Date 对象，解析失败时返回 null
     */
    public static Date parseDate(String date) {
        Date ret = null;
        try {
            if (FULL_DATE_LENGTH == date.length()) {
                ret = FULL_DATE_FORMAT.parse(date);
            } else if (SHORT_DATE_LENGTH == date.length()) {
                ret = SHORT_DATE_FORMAT.parse(date);
            }
        } catch (ParseException e) {
        }
        return ret;
    }
}
