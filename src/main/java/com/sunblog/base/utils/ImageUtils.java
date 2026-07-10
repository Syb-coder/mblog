package com.sunblog.base.utils;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Coordinate;
import net.coobird.thumbnailator.geometry.Position;
import net.coobird.thumbnailator.geometry.Positions;
import net.sf.ehcache.hibernate.regions.EhcacheCollectionRegion;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * 图片处理工具类
 * <p>
 * 基于 Thumbnailator 封装图片下载、压缩、裁剪与截图等常用操作，
 * </p>
 *
 * @since 3.0
 */
@Slf4j
public class ImageUtils {

    /**
     * 校验目标路径可用性
     *
     * @param dest 目标文件绝对路径
     * @throws IOException 路径不可创建或只读时抛出
     */
    public static void validate(String dest) throws IOException {
        File destFile = new File(dest);
        if (destFile.getParentFile() != null && !destFile.getParentFile().exists() && !destFile.getParentFile().mkdirs()) {
            throw new IOException("Destination \'" + dest + "\' directory cannot be created");
        } else if (destFile.exists() && !destFile.canWrite()) {
            throw new IOException("Destination \'" + dest + "\' exists but is read-only");
        }
    }

    /**
     * 下载远程图片并压缩为字节数组
     *
     * @param urlString 图片 URL
     * @return 压缩后的字节数组
     * @throws Exception 下载或解码失败时抛出
     */
    public static byte[] download(String urlString) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Thumbnails.of(new URL(urlString)).scale(1f).outputQuality(0.75f).toOutputStream(output);
        return output.toByteArray();
    }

    /**
     * 图片压缩,各个边按比例压缩
     *
     * @param builder Thumbnails.of
     * @param width   压缩后的宽度
     * @param height  压缩后的高度
     * @param <T>     T
     * @throws IOException IOException
     */
    public static <T> byte[] scale(Thumbnails.Builder<T> builder, int width, int height) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        builder.size(width, height).toOutputStream(output);
        return output.toByteArray();
    }

    /**
     * 根据最大宽度图片压缩
     *
     * @param file    原图位置
     * @param maxSize 指定压缩后最大边长
     * @throws IOException IOException
     */
    public static byte[] scaleByWidth(MultipartFile file, int maxSize) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Thumbnails.of(file.getInputStream()).width(maxSize).toOutputStream(output);
        return output.toByteArray();
    }

    /**
     * 按指定宽高压缩本地文件
     *
     * @param file   本地文件
     * @param width  目标宽度
     * @param height 目标高度
     * @return 压缩后的字节数组
     * @throws IOException IO 异常
     */
    public static byte[] scale(File file, int width, int height) throws IOException {
        return scale(Thumbnails.of(file), width, height);
    }

    /**
     * 按正方形边长压缩本地文件
     *
     * @param file    本地文件
     * @param maxSize 目标边长（宽高一致）
     * @return 压缩后的字节数组
     * @throws IOException IO 异常
     */
    public static byte[] scale(File file, int maxSize) throws IOException {
        return scale(file, maxSize, maxSize);
    }

    /**
     * 按指定宽高压缩上传文件
     *
     * @param file   上传文件
     * @param width  目标宽度
     * @param height 目标高度
     * @return 压缩后的字节数组
     * @throws IOException IO 异常
     */
    public static byte[] scale(MultipartFile file, int width, int height) throws IOException {
        return scale(Thumbnails.of(file.getInputStream()), width, height);
    }

    /**
     * 按正方形边长压缩上传文件
     *
     * @param file    上传文件
     * @param maxSize 目标边长（宽高一致）
     * @return 压缩后的字节数组
     * @throws IOException IO 异常
     */
    public static byte[] scale(MultipartFile file, int maxSize) throws IOException {
        return scale(file, maxSize, maxSize);
    }

    /**
     * 截图
     *
     * @param builder  Thumbnails.of
     * @param position the position
     * @param width    width
     * @param height   height
     * @param <T>      T
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    public static <T> byte[] screenshot(Thumbnails.Builder<T> builder, Position position, int width, int height) throws IOException, InterruptedException {
        BufferedImage image = builder.size(width, height).asBufferedImage();
        image = Thumbnails.of(image).size(width, height).crop(position).asBufferedImage();
        return toByte(image);
    }

    /**
     * 按坐标和宽高对本地文件截图
     *
     * @param file   本地文件
     * @param x      截图起点 x
     * @param y      截图起点 y
     * @param width  截图宽度
     * @param height 截图高度
     * @return 截图字节数组
     * @throws IOException          IO 异常
     * @throws InterruptedException 线程中断异常
     */
    public static byte[] screenshot(File file, int x, int y, int width, int height) throws IOException, InterruptedException {
        return screenshot(Thumbnails.of(file), new Coordinate(x, y), width, height);
    }

    /**
     * 按坐标和正方形边长对本地文件截图
     *
     * @param file 本地文件
     * @param x    截图起点 x
     * @param y    截图起点 y
     * @param size 截图边长
     * @return 截图字节数组
     * @throws IOException          IO 异常
     * @throws InterruptedException 线程中断异常
     */
    public static byte[] screenshot(File file, int x, int y, int size) throws IOException, InterruptedException {
        return screenshot(file, x, y, size, size);
    }

    /**
     * 居中裁剪方式对本地文件截图
     *
     * @param file   本地文件
     * @param width  目标宽度
     * @param height 目标高度
     * @return 截图字节数组
     * @throws IOException          IO 异常
     * @throws InterruptedException 线程中断异常
     */
    public static byte[] screenshot(File file, int width, int height) throws IOException, InterruptedException {
        return screenshot(Thumbnails.of(file), Positions.CENTER, width, height);
    }

    /**
     * 按坐标和宽高对上传文件截图
     *
     * @param file   上传文件
     * @param x      截图起点 x
     * @param y      截图起点 y
     * @param width  截图宽度
     * @param height 截图高度
     * @return 截图字节数组
     * @throws IOException          IO 异常
     * @throws InterruptedException 线程中断异常
     */
    public static byte[] screenshot(MultipartFile file, int x, int y, int width, int height) throws IOException, InterruptedException {
        return screenshot(Thumbnails.of(file.getInputStream()), new Coordinate(x, y), width, height);
    }

    /**
     * 按坐标和正方形边长对上传文件截图
     *
     * @param file 上传文件
     * @param x    截图起点 x
     * @param y    截图起点 y
     * @param size 截图边长
     * @return 截图字节数组
     * @throws IOException          IO 异常
     * @throws InterruptedException 线程中断异常
     */
    public static byte[] screenshot(MultipartFile file, int x, int y, int size) throws IOException, InterruptedException {
        return screenshot(file, x, y, size, size);
    }

    /**
     * 居中裁剪方式对上传文件截图
     *
     * @param file   上传文件
     * @param width  目标宽度
     * @param height 目标高度
     * @return 截图字节数组
     * @throws IOException          IO 异常
     * @throws InterruptedException 线程中断异常
     */
    public static byte[] screenshot(MultipartFile file, int width, int height) throws IOException, InterruptedException {
        return screenshot(Thumbnails.of(file.getInputStream()), Positions.CENTER, width, height);
    }

    /**
     * 将 BufferedImage 编码为 PNG 字节数组
     *
     * @param image 待编码图片
     * @return PNG 字节数组
     * @throws IOException 编码失败时抛出
     */
    private static byte[] toByte(BufferedImage image) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(image, "png", output);
        output.flush();
        byte[] bytes = output.toByteArray();
        output.close();
        return bytes;
    }
}
