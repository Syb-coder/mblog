package com.mtons.mblog.modules.service;

import com.mtons.mblog.modules.entity.Channel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 栏目（频道）管理 Service
 * <p>
 * </p>
 *
 */
public interface ChannelService {
	/**
	 * <p>当 status 大于忽略阈值时按状态过滤，否则返回全部栏目；默认按 weight、id 倒序排序。</p>
	 *
	 * @param status 栏目状态过滤标识，详见 {@code Consts.IGNORE}
	 * @return 栏目列表
	 */
	List<Channel> findAll(int status);

	/* *
	 * @param ids 栏目 ID 集合
	 * @return 以 id 为 key 的栏目映射，集合为空时返回空 Map
	 */
	Map<Integer, Channel> findMapByIds(Collection<Integer> ids);

	/* *
	 * @param id 栏目 ID
	 */
	Channel getById(int id);

	/* *
	 * @param channel 栏目对象
	 */
	void update(Channel channel);

	/**
	 * 调整栏目权重（用于置顶/取消置顶场景）
	 *
	 * @param id       栏目 ID
	 * @param weighted 权重操作标识，参考 {@code Consts.FEATURED_ACTIVE}
	 */
	void updateWeight(int id, int weighted);

	/**
	 * 删除栏目
	 *
	 * @param id 栏目 ID
	 */
	void delete(int id);

	/**
	 * 统计栏目总数
	 *
	 * @return 栏目总数
	 */
	long count();
}
