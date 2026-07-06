package com.mtons.mblog.modules.service;

import com.mtons.mblog.modules.entity.Options;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;


/**
 * 系统配置项 Service
 * <p>
 * </p>
 *
 */
public interface OptionsService {
	/* *
	 * @return 配置项列表
	 */
	List<Options> findAll();

	/**
	 * 批量更新配置项
	 *
	 * @param options 配置项 key-value 映射
	 */
	void update(Map<String, String> options);

	/**
	 * 执行初始化 SQL 脚本，用于首次部署或重置站点配置
	 *
	 * @param resource SQL 脚本资源
	 */
	void initSettings(Resource resource);
}
