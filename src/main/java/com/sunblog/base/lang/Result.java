package com.sunblog.base.lang;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应消息对象
 * <p>
 * 用于 Controller 层向客户端返回标准化结果，包含状态码、提示消息与业务数据三部分。
 * 通过泛型 {@code <T>} 支持任意类型的数据载荷，配合静态工厂方法快速构建成功/失败结果。
 * </p>
 * <p>
 * 关键字段：
 * <ul>
 *   <li>{@link #code}：状态码，0 表示成功，-1 表示失败</li>
 *   <li>{@link #message}：面向用户的提示消息</li>
 *   <li>{@link #data}：业务数据载荷</li>
 * </ul>
 * </p>
 *
 */
@Data
public class Result<T> implements Serializable {
    private static final long serialVersionUID = -1491499610244557029L;

    /** 成功状态码 */
    public static final int SUCCESS = 0;
    /** 失败状态码 */
    public static final int ERROR = -1;

    /**
     * 状态码 0: success, -1: error
     */
    private int code;

    /**
     * 提示消息
     */
    private String message;

    /**
     * 结果数据
     */
    private T data;

    /**
     * 全参构造方法
     *
     * @param code    状态码
     * @param message 提示消息
     * @param data    业务数据
     */
    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 构建无数据的成功结果
     *
     * @param <T> 业务数据类型
     * @return 不携带数据的成功 Result
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 构建携带数据的成功结果，使用默认提示消息
     *
     * @param <T>  业务数据类型
     * @param data 业务数据
     * @return 携带数据的成功 Result
     */
    public static <T> Result<T> success(T data) {
        return success("操作成功", data);
    }

    /**
     * 构建仅含提示消息的成功结果，不携带数据
     *
     * @param <T>     业务数据类型
     * @param message 提示消息
     * @return 仅含提示消息的成功 Result
     */
    public static <T> Result<T> successMessage(String message) {
        return success(message, null);
    }

    /**
     * 构建携带消息与数据的成功结果
     *
     * @param <T>     业务数据类型
     * @param message 提示消息
     * @param data    业务数据
     * @return 携带消息与数据的成功 Result
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(Result.SUCCESS, message, data);
    }

    /**
     * 构建默认错误码的失败结果
     *
     * @param <T>     业务数据类型
     * @param message 错误提示消息
     * @return 失败 Result
     */
    public static <T> Result<T> failure(String message) {
        return failure(Result.ERROR, message);
    }

    /**
     * 构建指定错误码的失败结果
     *
     * @param <T>     业务数据类型
     * @param code    错误码
     * @param message 错误提示消息
     * @return 失败 Result
     */
    public static <T> Result<T> failure(int code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 判断是否为成功结果
     *
     * @return true 表示成功，false 表示失败
     */
    public boolean isOk() {
        return code == SUCCESS;
    }

    /**
     * 判断是否为失败结果
     *
     * @return true 表示失败，false 表示成功
     */
    public boolean isError() {
        return !isOk();
    }

}
