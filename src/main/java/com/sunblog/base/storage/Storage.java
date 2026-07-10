package com.sunblog.base.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件存储接口 —— 统一上传文件的存储方式
 *
 * <h3>为什么需要抽象成接口？</h3>
 * 项目的文件（用户头像、文章图片等）可以存储在：
 * 1. 本地磁盘（开发环境）—— LocalStorageImpl
 * 2. 阿里云 OSS（生产环境）—— AliyunOSSStorageImpl
 * 3. 腾讯云 COS（生产环境）—— COSStorageImpl
 * 4. 华为云 OBS（生产环境）—— OBSStorageImpl
 *
 * Storage 接口定义了统一的存取操作，具体的存储实现通过策略模式切换。
 * 在 SiteOptions 配置中设置 storage_scheme 字段来选择具体实现。
 *
 * <h3>接口方法说明</h3>
 * <pre>
 * store(file, basePath)           → 上传文件，返回访问路径
 * storeScale(file, basePath, w)   → 上传并等比缩放图片（指定最大宽度）
 * storeScale(file, basePath, w,h) → 上传并裁剪到指定尺寸（如生成头像缩略图）
 * deleteFile(storePath)           → 删除已存储的文件
 * writeToStore(bytes, pathName)   → 直接写入字节数组到指定路径
 * </pre>
 *
 * <h3>store vs writeToStore 的区别</h3>
 * - store：接收 Spring 的 MultipartFile（HTTP 上传），内部自动生成文件名防止冲突
 * - writeToStore：接收 byte[] 和完整路径名，用于代码内部生成的文件（如压缩后的图片）
 */
public interface Storage {

	/* *
	 * @param file     Spring 上传文件对象
	 * @throws Exception 文件读写或校验失败时抛出
	 */
	String store(MultipartFile file, String basePath) throws Exception;

	/* *
	 * @param file     Spring 上传文件对象
	 * @param maxWidth 压缩后图片的最大宽度
	 * @throws Exception 文件读写或压缩失败时抛出
	 */
	String storeScale(MultipartFile file, String basePath, int maxWidth) throws Exception;

	/* *
	 * @param file     Spring 上传文件对象
	 * @param width    目标宽度
	 * @param height   目标高度
	 * @throws Exception 文件读写或压缩失败时抛出
	 */
	String storeScale(MultipartFile file, String basePath, int width, int height) throws Exception;

	void deleteFile(String storePath);

	/* *
	 * @param bytes           文件字节数据
	 * @param pathAndFileName 完整路径与文件名
	 * @throws Exception 文件读写失败时抛出
	 */
	String writeToStore(byte[] bytes, String pathAndFileName) throws Exception;
}
