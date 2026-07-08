package com.mtons.mblog.modules.repository;

import com.mtons.mblog.modules.entity.Channel;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

/**
 * 栏目/频道数据访问层
 *
 * <h3>功能</h3>
 * - findAllByStatus：按状态查询栏目列表（前台只查 status=0 的启用栏目）
 * - maxWeight：获取所有栏目的最大排序权重，用于新建栏目时自动分配 weight
 *
 * <h3>为什么主键是 Integer 不是 Long？</h3>
 * 栏目数量通常很少（几个到十几个），Integer 足够。
 * 其他 entity 如 Post、User 使用 Long 是因为可能有很多条记录。
 */
public interface ChannelRepository extends JpaRepository<Channel, Integer>, JpaSpecificationExecutor<Channel> {

	/**
	 * 按状态查询频道列表，支持排序
	 *
	 * Spring Data JPA 命名策略：方法名自动解析为 SQL
	 *   findAllByStatus → WHERE status = ?
	 *
	 * 使用场景：
	 *   findAllByStatus(0, sort) → 前台查询所有启用的频道（status=0 显示）
	 *   findAllByStatus(1, sort) → 后台查询所有隐藏的频道（status=1 隐藏）
	 *
	 * @param status 频道状态（0=显示，1=隐藏）
	 * @param sort   排序条件，如 Sort.by(Sort.Direction.DESC, "weight")
	 * @return 符合条件的频道列表
	 */
	List<Channel> findAllByStatus(int status, Sort sort);

	/**
	 * 查询所有频道的最大排序权重值
	 *
	 * JPQL 说明：
	 *   @Query 中写的是 JPQL 而非 SQL：
	 *     - "from Channel" 中的 Channel 是实体类名（不是表名 mto_channel）
	 *     - "weight" 是实体属性名（不是数据库字段名）
	 *
	 *   coalesce(max(weight), 0) 的含义：
	 *     - max(weight)       → 取所有频道中最大的 weight 值
	 *     - coalesce(..., 0)  → 如果结果为 null（表中无数据），则返回 0
	 *
	 * 等效 SQL：SELECT COALESCE(MAX(weight), 0) FROM mto_channel
	 *
	 * 使用场景：新建频道时，自动分配一个比当前最大 weight 大 1 的值，保证新频道排在最后
	 *
	 * @return 最大 weight 值，表中无数据时返回 0
	 */
	@Query("select coalesce(max(weight), 0) from Channel")
	int maxWeight();
}