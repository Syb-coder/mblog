package com.mtons.mblog.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 站点配置属性 Bean
 * <p>
 * {@link ContextStartup#reloadOptions(boolean)} 将数据库中的配置项同步到此 Map，
 * </p>
 * <p>
 * 关键字段说明：
 * <ul>
 *   <li>{@code controls}：前台功能开关，对应 {@code site.controls.*}</li>
 *   <li>{@code options}：动态配置项 Map，key 为配置键，value 为配置值</li>
 * </ul>
 * </p>
 *
 * @version 1.0
 * @date 2019/01/18
 */
@Configuration
@ConfigurationProperties(prefix = "site")
public class SiteOptions {

    private String version;

    private String location;

    /**
     * 前台控制器开关配置，对应 site.controls
     */
    private Controls controls;

    /**
     * 动态配置项 Map
     */
    private Map<String, String> options = new HashMap<>();

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Controls getControls() {
        return controls;
    }

    public void setControls(Controls controls) {
        this.controls = controls;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    /**
     * 读取指定配置项的字符串值。
     *
     * @param key 配置键
     */
    public String getValue(String key) {
        String value = options.get(key);
        return null != value ? value.trim() : null;
    }

    /**
     * 读取指定配置项的整数值。
     *
     * @param key 配置键
     * @return 配置值的整数形式
     * @throws NumberFormatException 当值非数字时抛出
     */
    public Integer getIntegerValue(String key) {
        return Integer.parseInt(options.get(key));
    }

    /**
     * 读取指定配置项并按分隔符切分为整型数组。
     *
     * @param key       配置键
     * @param separator 切分分隔符
     * @return 整型数组
     */
    public Integer[] getIntegerArrayValue(String key, String separator) {
        @NotNull String value = getValue(key);
        String[] array = value.split(separator);
        Integer[] ret = new Integer[array.length];
        for (int i = 0; i < array.length; i ++) {
            ret[i] = Integer.parseInt(array[i]);
        }
        return ret;
    }

    /* *
     * @param key 配置键
     */
    public boolean hasValue(String key) {
        return StringUtils.isNotBlank(options.get(key));
    }

    /**
     * 前台功能开关配置
     * <p>对应配置项 site.controls，控制注册、发文、评论等前台能力的开启。</p>
     */
    public static class Controls {
        /**
         * 是否开放用户注册，对应 site.controls.register
         */
        private boolean register;
        /**
         * 是否开放用户发文，对应 site.controls.post
         */
        private boolean post;
        /**
         * 是否开放评论，对应 site.controls.comment
         */
        private boolean comment;

        public boolean isRegister() {
            return register;
        }

        public void setRegister(boolean register) {
            this.register = register;
        }

        public boolean isPost() {
            return post;
        }

        public void setPost(boolean post) {
            this.post = post;
        }

        public boolean isComment() {
            return comment;
        }

        public void setComment(boolean comment) {
            this.comment = comment;
        }
    }

}
