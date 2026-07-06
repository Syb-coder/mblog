package com.mtons.mblog.base.utils;

public class MD5Calc {
    public static void main(String[] args) {
        System.out.println("MD5 of 'admin123': " + MD5.md5("admin123"));
        System.out.println("MD5 of 'admin': " + MD5.md5("admin"));
        System.out.println("MD5 of '123456': " + MD5.md5("123456"));
    }
}
