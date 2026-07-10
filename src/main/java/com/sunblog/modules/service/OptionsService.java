// 包声明：系统配置服务接口所在包
package com.sunblog.modules.service;

// 导入配置项实体类，对应数据库 key-value 配置表
import com.sunblog.modules.entity.Options;
// 导入 Spring Resource 接口，用于加载初始化 SQL 脚本文件
import org.springframework.core.io.Resource;

// 导入 List 列表接口
import java.util.List;
// 导入 Map 映射接口，用于批量更新配置项
import java.util.Map;


/**
 * 系统配置 Service 接口
 *
 * <h3>配置项的存储方式</h3>
 * Options 表是一张 key-value 表（类似 WordPress 的 wp_options 表）。
 * - key：配置项名称（如 "site_name"、"storage_scheme"）
 * - value：配置项值
 *
 * 配置项通过 SiteOptions 类在内存中缓存，
 * 所有代码通过 SiteOptions.getValue(key) 读取。
 * OptionsService 负责实际的数据库读写。
 *
 * <h3>initSettings</h3>
 * 首次启动时执行 SQL 脚本插入默认配置，
 * 由 ContextStartup 在应用启动时调用。
 */
// OptionsService 接口：定义系统配置项的业务操作契约
public interface OptionsService {

	// findAll：查询所有配置项，返回完整列表
	List<Options> findAll();

	// update：批量更新配置项，参数为 key-value 映射，存在则更新，不存在则新增
	void update(Map<String, String> options);

	// initSettings：执行初始化 SQL 脚本，用于首次部署或重置站点配置
	void initSettings(Resource resource);
}