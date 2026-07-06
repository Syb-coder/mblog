package com.mtons.mblog.modules.service;

import com.mtons.mblog.base.lang.Consts;
import com.mtons.mblog.modules.data.AccountProfile;
import com.mtons.mblog.modules.data.UserVO;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.Set;

/**
 * 用户管理 Service
 * <p>
 * 负责用户的注册、登录、信息维护、密码/邮箱/头像修改及账户状态管理等业务。
 * </p>
 * <p>
 * </p>
 *
 */
@CacheConfig(cacheNames = Consts.CACHE_USER)
public interface UserService {
	/* *
	 * @param pageable 分页参数
	 * @param name     用户名，模糊匹配；为 null 时忽略
	 * @return 用户分页结果
	 */
	Page<UserVO> paging(Pageable pageable, String name);

	/* *
	 * @param ids 用户 ID 集合
	 * @return 以用户 id 为 key 的映射
	 */
	Map<Long, UserVO> findMapByIds(Set<Long> ids);

	/* *
	 * @param username 用户名
	 * @param password 密码（明文）
	 */
	AccountProfile login(String username, String password);

	/* *
	 * @param id 用户 ID
	 * @return 登录凭证 AccountProfile
	 */
	AccountProfile findProfile(Long id);

	/**
	 * 用户注册（用户名、邮箱唯一性校验）
	 *
	 * @param user 用户 VO
	 * @return 新用户 VO
	 */
	UserVO register(UserVO user);

	/* *
	 * @param user 用户 VO
	 * @return 更新后的登录凭证
	 */
	@CacheEvict(key = "#user.getId()")
	AccountProfile update(UserVO user);

	/* *
	 * @param id    用户 ID
	 * @param email 新邮箱
	 * @return 更新后的登录凭证
	 */
	@CacheEvict(key = "#id")
	AccountProfile updateEmail(long id, String email);

	/* *
	 * @param userId 用户 ID
	 */
	@Cacheable(key = "#userId")
	UserVO get(long userId);

	/* *
	 * @param username 用户名
	 * @return 用户 VO
	 */
	UserVO getByUsername(String username);

	/* *
	 * @param email 邮箱
	 * @return 用户 VO
	 */
	UserVO getByEmail(String email);

	/* *
	 * @param id   用户 ID
	 * @param path 头像路径
	 * @return 更新后的登录凭证
	 */
	@CacheEvict(key = "#id")
	AccountProfile updateAvatar(long id, String path);

	/**
	 * 修改密码（不校验旧密码，用于管理员重置场景）
	 *
	 * @param id          用户 ID
	 * @param newPassword 新密码（明文）
	 */
	void updatePassword(long id, String newPassword);

	/**
	 * 修改密码（校验旧密码）
	 *
	 * @param id          用户 ID
	 * @param oldPassword 旧密码（明文）
	 * @param newPassword 新密码（明文）
	 */
	void updatePassword(long id, String oldPassword, String newPassword);

	/**
	 * 更新用户状态（启用/封禁）
	 *
	 * @param id     用户 ID
	 * @param status 状态值，参考 {@code EntityStatus}
	 */
	void updateStatus(long id, int status);

	/**
	 * 统计用户总数
	 *
	 * @return 用户总数
	 */
	long count();

}
