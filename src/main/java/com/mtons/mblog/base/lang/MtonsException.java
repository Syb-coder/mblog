package com.mtons.mblog.base.lang;

/**
 * 系统业务异常
 * <p>
 * 框架统一的运行时异常类型，用于在业务流程中抛出可预期的错误，
 * 通过 {@link #code} 字段承载业务错误码，便于上层统一处理与国际化。
 * </p>
 * <p>
 * 关键字段：
 * <ul>
 *   <li>{@link #code}：业务错误码，默认为 0，由调用方根据场景赋值</li>
 * </ul>
 * </p> *
 */
public class MtonsException extends RuntimeException {
	private static final long serialVersionUID = -7443213283905815106L;
	/** 业务错误码，用于标识具体的业务错误类型 */
	private int code;

	/**
	 * 默认构造方法
	 */
	public MtonsException() {
	}
	
	/**
	 * 以错误码构造异常，消息自动拼接为 "code=xxx" 形式便于排查
	 * 
	 * @param code 错误代码
	 */
	public MtonsException(int code) {
		super("code=" + code);
		this.code = code;
	}

	/**
	 * 以错误消息构造异常
	 * 
	 * @param message 错误消息
	 */
	public MtonsException(String message) {
		super(message);
	}

	/**
	 * 以捕获的根因异常构造，便于异常链传递
	 * 
	 * @param cause 捕获的异常
	 */
	public MtonsException(Throwable cause) {
		super(cause);
	}

	/**
	 * 同时携带错误消息与根因异常
	 * 
	 * @param message 错误消息
	 * @param cause 捕获的异常
	 */
	public MtonsException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * 同时携带错误码与错误消息
	 * 
	 * @param code 错误代码
	 * @param message 错误消息
	 */
	public MtonsException(int code, String message) {
		super(message);
		this.code = code;
	}

	/**
	 * 获取业务错误码
	 * 
	 * @return 错误码
	 */
	public int getCode() {
		return code;
	}

	/**
	 * 设置业务错误码
	 * 
	 * @param code 错误码
	 */
	public void setCode(int code) {
		this.code = code;
	}
}
