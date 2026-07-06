package com.mtons.mblog.base.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * <p>
 * </p> *
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
