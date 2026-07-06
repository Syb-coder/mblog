package com.mtons.mblog.base.utils;

import com.mtons.mblog.base.lang.MtonsException;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 摘要工具
 * <p>
 * 提供字符串与字节数组的 MD5 计算能力，支持加盐二次摘要，
 * 结果以 32 进制大写形式返回，用于密码与文件去重场景。
 * </p>
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
