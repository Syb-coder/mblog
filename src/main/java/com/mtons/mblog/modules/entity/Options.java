package com.mtons.mblog.modules.entity;

import jakarta.persistence.*;

/**
 * 系统配置实体 —— 对应数据库表 mto_options
 *
 * <h3>业务含义</h3>
 * 这是一个通用的键值对存储表（类似 Properties 文件但存在数据库里）。
 * 用于存储站点的各种可配置项，例如：
 * - 站点名称/描述/关键字（SEO）
 * - 文件存储配置（本地路径/OSS 等）
 * - 注册/评论/发文等功能的开关
 *
 * <h3>工作机制</h3>
 * 系统启动时，ContextStartup 调用 OptionsServiceImpl.initSettings()
 * 读取本表所有记录，填充到 SiteOptions.options 这个 Map 中。
 * 后台管理页面（admin/OptionsController）可以修改这些配置，
 * 修改后通过刷新缓存立即生效。
 *
 * <h3>数据库表结构</h3>
 * <pre>
 * mto_options
 * ├── id    BIGINT    PK, 自增
 * ├── type  INT                        ← 类型（冗余预留）
 * ├── key_  VARCHAR(32)  UNIQUE        ← 配置键，如 "site_name"
 * └── value VARCHAR(300)               ← 配置值
 * </pre>
 */
@Entity
@Table(name = "mto_options")
public class Options {
	/**
	 * 主键 ID，使用数据库自增策略生成
	 * @Id 标记为主键
	 * @GeneratedValue(strategy = GenerationType.IDENTITY) 表示由数据库自动生成（自增）
	 * 每条配置记录的唯一标识，无实际业务含义
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	/**
	 * 类型（当前为冗余预留字段，暂未使用）
	 * 扩展用途：后续可用来区分不同类别的配置（如 0=系统配置、1=主题配置、2=邮件配置等）
	 */
	@Column(length = 5)
	private int type;

	/**
	 * 配置键（唯一标识）
	 * @Column(name = "key_") 映射到数据库的 key_ 字段（下划线是保留字需特殊处理）
	 * unique = true 表示该字段值必须唯一，不可重复
	 * 类似 Properties 文件中的 key，例如：
	 *   site_name        — 站点名称
	 *   site_keywords    — 站点关键字（SEO）
	 *   site_description — 站点描述（SEO）
	 *   storage_type     — 文件存储方式（native/oss等）
	 */
	@Column(name = "key_", unique = true, length = 32)
	private String key;

	/**
	 * 配置值
	 * 与 key 对应的值，存储实际的配置内容
	 * 最长 300 个字符，例如 key 为 site_name 时，value 就是 "MBlog"
	 */
	@Column(length = 300)
	private String value;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}