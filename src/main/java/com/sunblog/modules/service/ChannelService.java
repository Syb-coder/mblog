// 包声明：频道/栏目服务接口所在包
package com.sunblog.modules.service;

// 导入频道实体类，方法返回值和参数中使用
import com.sunblog.modules.entity.Channel;

// 导入 Collection 集合接口，用于批量查询参数
import java.util.Collection;
// 导入 List 列表接口，用于返回有序的频道集合
import java.util.List;
// 导入 Map 映射接口，用于以 ID 为 key 快速查找频道
import java.util.Map;

/**
 * 栏目/频道 Service 接口
 *
 * <h3>栏目是什么？</h3>
 * 栏目 = 文章的分类，类似于 WordPress 的分类目录。
 * 每篇文章属于一个栏目（Channel.posts 是多对一关系）。
 * 前台的导航菜单就是栏目列表。
 *
 * <h3>weight 字段的作用</h3>
 * 栏目可以设置 weight（权重），权重高的排在前面。
 * updateWeight() 方法用于置顶/取消置顶操作。
 */
// ChannelService 接口：定义栏目/频道的业务操作契约
public interface ChannelService {

	// findAll：根据状态查询所有栏目，status > 0 时按状态过滤，否则查询全部
	List<Channel> findAll(int status);

	// findMapByIds：根据 ID 集合批量查询栏目，返回以 ID 为 key 的 Map，便于快速定位
	Map<Integer, Channel> findMapByIds(Collection<Integer> ids);

	// getById：根据单个 ID 查询栏目详情
	Channel getById(int id);

	// update：新增或更新栏目信息
	void update(Channel channel);

	// updateWeight：调整栏目权重，id 为栏目 ID，weighted 为权重值（用于置顶/取消置顶）
	void updateWeight(int id, int weighted);

	// delete：根据 ID 删除栏目
	void delete(int id);

	// count：统计栏目总数，用于仪表盘展示
	long count();
}