package com.sunblog.base.storage;

import com.sunblog.base.storage.impl.NativeStorageImpl;
import com.sunblog.config.SiteOptions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * </p>
 * <p>
 * 关键依赖：
 * <ul>
 * </ul>
 * </p>
 *
 * on 2019/1/21
 */
@Component
public class StorageFactory implements InitializingBean {
    /** 存储方案映射表，key 为方案名（如 native），value 为对应存储实现 */
    private final Map<String, Storage> fileRepoMap = new HashMap<>();
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private SiteOptions siteOptions;

    public boolean registry(String key, Storage storage) {
        if (fileRepoMap.containsKey(key)) {
            return false;
        }
        fileRepoMap.put(key, storage);
        return true;
    }

    /**
     * <p>
     * </p>
     *
     */
    public Storage get() {
        String scheme = siteOptions.getValue("storage_scheme");
        if (StringUtils.isBlank(scheme)) {
            scheme = "native";
        }
        return fileRepoMap.get(scheme);
    }

    /* *
     * @throws Exception 任何异常
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        fileRepoMap.put("native", applicationContext.getBean(NativeStorageImpl.class));
    }
}
