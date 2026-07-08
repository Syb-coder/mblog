package com.mtons.mblog.modules.entity;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * 模块/内容分组 Entity
 * <p>
 * 职责：对应数据库表 mto_channel，描述博客内容的分组维度，
 * 例如文章分类频道。每个 Channel 通过唯一 key 标识，并支持
 * 排序、隐藏、缩略图等展示属性。
 * </p>
 *
 */
@Entity
@Table(name = "mto_channel")
public class Channel implements Serializable {
	private static final long serialVersionUID = 2436696690653745208L;

	/**
	 * 主键 ID，使用数据库自增策略生成
	 * @Id 标记为主键
	 * @GeneratedValue(strategy = GenerationType.IDENTITY) 表示由数据库自动生成（自增）
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	/**
	 * 分组名称，最大长度 32 字符
	 * 对应数据库表字段 name，如"技术"、"生活"等频道名称
	 */
	@Column(length = 32)
	private String name;

	/**
	 * 唯一关键字，用于 URL 与程序内引用，对应表字段 key_，最大长度 32
	 * @Column(name = "key_") 映射到数据库的 key_ 字段（下划线是保留字需特殊处理）
	 * unique = true 表示该字段值必须唯一，不可重复
	 * 例如：tech、life、qa 等，用于构建 URL 路径
	 */
	@Column(name = "key_", unique = true, length = 32)
	private String key;

	/**
	 * 预览图 URL，最大长度 128
	 * 频道的封面图片地址，展示在频道列表页面
	 */
	@Column(length = 128)
	private String thumbnail;

	/**
	 * 状态：0 表示显示，1 表示隐藏
	 * 用于控制频道是否在前台展示
	 */
	@Column(length = 5)
	private int status;

	/**
	 * 排序值，数值越大越靠前
	 * weight 值越大，在页面显示时排序越靠前
	 */
	private int weight;

	/**
	 * 获取主键 ID
	 * @return 主键 ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * 设置主键 ID
	 * @param id 主键 ID
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * 获取分组名称
	 * @return 分组名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置分组名称
	 * @param name 分组名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取唯一关键字
	 * @return 唯一关键字
	 */
	public String getKey() {
		return key;
	}

	/**
	 * 设置唯一关键字
	 * @param key 唯一关键字
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * 获取状态
	 * @return 状态值（0 显示，1 隐藏）
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * 设置状态
	 * @param status 状态值（0 显示，1 隐藏）
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * 获取预览图 URL
	 * @return 预览图 URL
	 */
	public String getThumbnail() {
		return thumbnail;
	}

	/**
	 * 设置预览图 URL
	 * @param thumbnail 预览图 URL
	 */
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	/**
	 * 获取排序值
	 * @return 排序值
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * 设置排序值
	 * @param weight 排序值
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}
}