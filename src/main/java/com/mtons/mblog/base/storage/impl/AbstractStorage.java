package com.mtons.mblog.base.storage.impl;

import com.mtons.mblog.base.lang.MtonsException;
import com.mtons.mblog.base.storage.Storage;
import com.mtons.mblog.base.utils.*;
import com.mtons.mblog.config.SiteOptions;
import com.mtons.mblog.modules.entity.Resource;
import com.mtons.mblog.modules.repository.ResourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * <p>
 * 提供文件上传、图片压缩、MD5 去重以及资源入库等通用流程编排，子类只需实现
 * 与 {@link #deleteFile(String)}）。
 * </p>
 * <p>
 * 关键依赖：
 * <ul>
 *   <li>{@link ResourceRepository}：用于 MD5 去重与资源元信息持久化</li>
 * </ul>
 * </p>
 *
 * @since 3.0
 */
@Slf4j
public abstract class AbstractStorage implements Storage {
    @Autowired
    protected SiteOptions options;
    @Autowired
    protected ResourceRepository resourceRepository;

    /**
     * 校验上传文件是否合法
     * <p>
     * </p>
     *
     * @param file 待校验的上传文件
     */
    protected void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new MtonsException("文件不能为空");
        }

        if (!FileKit.checkFileType(file.getOriginalFilename())) {
            throw new MtonsException("文件格式不支持");
        }
    }

    /* *
     * @param file     上传文件对象
     * @throws Exception 文件读写或入库失败时抛出
     */
    @Override
    public String store(MultipartFile file, String basePath) throws Exception {
        validateFile(file);
        return writeToStore(file.getBytes(), basePath, file.getOriginalFilename());
    }

    /* *
     * @param file     上传文件对象
     * @param maxWidth 压缩后图片最大宽度
     * @throws Exception 压缩或入库失败时抛出
     */
    @Override
    public String storeScale(MultipartFile file, String basePath, int maxWidth) throws Exception {
        validateFile(file);
        byte[] bytes = ImageUtils.scaleByWidth(file, maxWidth);
        return writeToStore(bytes, basePath, file.getOriginalFilename());
    }

    /* *
     * @param file     上传文件对象
     * @param width    目标宽度
     * @param height   目标高度
     * @throws Exception 裁剪压缩或入库失败时抛出
     */
    @Override
    public String storeScale(MultipartFile file, String basePath, int width, int height) throws Exception {
        validateFile(file);
        byte[] bytes = ImageUtils.screenshot(file, width, height);
        return writeToStore(bytes, basePath, file.getOriginalFilename());
    }

    /**
     * <p>
     * </p>
     *
     * @param bytes            文件字节内容
     * @param originalFilename 原始文件名（用于推断扩展名）
     * @throws Exception 写入文件或持久化失败时抛出
     */
    public String writeToStore(byte[] bytes, String src, String originalFilename) throws Exception {
        String md5 = MD5.md5(bytes);
        Resource resource = resourceRepository.findByMd5(md5);
        if (resource != null){
            return resource.getPath();
        }
        String path = FilePathUtils.wholePathName(src, originalFilename, md5);
        path = writeToStore(bytes, path);

        // 图片入库
        resource = new Resource();
        resource.setMd5(md5);
        resource.setPath(path);
        resource.setCreateTime(LocalDateTime.now());
        resourceRepository.save(resource);
        return path;
    }

}
