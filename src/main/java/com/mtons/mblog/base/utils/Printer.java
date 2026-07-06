package com.mtons.mblog.base.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * 日志打印工具
 * <p>
 * 对 Slf4j 日志门面做薄封装，统一系统中 info/debug/warn/error 的调用入口，
 * 便于后续替换或增强（如统一附加上下文信息）。
 * </p>
 *
 */
@Slf4j
public class Printer {
    /**
     * 输出 INFO 级别日志
     *
     * @param message 日志消息
     */
    public static void info(String message) {
        log.info(message);
    }

    /**
     * 输出 DEBUG 级别日志
     *
     * @param message 日志消息
     */
    public static void debug(String message) {
        log.debug(message);
    }

    /**
     * 输出 WARN 级别日志
     *
     * @param message 日志消息
     */
    public static void warn(String message) {
        log.warn(message);
    }

    /**
     * 输出 ERROR 级别日志
     *
     * @param message 日志消息
     */
    public static void error(String message) {
        log.error(message);
    }

    /**
     * 输出 ERROR 级别日志并附带异常堆栈
     *
     * @param message 日志消息
     * @param t       异常对象
     */
    public static void error(String message, Throwable t) {
        log.error(message, t);
    }

    /**
     * 输出 ERROR 级别日志，仅以异常消息作为日志内容
     *
     * @param t 异常对象
     */
    public static void error(Throwable t) {
        log.error(t.getMessage(), t);
    }
}
