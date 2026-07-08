package com.mtons.mblog.base.utils;

import com.mtons.mblog.base.lang.MtonsException;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 摘要工具
 *
 * <h3>两个使用场景</h3>
 * 1. 密码加密：md5(password, salt) → 加盐二次摘要，存储在 User.password 字段
 * 2. 文件去重：md5(bytes) → 计算文件内容的 MD5，用于 Resource 表的 MD5 去重
 *
 * <h3>为什么用 32 进制大写出力？</h3>
 * BigInteger.toString(32) 比传统的 16 进制更短（32 进制用 0-9A-V 表示），
 * 适合作为存储文件名。MD5 本身就是 128 位，32 进制只需约 26 个字符。
 * 传统 16 进制需要 32 个字符。
 */
public class MD5 {

	/**
	 * 对字符串进行 Md5 加密
	 *
	 * @param input 原文
	 * @return md5后的密文
	 */
	public static String md5(String input) {
		return md5(input.getBytes());
	}

	/**
	 * 对字符串进行 Md5 加密
	 *
	 * @param input 原文
	 * @param salt 随机数
	 * @return string
	 */
	public static String md5(String input, String salt) {
		if(StringUtils.isEmpty(salt)) {
			salt = "";
		}
		return md5(salt + md5(input));
	}

	/**
	 * 计算字节数组的 MD5 摘要
	 * <p>
	 * 使用 32 进制大写输出，相比 16 进制更短，适合作为存储文件名。
	 * </p>
	 *
	 * @param bytes 待计算的字节数组
	 * @return 32 进制大写的 MD5 摘要字符串
	 */
	public static String md5(byte[] bytes)  {
		byte[] code;
		try {
			code = MessageDigest.getInstance("md5").digest(bytes);
		} catch (NoSuchAlgorithmException e) {
			throw new MtonsException(e.getMessage());
		}
		BigInteger bi = new BigInteger(code);
		return bi.abs().toString(32).toUpperCase();
	}

}
