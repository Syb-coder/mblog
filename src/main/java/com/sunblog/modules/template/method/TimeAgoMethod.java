package com.sunblog.modules.template.method;

import freemarker.template.TemplateModelException;
import com.sunblog.modules.template.BaseMethod;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 例如「3 秒前」「5 分钟前」「2 小时前」「昨天」「3 天前」「2 月前」「1 年前」。
 * </p>
 */
public class TimeAgoMethod extends BaseMethod {
    /** 一分钟的毫秒数 */
    private static final long ONE_MINUTE = 60000L;
    /** 一小时的毫秒数 */
    private static final long ONE_HOUR = 3600000L;
    /** 一天的毫秒数 */
    private static final long ONE_DAY = 86400000L;
    /** 一周的毫秒数 */
    private static final long ONE_WEEK = 604800000L;

    /** 时间单位后缀：秒前 */
    private static final String ONE_SECOND_AGO = "秒前";
    /** 时间单位后缀：分钟前 */
    private static final String ONE_MINUTE_AGO = "分钟前";
    /** 时间单位后缀：小时前 */
    private static final String ONE_HOUR_AGO = "小时前";
    /** 时间单位后缀：天前 */
    private static final String ONE_DAY_AGO = "天前";
    /** 时间单位后缀：月前 */
    private static final String ONE_MONTH_AGO = "月前";
    /** 时间单位后缀：年前 */
    private static final String ONE_YEAR_AGO = "年前";
    /** 时间未知时的占位文案 */
    private static final String ONE_UNKNOWN = "未知";

    /**
     * <p>
     * 从参数列表第 0 位取出 Date，交由 format 方法格式化输出。
     * </p>
     * @throws TemplateModelException 参数类型不匹配或缺失时抛出
     */
    @Override
    public Object exec(List arguments) throws TemplateModelException {
        Date time = getDate(arguments, 0);
        return format(time);
    }

    /**
     * <p>
     * 小于 1 分钟 -> 秒前；
     * 小于 45 分钟 -> 分钟前；
     * 小于 24 小时 -> 小时前；
     * 小于 30 天 -> 天前；
     * 小于 12 个月（约 48 周）-> 月前；
     * 其余 -> 年前。
     * 当换算结果为 0 时补 1，避免出现「0 秒前」这种不合理文案。
     * </p>
     */
    public static String format(Date date) {
        if (null == date) {
            return ONE_UNKNOWN;
        }
        // 计算 target 与当前时刻的毫秒差，正值表示 target 在过去
        long delta = new Date().getTime() - date.getTime();
        if (delta < 1L * ONE_MINUTE) {
            long seconds = toSeconds(delta);
            return (seconds <= 0 ? 1 : seconds) + ONE_SECOND_AGO;
        }
        if (delta < 45L * ONE_MINUTE) {
            long minutes = toMinutes(delta);
            return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
        }
        if (delta < 24L * ONE_HOUR) {
            long hours = toHours(delta);
            return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;
        }
        if (delta < 48L * ONE_HOUR) {
            return "昨天";
        }
        if (delta < 30L * ONE_DAY) {
            long days = toDays(delta);
            return (days <= 0 ? 1 : days) + ONE_DAY_AGO;
        }
        if (delta < 12L * 4L * ONE_WEEK) {
            long months = toMonths(delta);
            return (months <= 0 ? 1 : months) + ONE_MONTH_AGO;
        } else {
            long years = toYears(delta);
            return (years <= 0 ? 1 : years) + ONE_YEAR_AGO;
        }
    }

    /**
     * 毫秒差转秒
     * @param date 毫秒差
     * @return 秒数
     */
    private static long toSeconds(long date) {
        return date / 1000L;
    }

    /**
     * 毫秒差转分钟
     * @param date 毫秒差
     * @return 分钟数
     */
    private static long toMinutes(long date) {
        return toSeconds(date) / 60L;
    }

    /**
     * 毫秒差转小时
     * @param date 毫秒差
     * @return 小时数
     */
    private static long toHours(long date) {
        return toMinutes(date) / 60L;
    }

    /**
     * 毫秒差转天
     * @param date 毫秒差
     * @return 天数
     */
    private static long toDays(long date) {
        return toHours(date) / 24L;
    }

    /**
     * 毫秒差转月（按 30 天近似）
     * @param date 毫秒差
     * @return 月数
     */
    private static long toMonths(long date) {
        return toDays(date) / 30L;
    }

    /**
     * 毫秒差转年（按 365 天近似，与月换算口径一致）
     * @param date 毫秒差
     * @return 年数
     */
    private static long toYears(long date) {
        return toMonths(date) / 365L;
    }
}
