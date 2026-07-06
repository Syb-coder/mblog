package com.mtons.mblog.modules.service.impl;

import com.mtons.mblog.base.lang.Consts;
import com.mtons.mblog.modules.entity.Post;
import com.mtons.mblog.modules.repository.ChannelRepository;
import com.mtons.mblog.modules.service.ChannelService;
import com.mtons.mblog.modules.entity.Channel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
@Service
@Transactional(readOnly = true)
public class ChannelServiceImpl implements ChannelService {
	@Autowired
	private ChannelRepository channelRepository;

	/**
	 * <p>当 status > {@link Consts#IGNORE} 时按状态过滤；默认按 weight、id 倒序排序，保证置顶栏目在前。</p>
	 */
	@Override
	public List<Channel> findAll(int status) {
		Sort sort = Sort.by(Sort.Direction.DESC, "weight", "id");
		List<Channel> list;
		if (status > Consts.IGNORE) {
			list = channelRepository.findAllByStatus(status, sort);
		} else {
			list = channelRepository.findAll(sort);
		}
		return list;
	}

	@Override
	public Map<Integer, Channel> findMapByIds(Collection<Integer> ids) {
		List<Channel> list = channelRepository.findAllById(ids);
		if (null == list) {
			return Collections.emptyMap();
		}
		return list.stream().collect(Collectors.toMap(Channel::getId, n -> n));
	}

	@Override
	public Channel getById(int id) {
		return channelRepository.findById(id).get();
	}

	/**
	 * 新增/更新栏目
	 */
	@Override
	@Transactional
	public void update(Channel channel) {
		Optional<Channel> optional = channelRepository.findById(channel.getId());
		Channel po = optional.orElse(new Channel());
		BeanUtils.copyProperties(channel, po);
		channelRepository.save(po);
	}

	/**
	 * 调整栏目权重（置顶/取消置顶）
	 */
	@Override
	@Transactional
	public void updateWeight(int id, int weighted) {
		Channel po = channelRepository.findById(id).get();

		int max = Consts.ZERO;
		if (Consts.FEATURED_ACTIVE == weighted) {
			// 置顶场景：取现有最大权重 +1，保证当前栏目排序最靠前
			max = channelRepository.maxWeight() + 1;
		}
		po.setWeight(max);
		channelRepository.save(po);
	}

	@Override
	@Transactional
	public void delete(int id) {
		channelRepository.deleteById(id);
	}

	@Override
	public long count() {
		return channelRepository.count();
	}

}
