package com.mtons.mblog.modules.service.impl;

import com.mtons.mblog.modules.entity.Options;
import com.mtons.mblog.modules.repository.OptionsRepository;
import com.mtons.mblog.modules.service.OptionsService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 系统配置项 Service 实现
 * <p>
 * 关键依赖：
 * <ul>
 *   <li>{@link OptionsRepository}：配置项 JPA 仓储</li>
 *   <li>{@link EntityManager}：用于执行原生 SQL 脚本（初始化场景）</li>
 * </ul>
 * </p>
 *
 */
@Service
public class OptionsServiceImpl implements OptionsService {
	@Autowired
	private OptionsRepository optionsRepository;
	@Autowired
	private EntityManager entityManager;

	@Override
	@Transactional(readOnly = true)
	public List<Options> findAll() {
		List<Options> list = optionsRepository.findAll();
		List<Options> rets = new ArrayList<>();

		for (Options po : list) {
			Options r = new Options();
			BeanUtils.copyProperties(po, r);
			rets.add(r);
		}
		return rets;
	}

	/**
	 * 批量更新配置项
	 * 整体处于一个事务内，任一记录失败则全部回滚。</p>
	 */
	@Override
	@Transactional
	public void update(Map<String, String> options) {
		if (options == null) {
			return;
		}

		options.forEach((key, value) -> {
			Options entity = optionsRepository.findByKey(key);
			String val = StringUtils.trim(value);
			if (entity != null) {
				entity.setValue(val);
			} else {
				entity = new Options();
				entity.setKey(key);
				entity.setValue(val);
			}
			optionsRepository.save(entity);
		});
	}

	/**
	 * 执行初始化 SQL 脚本
	 * <p>通过 Hibernate Session 解包出 JDBC Connection，调用 Spring ScriptUtils 执行脚本；
	 * 用于首次部署或重置站点配置场景。</p>
	 */
	@Override
	@Transactional
	public void initSettings(Resource resource) {
		Session session = entityManager.unwrap(Session.class);
		session.doWork(connection -> ScriptUtils.executeSqlScript(connection, resource));
	}

}
