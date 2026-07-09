// 包声明：系统配置服务实现类所在包
package com.mtons.mblog.modules.service.impl;

// 导入配置项实体类
import com.mtons.mblog.modules.entity.Options;
// 导入配置项仓储接口，提供配置表的 CRUD 操作
import com.mtons.mblog.modules.repository.OptionsRepository;
// 导入配置服务接口，本类实现该接口
import com.mtons.mblog.modules.service.OptionsService;
// 导入 Apache Commons 字符串工具类，用于 trim 操作
import org.apache.commons.lang3.StringUtils;
// 导入 Hibernate Session 接口，用于获取 JDBC Connection
import org.hibernate.Session;
// 导入 Spring Bean 属性拷贝工具
import org.springframework.beans.BeanUtils;
// 导入 Spring 自动注入注解
import org.springframework.beans.factory.annotation.Autowired;
// 导入 Spring Resource 接口，表示 SQL 脚本资源
import org.springframework.core.io.Resource;
// 导入 Spring SQL 脚本执行工具
import org.springframework.jdbc.datasource.init.ScriptUtils;
// 导入 Spring Service 注解
import org.springframework.stereotype.Service;
// 导入 Spring 事务注解
import org.springframework.transaction.annotation.Transactional;

// 导入 JPA 实体管理器，用于执行原生 SQL
import jakarta.persistence.EntityManager;
// 导入 ArrayList 列表类
import java.util.ArrayList;
// 导入 List 列表接口
import java.util.List;
// 导入 Map 映射接口
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
// @Service 注解：将该类注册为 Spring 业务服务组件
@Service
// OptionsServiceImpl：系统配置服务实现类
public class OptionsServiceImpl implements OptionsService {
	// @Autowired：Spring 自动注入配置项仓储实例
	@Autowired
	// optionsRepository：配置项 JPA 仓储，提供配置表的 CRUD 操作
	private OptionsRepository optionsRepository;
	// @Autowired：Spring 自动注入 JPA 实体管理器
	@Autowired
	// entityManager：JPA 实体管理器，用于获取 Hibernate Session 执行原生 SQL
	private EntityManager entityManager;

	// @Override：实现接口方法
	@Override
	// @Transactional(readOnly = true)：只读事务
	@Transactional(readOnly = true)
	// findAll：查询所有配置项，返回深拷贝后的列表
	public List<Options> findAll() {
		// 从数据库查询所有配置项
		List<Options> list = optionsRepository.findAll();
		// 创建返回结果列表
		List<Options> rets = new ArrayList<>();

		// 遍历查询结果，逐条深拷贝避免修改影响 JPA 托管对象
		for (Options po : list) {
			// 创建新的配置项对象
			Options r = new Options();
			// 将属性从 JPA 托管对象拷贝到新对象
			BeanUtils.copyProperties(po, r);
			// 加入返回列表
			rets.add(r);
		}
		// 返回深拷贝后的配置项列表
		return rets;
	}

	/**
	 * 批量更新配置项
	 * 整体处于一个事务内，任一记录失败则全部回滚。</p>
	 */
	// @Override：实现接口方法
	@Override
	// @Transactional：可写事务，批量更新需要事务保护
	@Transactional
	// update：批量更新配置项，存在则更新，不存在则新增
	public void update(Map<String, String> options) {
		// 如果传入的配置项映射为空，直接返回不做操作
		if (options == null) {
			return;
		}

		// 遍历每个配置项 key-value 对
		options.forEach((key, value) -> {
			// 根据 key 查询数据库中是否已有该配置项
			Options entity = optionsRepository.findByKey(key);
			// 对值进行 trim 去除首尾空白
			String val = StringUtils.trim(value);
			// 如果数据库中已存在该 key，则更新值
			if (entity != null) {
				// 更新已有配置项的值
				entity.setValue(val);
			} else {
				// 不存在则创建新的配置项实体
				entity = new Options();
				// 设置配置项 key
				entity.setKey(key);
				// 设置配置项 value
				entity.setValue(val);
			}
			// 更新最后修改时间
			entity.setUpdated(new java.util.Date());
			// 保存配置项（JPA 自动判断 insert/update）
			optionsRepository.save(entity);
		});
	}

	/**
	 * 执行初始化 SQL 脚本
	 * <p>通过 Hibernate Session 解包出 JDBC Connection，调用 Spring ScriptUtils 执行脚本；
	 * 用于首次部署或重置站点配置场景。</p>
	 */
	// @Override：实现接口方法
	@Override
	// @Transactional：可写事务，执行 SQL 脚本需要事务保护
	@Transactional
	// initSettings：执行初始化 SQL 脚本，用于首次部署或重置站点配置
	public void initSettings(Resource resource) {
		// 通过 EntityManager 解包获取 Hibernate Session
		Session session = entityManager.unwrap(Session.class);
		// 通过 Session 的 doWork 方法获取 JDBC Connection，执行 SQL 脚本
		session.doWork(connection -> ScriptUtils.executeSqlScript(connection, resource));
	}

}