package com.mtons.mblog.modules.repository;

import com.mtons.mblog.modules.entity.Options;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 系统配置项（Options）数据访问层
 * <p>
 * 对应 Entity：{@link Options}，主键类型 Long。
 * </p>
 *
 */
public interface OptionsRepository extends JpaRepository<Options, Long>, JpaSpecificationExecutor<Options> {

	/**
	 * @param key 配置项键名
	 * @return 配置项记录，未命中返回 null
	 */
	Options findByKey(String key);
}