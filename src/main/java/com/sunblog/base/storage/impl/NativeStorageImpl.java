package com.sunblog.base.storage.impl;

import com.sunblog.base.utils.FileKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * <p>
 * 将文件写入站点配置中指定的本地目录（{@link com.sunblog.config.SiteOptions#getLocation()}），
 * </p>
 *
 * @since  3.0
 */
@Slf4j
@Component
public class NativeStorageImpl extends AbstractStorage {

    /**
     * <p>
     * </p>
     *
     */
    @Override
    public void deleteFile(String storePath) {
        File file = new File(getStoragePath() + storePath);

        if (file.exists() && !file.isDirectory()) {
            file.delete();
            log.info("fileRepo delete " + storePath);
        }
    }

    /**
     * 将字节数据写入本地文件系统
     *
     * @param bytes           待写入的字节数据
     * @param pathAndFileName 相对路径与文件名
     * @return 相对路径（与入参一致）
     * @throws Exception 文件写入失败时抛出
     */
    @Override
    public String writeToStore(byte[] bytes, String pathAndFileName) throws Exception {
        String dest = getStoragePath() + pathAndFileName;
        FileKit.writeByteArrayToFile(bytes, dest);
        return pathAndFileName;
    }

    private String getStoragePath() {
        return options.getLocation();
    }

}
