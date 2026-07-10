package com.sunblog.base.lang;

/**
 * 实体状态常量接口
 * <p>
 * 统一定义业务实体的启用/禁用状态值，供持久化层与业务层共享，
 * 避免散落的硬编码状态值。
 * </p> *
 */
public interface EntityStatus {
	/** 启用状态：实体处于可用状态 */
	int ENABLED = 0; // 启动
	/** 禁用状态：实体被管理员或系统策略关闭 */
	int DISABLED = 1; // 禁用
}
