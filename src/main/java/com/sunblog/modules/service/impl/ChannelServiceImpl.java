// 包声明：频道服务实现类所在包
package com.sunblog.modules.service.impl;

// 导入常量定义类，包含状态常量和零值常量
import com.sunblog.base.lang.Consts;
// 导入文章实体类（未直接使用，但保留导入以防扩展）
import com.sunblog.modules.entity.Post;
// 导入频道仓储接口，提供频道表的 CRUD 操作
import com.sunblog.modules.repository.ChannelRepository;
// 导入频道服务接口，本类实现该接口
import com.sunblog.modules.service.ChannelService;
// 导入频道实体类，方法参数和返回值中使用
import com.sunblog.modules.entity.Channel;
// 导入 Spring Bean 属性拷贝工具，用于更新时复制属性
import org.springframework.beans.BeanUtils;
// 导入 Spring 自动注入注解
import org.springframework.beans.factory.annotation.Autowired;
// 导入 Spring Data 排序类，用于指定查询排序规则
import org.springframework.data.domain.Sort;
// 导入 Spring Service 注解，标识该类为业务服务组件
import org.springframework.stereotype.Service;
// 导入 Spring 事务注解，控制数据库事务行为
import org.springframework.transaction.annotation.Transactional;

// 导入 Java 工具类包，包含 Collection、Map、Optional 等
import java.util.*;
// 导入 Stream 收集器，用于 toMap 转换
import java.util.stream.Collectors;

/**
 * 栏目管理 Service 实现
 * <p>
 * 关键依赖：
 * <ul>
 *   <li>{@link ChannelRepository}：栏目 JPA 仓储</li>
 * </ul>
 * </p>
 * <p>
 * 事务策略：类级别 {@code @Transactional(readOnly = true)} 开启只读事务，写操作方法单独标注 {@link Transactional} 切换为可写事务。
 * </p>
 *
 */
// @Service 注解：将该类注册为 Spring 业务服务组件，由 Spring 容器管理
@Service
// @Transactional(readOnly = true)：类级别只读事务，所有查询方法默认只读，提升数据库性能
@Transactional(readOnly = true)
// ChannelServiceImpl：频道服务实现类，实现 ChannelService 接口定义的所有业务操作
public class ChannelServiceImpl implements ChannelService {
	// @Autowired：Spring 自动注入频道仓储实例
	@Autowired
	// channelRepository：频道 JPA 仓储，提供频道表的数据库操作
	private ChannelRepository channelRepository;

	/**
	 * <p>当 status > {@link Consts#IGNORE} 时按状态过滤；默认按 weight、id 倒序排序，保证置顶栏目在前。</p>
	 */
	// @Override：实现接口方法
	@Override
	// findAll：查询所有栏目，支持按状态过滤，按权重和 ID 倒序排序
	public List<Channel> findAll(int status) {
		// 构建排序规则：先按 weight 倒序（置顶在前），再按 id 倒序（新建在前）
		Sort sort = Sort.by(Sort.Direction.DESC, "weight", "id");
		// 声明返回结果列表
		List<Channel> list;
		// 当 status 大于忽略阈值时，按状态过滤查询
		if (status > Consts.IGNORE) {
			// 按状态和排序条件查询栏目列表
			list = channelRepository.findAllByStatus(status, sort);
		} else {
			// status 未指定时查询全部栏目
			list = channelRepository.findAll(sort);
		}
		// 返回查询结果
		return list;
	}

	// @Override：实现接口方法
	@Override
	// findMapByIds：根据 ID 集合批量查询栏目，返回以 ID 为 key 的 Map
	public Map<Integer, Channel> findMapByIds(Collection<Integer> ids) {
		// 根据 ID 集合查询栏目列表
		List<Channel> list = channelRepository.findAllById(ids);
		// 如果查询结果为空，返回空 Map 避免 NPE
		if (null == list) {
			return Collections.emptyMap();
		}
		// 将列表转换为以栏目 ID 为 key 的 Map，便于按 ID 快速查找
		return list.stream().collect(Collectors.toMap(Channel::getId, n -> n));
	}

	// @Override：实现接口方法
	@Override
	// getById：根据单个 ID 查询栏目详情
	public Channel getById(int id) {
		// 通过仓储按 ID 查询，get() 直接获取值（假设一定存在）
		return channelRepository.findById(id).get();
	}

	/**
	 * 新增/更新栏目
	 */
	// @Override：实现接口方法
	@Override
	// @Transactional：覆盖类级别只读事务，开启可写事务
	@Transactional
	// update：新增或更新栏目，如果 ID 对应的记录存在则更新，否则新增
	public void update(Channel channel) {
		// 根据 ID 查询已有栏目记录
		Optional<Channel> optional = channelRepository.findById(channel.getId());
		// 如果存在则使用已有记录（更新），否则创建新记录（新增）
		Channel po = optional.orElse(new Channel());
		// 将传入的栏目属性拷贝到持久化对象中
		BeanUtils.copyProperties(channel, po);
		// 更新最后修改时间
		po.setUpdated(new Date());
		// 保存到数据库（JPA 根据 ID 是否存在自动判断 insert/update）
		channelRepository.save(po);
	}

	/**
	 * 调整栏目权重（置顶/取消置顶）
	 */
	// @Override：实现接口方法
	@Override
	// @Transactional：覆盖类级别只读事务，开启可写事务
	@Transactional
	// updateWeight：调整栏目权重，置顶时设为最大权重+1，取消置顶时设为0
	public void updateWeight(int id, int weighted) {
		// 根据 ID 查询栏目记录
		Channel po = channelRepository.findById(id).get();

		// 初始化权重为 0（取消置顶的默认值）
		int max = Consts.ZERO;
		// 如果传入的 weighted 为置顶激活状态
		if (Consts.FEATURED_ACTIVE == weighted) {
			// 置顶场景：取现有最大权重 +1，保证当前栏目排序最靠前
			max = channelRepository.maxWeight() + 1;
		}
		// 设置新的权重值
		po.setWeight(max);
		// 更新最后修改时间
		po.setUpdated(new Date());
		// 保存更新后的栏目
		channelRepository.save(po);
	}

	// @Override：实现接口方法
	@Override
	// @Transactional：覆盖类级别只读事务，开启可写事务
	@Transactional
	// delete：根据 ID 删除栏目
	public void delete(int id) {
		// 调用仓储删除指定 ID 的栏目
		channelRepository.deleteById(id);
	}

	// @Override：实现接口方法
	@Override
	// count：统计栏目总数
	public long count() {
		// 调用仓储的 count 方法获取总数
		return channelRepository.count();
	}

}