package com.sunblog.base.utils;

import org.apache.commons.io.FileUtils;

import jakarta.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * 文件工具类 —— 上传文件类型校验与文件名解析
 *
 * <h3>功能</h3>
 * - checkFileType：检查文件后缀是否在白名单中（.gif/.png/.jpg/.jpeg/.bmp）
 * - getFilename/getSuffix：解析文件名和扩展名
 * - writeByteArrayToFile：将字节数组写入磁盘（底层委托 commons-io FileUtils）
 *
 * <h3>为什么只允许图片格式？</h3>
 * 这个项目是博客系统，上传场景只有用户头像和文章中嵌入的图片，
 * 不需要支持 pdf/doc/zip 等其他文件类型。
 * 限制文件类型也是安全措施，防止上传恶意脚本文件。
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
