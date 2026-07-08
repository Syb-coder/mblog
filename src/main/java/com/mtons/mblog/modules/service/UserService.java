// 包声明：用户服务接口所在包
package com.mtons.mblog.modules.service;

// 导入常量定义类，包含缓存名称等常量
import com.mtons.mblog.base.lang.Consts;
// 导入账户资料 VO，登录/更新后返回的用户凭证信息
import com.mtons.mblog.modules.data.AccountProfile;
// 导入用户 VO（视图对象），包含用户基本信息
import com.mtons.mblog.modules.data.UserVO;
// 导入缓存配置注解，指定缓存的名称空间
import org.springframework.cache.annotation.CacheConfig;
// 导入缓存驱逐注解，写操作时清除指定缓存
import org.springframework.cache.annotation.CacheEvict;
// 导入缓存可读注解，查询时自动从缓存读取或写入缓存
import org.springframework.cache.annotation.Cacheable;
// 导入 Spring Data 分页结果封装类
import org.springframework.data.domain.Page;
// 导入 Spring Data 分页请求参数接口
import org.springframework.data.domain.Pageable;

// 导入 Map 映射接口
import java.util.Map;
// 导入 Set 集合接口，用于批量查询参数
import java.util.Set;

/**
 * 用户 Service 接口
 *
 * <h3>核心流程</h3>
 * 1. register()：创建用户 → 写入 User 表 → 写入 UserRole 赋予默认角色
 * 2. login()：查询用户 → 密码校验 → 返回 AccountProfile（登录凭证）
 * 3. get() → UserVO（@Cacheable 按 userId 缓存）
 * 4. update() / updateEmail() / updateAvatar() → @CacheEvict 清除缓存
 *
 * <h3>两个 updatePassword 重载</h3>
 * - updatePassword(id, newPassword)：管理员重置密码，不校验旧密码
 * - updatePassword(id, oldPassword, newPassword)：用户自行修改，需校验旧密码
 */
// @CacheConfig：配置该 Service 所有方法的默认缓存名称为 CACHE_USER
@CacheConfig(cacheNames = Consts.CACHE_USER)
// UserService 接口：定义用户的业务操作契约
public interface UserService {

	// paging：分页查询用户列表，支持按昵称模糊搜索
	Page<UserVO> paging(Pageable pageable, String name);

	// findMapByIds：根据 ID 集合批量查询用户，返回以用户 ID 为 key 的 Map
	Map<Long, UserVO> findMapByIds(Set<Long> ids);

	// login：用户登录，校验用户名密码后返回 AccountProfile 登录凭证
	AccountProfile login(String username, String password);

	// findProfile：根据用户 ID 查询账户资料（登录后的会话信息刷新用）
	AccountProfile findProfile(Long id);

	// register：用户注册，创建新用户并返回用户 VO
	UserVO register(UserVO user);

	// @CacheEvict(key = "#user.getId()")：更新后清除该用户的缓存
	// update：更新用户基本信息（昵称、签名），返回更新后的账户资料
	@CacheEvict(key = "#user.getId()")
	AccountProfile update(UserVO user);

	// @CacheEvict(key = "#id")：更新邮箱后清除该用户的缓存
	// updateEmail：更新用户邮箱地址
	@CacheEvict(key = "#id")
	AccountProfile updateEmail(long id, String email);

	// @Cacheable(key = "#userId")：以 userId 为缓存 key 自动缓存
	// get：根据用户 ID 查询用户详情（从缓存或数据库）
	@Cacheable(key = "#userId")
	UserVO get(long userId);

	// getByUsername：根据用户名查询用户
	UserVO getByUsername(String username);

	// getByEmail：根据邮箱查询用户
	UserVO getByEmail(String email);

	// @CacheEvict(key = "#id")：更新头像后清除该用户的缓存
	// updateAvatar：更新用户头像路径
	@CacheEvict(key = "#id")
	AccountProfile updateAvatar(long id, String path);

	// updatePassword（管理员版本）：直接重置密码，不校验旧密码
	void updatePassword(long id, String newPassword);

	// updatePassword（用户自行修改）：需校验旧密码正确性后才允许修改
	void updatePassword(long id, String oldPassword, String newPassword);

	// updateStatus：更新用户状态（启用/封禁）
	void updateStatus(long id, int status);

	// count：统计用户总数，用于仪表盘展示
	long count();
}