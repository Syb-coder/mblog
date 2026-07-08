package com.mtons.mblog.base.utils;

/**
 * MD5 计算工具入口 — 独立的 main 方法类，用于在开发和运维阶段
 * 快速计算特定密码的 MD5 哈希值。
 * <p>
 * 用途：项目使用 MD5 加密密码，当需要手动构造数据库初始用户密码或
 * 调试时可用此类直接计算结果。
 * </p>
 * <p>
 * 示例输出：
 * <ul>
 *   <li>MD5 of 'admin123': 0192023a7bbd7324b8e6e1e34da8f0c9</li>
 *   <li>MD5 of 'admin': 21232f297a57a5a041133e9e8ca06eb1</li>
 * </ul>
 * </p>
 */
public class MD5Calc {
    public static void main(String[] args) {
        System.out.println("MD5 of 'admin123': " + MD5.md5("admin123"));
        System.out.println("MD5 of 'admin': " + MD5.md5("admin"));
        System.out.println("MD5 of '123456': " + MD5.md5("123456"));
    }
}
