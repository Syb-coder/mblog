package com.mtons.mblog.base.utils;

import org.apache.commons.io.FileUtils;

import jakarta.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * 文件工具类
 * <p>
 * 提供文件类型校验、文件名/后缀解析以及字节数组落盘等通用能力，
 * </p>
 *
 * @create - 2018/3/9
 */
public class FileKit {
    /** 允许上传的图片扩展名白名单 */
    private static final List<String> allowFiles = Arrays.asList(".gif", ".png", ".jpg", ".jpeg", ".bmp");

    /**
     * 校验文件名后缀是否在允许的图片格式白名单内
     *
     * @param fileName 待校验文件名
     * @return true 表示允许上传，false 表示被拒绝
     */
    public static boolean checkFileType(String fileName) {
        Iterator<String> type = allowFiles.iterator();
        while (type.hasNext()) {
            String ext = type.next();
            if (fileName.toLowerCase().endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 截取文件名中除扩展名之外的部分
     *
     * @param filename 完整文件名（非空）
     * @return 不含扩展名的主文件名
     */
    public static String getFilename(@NotNull String filename) {
        int pos = filename.lastIndexOf(".");
        return filename.substring(0, pos);
    }

    /**
     * 获取文件扩展名（含点号）
     *
     * @param filename 完整文件名
     * @return 扩展名，例如 ".jpg"
     */
    public static String getSuffix(String filename) {
        int pos = filename.lastIndexOf(".");
        return filename.substring(pos);
    }

    /**
     * 将字节数组写入指定路径的文件
     *
     * @param bytes 待写入的字节数据
     * @param dest  目标文件绝对路径
     * @throws IOException 写入失败时抛出
     */
    public static void writeByteArrayToFile(byte[] bytes, String dest) throws IOException {
        FileUtils.writeByteArrayToFile(new File(dest), bytes);
    }


}
