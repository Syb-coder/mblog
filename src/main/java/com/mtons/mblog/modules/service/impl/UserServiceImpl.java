// 包声明：用户服务实现类所在包
package com.mtons.mblog.modules.service.impl;

// 导入实体状态常量类（ENABLED、DISABLED 等）
import com.mtons.mblog.base.lang.EntityStatus;
// 导入业务异常类
import com.mtons.mblog.base.lang.MtonsException;
// 导入 MD5 散列工具类
import com.mtons.mblog.base.utils.MD5;
// 导入账户概要 VO（用于登录会话）
import com.mtons.mblog.modules.data.AccountProfile;
// 导入用户 VO
import com.mtons.mblog.modules.data.UserVO;
// 导入用户实体类
import com.mtons.mblog.modules.entity.User;
// 导入角色仓储接口
import com.mtons.mblog.modules.repository.RoleRepository;
// 导入用户仓储接口
import com.mtons.mblog.modules.repository.UserRepository;
// 导入用户服务接口，本类实现该接口
import com.mtons.mblog.modules.service.UserService;
// 导入 Bean 属性拷贝工具
import com.mtons.mblog.base.utils.BeanMapUtils;
// 导入 Apache Commons 字符串工具类
import org.apache.commons.lang3.StringUtils;
// 导入 Spring Bean 属性拷贝工具
import org.springframework.beans.BeanUtils;
// 导入 Spring 自动注入注解
import org.springframework.beans.factory.annotation.Autowired;
// 导入 Spring Data 分页相关类
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
// 导入 Spring Service 注解
import org.springframework.stereotype.Service;
// 导入 Spring 事务注解
import org.springframework.transaction.annotation.Transactional;
// 导入 Spring 断言工具
import org.springframework.util.Assert;

// 导入 JPA Criteria 断言接口
import jakarta.persistence.criteria.Predicate;
// 导入 Java 工具类包
import java.util.*;

/**
 * 用户管理 Service 实现
 *
 * <h3>职责</h3>
 * 提供用户注册、登录、信息更新、密码管理等功能。
 *
 * <h3>密码策略</h3>
 * 密码使用 MD5 单向散列后存储（不存明文），每次校验时对用户输入的密码
 * 做同样的 MD5 后与数据库比对。注意：MD5 本身强度有限，生产环境建议升级为
 * bcrypt 或 Argon2，但作为个人博客系统，MD5 基本够用。
 *
 * <h3>登录流程</h3>
 * <pre>
 * LoginController → UserService.login(username, password)
 *   1) userRepository.findByUsername(username)  → 查用户
 *   2) password 明文与数据库 MD5 密文比对
 *   3) 更新 lastLogin 时间
 *   4) 返回 AccountProfile（会话信息，不含敏感字段）
 * </pre>
 *
 * <h3>注册流程</h3>
 * <pre>
 * RegisterController → UserService.register(userVO)
 *   1) 校验用户名、密码非空
 *   2) 校验用户名唯一性
 *   3) 校验邮箱唯一性（如果填写了邮箱）
 *   4) 密码 MD5 加密
 *   5) 保存用户，status = ENABLED(0)
 * </pre>
 */
// @Service 注解：将该类注册为 Spring 业务服务组件
@Service
// @Transactional(readOnly = true)：类级别只读事务
@Transactional(readOnly = true)
// UserServiceImpl：用户管理服务实现类
public class UserServiceImpl implements UserService {
    // @Autowired：Spring 自动注入用户仓储实例
    @Autowired
    // userRepository：用户 JPA 仓储，提供用户表的 CRUD 操作
    private UserRepository userRepository;

    // @Autowired：Spring 自动注入角色仓储实例
    @Autowired
    // roleRepository：角色 JPA 仓储（本类中未直接使用，保留以防扩展）
    private RoleRepository roleRepository;

    /**
     * <p>使用 JPA Criteria 动态拼接 name 模糊匹配条件。</p>
     */
    // @Override：实现接口方法
    @Override
    // paging：分页查询用户，支持按名称模糊搜索
    public Page<UserVO> paging(Pageable pageable, String name) {
        // 使用 JPA Criteria 动态构建查询条件
        Page<User> page = userRepository.findAll((root, query, builder) -> {
            // 创建空 conjunction 条件
            Predicate predicate = builder.conjunction();

            // 如果 name 参数非空，添加模糊匹配条件
            if (StringUtils.isNoneBlank(name)) {
                // 添加 LIKE 模糊查询条件
                predicate.getExpressions().add(
                        builder.like(root.get("name"), "%" + name + "%"));
            }

            // 按 id 倒序排序
            query.orderBy(builder.desc(root.get("id")));
            // 返回查询条件
            return predicate;
        }, pageable);

        // 将实体列表转为 VO 列表
        List<UserVO> rets = new ArrayList<>();
        // 遍历实体列表，逐个拷贝为 VO
        page.getContent().forEach(n -> rets.add(BeanMapUtils.copy(n)));
        // 返回 VO 分页结果
        return new PageImpl<>(rets, pageable, page.getTotalElements());
    }

    // @Override：实现接口方法
    @Override
    // findMapByIds：根据 ID 集合批量查询用户，返回以用户 ID 为 key 的 Map
    public Map<Long, UserVO> findMapByIds(Set<Long> ids) {
        // 如果 ID 集合为空或 null，直接返回空 Map
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyMap();
        }
        // 根据 ID 集合查询用户列表
        List<User> list = userRepository.findAllById(ids);
        // 创建返回结果 Map
        Map<Long, UserVO> ret = new HashMap<>();

        // 遍历用户列表，将实体拷贝为 VO 并存入 Map
        list.forEach(po -> ret.put(po.getId(), BeanMapUtils.copy(po)));
        // 返回用户映射
        return ret;
    }

    /**
     * 用户登录
     * <p>校验流程：用户名存在性 → 密码匹配 → 更新最后登录时间 → 返回会话信息。</p>
     */
    // @Override：实现接口方法
    @Override
    // @Transactional：可写事务（需更新 lastLogin 时间）
    @Transactional
    // login：用户登录，返回账户概要信息
    public AccountProfile login(String username, String password) {
        // 根据用户名查询用户记录
        User po = userRepository.findByUsername(username);

        // 如果用户不存在，返回 null（由上层 Controller 判断并提示）
        if (null == po) {
            return null;
        }

//		Assert.state(po.getStatus() != Const.STATUS_CLOSED, "您的账户已被封禁");
        // 校验密码是否匹配（传入的 password 应为 MD5 散列后的值）
        Assert.state(StringUtils.equals(po.getPassword(), password), "密码错误");

        // 更新最后登录时间
        po.setLastLogin(Calendar.getInstance().getTime());
        // 保存更新后的用户记录
        userRepository.save(po);
        // 将用户实体转为账户概要 VO（不含密码等敏感字段）
        AccountProfile u = BeanMapUtils.copyPassport(po);

        // 返回账户概要信息
        return u;
    }

    // @Override：实现接口方法
    @Override
    // @Transactional：可写事务
    @Transactional
    // findProfile：根据用户 ID 查找账户概要信息（用于已登录用户的会话刷新）
    public AccountProfile findProfile(Long id) {
        // 根据 ID 查询用户记录
        User po = userRepository.findById(id).orElse(null);

        // 断言用户存在
        Assert.notNull(po, "账户不存在");

//		Assert.state(po.getStatus() != Const.STATUS_CLOSED, "您的账户已被封禁");
        // 更新最后登录时间
        po.setLastLogin(Calendar.getInstance().getTime());

        // 将用户实体转为账户概要 VO
        AccountProfile u = BeanMapUtils.copyPassport(po);

        // 返回账户概要信息
        return u;
    }

    /**
     * 用户注册
     * <p>校验流程：参数非空 -> 用户名唯一性 -> 邮箱唯一性（如有）；
     * 密码 MD5 散列后存储，初始状态为 ENABLED。</p>
     */
    // @Override：实现接口方法
    @Override
    // @Transactional：可写事务
    @Transactional
    // register：用户注册，返回注册后的用户 VO
    public UserVO register(UserVO user) {
        // 断言用户参数不为 null
        Assert.notNull(user, "Parameter user can not be null!");

        // 断言用户名非空
        Assert.hasLength(user.getUsername(), "用户名不能为空");
        // 断言密码非空
        Assert.hasLength(user.getPassword(), "密码不能为空!");

        // 查询用户名是否已存在
        User check = userRepository.findByUsername(user.getUsername());

        // 断言用户名未被占用
        Assert.isNull(check, "用户名已经存在");

        // 如果填写了邮箱，校验邮箱唯一性
        if (StringUtils.isNotBlank(user.getEmail())) {
            // 查询邮箱是否已被注册
            User emailCheck = userRepository.findByEmail(user.getEmail());
            // 断言邮箱未被占用
            Assert.isNull(emailCheck, "邮箱已经存在!");
        }

        // 创建新的用户实体
        User po = new User();

        // 将 VO 属性拷贝到实体中
        BeanUtils.copyProperties(user, po);

        // 如果昵称为空，默认使用用户名作为昵称
        if (StringUtils.isBlank(po.getName())) {
            po.setName(user.getUsername());
        }

        // 获取当前时间
        Date now = Calendar.getInstance().getTime();
        // 密码 MD5 散列后存储，不存明文
        po.setPassword(MD5.md5(user.getPassword()));
        // 设置初始状态为启用
        po.setStatus(EntityStatus.ENABLED);
        // 设置创建时间
        po.setCreated(now);

        // 保存用户到数据库
        userRepository.save(po);

        // 返回注册后的用户 VO
        return BeanMapUtils.copy(po);
    }

    /**
     * 更新用户基本信息（昵称、签名）
     */
    // @Override：实现接口方法
    @Override
    // @Transactional：可写事务
    @Transactional
    // update：更新用户基本信息（昵称、个性签名）
    public AccountProfile update(UserVO user) {
        // 根据 ID 查询已有用户记录
        User po = userRepository.findById(user.getId()).get();
        // 更新昵称
        po.setName(user.getName());
        // 更新个性签名
        po.setSignature(user.getSignature());
        // 保存更新
        userRepository.save(po);
        // 返回更新后的账户概要
        return BeanMapUtils.copyPassport(po);
    }

    /**
     * 更新用户邮箱
     * <p>校验流程：
     * <ul>
     *   <li>新旧邮箱相同则抛出业务异常</li>
     *   <li>新邮箱已被其他用户占用则抛出业务异常</li>
     * </ul>
     * </p>
     */
    // @Override：实现接口方法
    @Override
    // @Transactional：可写事务
    @Transactional
    // updateEmail：更新用户邮箱地址
    public AccountProfile updateEmail(long id, String email) {
        // 根据 ID 查询用户记录
        User po = userRepository.findById(id).get();

        // 如果新邮箱与旧邮箱相同
        if (email.equals(po.getEmail())) {
            // 抛出业务异常，提示邮箱未更改
            throw new MtonsException("邮箱地址没做更改");
        }

        // 查询新邮箱是否已被其他用户使用
        User check = userRepository.findByEmail(email);

        // 如果新邮箱已被占用且不是当前用户
        if (check != null && check.getId() != po.getId()) {
            // 抛出业务异常，提示邮箱已被使用
            throw new MtonsException("该邮箱地址已经被使用了");
        }
        // 更新邮箱
        po.setEmail(email);
        // 保存更新
        userRepository.save(po);
        // 返回更新后的账户概要
        return BeanMapUtils.copyPassport(po);
    }

    // @Override：实现接口方法
    @Override
    // get：根据用户 ID 查询用户 VO
    public UserVO get(long userId) {
        // 根据 ID 查询用户记录
        Optional<User> optional = userRepository.findById(userId);
        // 如果存在则拷贝为 VO 返回
        if (optional.isPresent()) {
            return BeanMapUtils.copy(optional.get());
        }
        // 不存在则返回 null
        return null;
    }

    // @Override：实现接口方法
    @Override
    // getByUsername：根据用户名查询用户 VO
    public UserVO getByUsername(String username) {
        // 通过仓储查询并拷贝为 VO
        return BeanMapUtils.copy(userRepository.findByUsername(username));
    }

    // @Override：实现接口方法
    @Override
    // getByEmail：根据邮箱查询用户 VO
    public UserVO getByEmail(String email) {
        // 通过仓储查询并拷贝为 VO
        return BeanMapUtils.copy(userRepository.findByEmail(email));
    }

    /**
     * 更新用户头像
     */
    // @Override：实现接口方法
    @Override
    // @Transactional：可写事务
    @Transactional
    // updateAvatar：更新用户头像路径
    public AccountProfile updateAvatar(long id, String path) {
        // 根据 ID 查询用户记录
        User po = userRepository.findById(id).get();
        // 更新头像路径
        po.setAvatar(path);
        // 保存更新
        userRepository.save(po);
        // 返回更新后的账户概要
        return BeanMapUtils.copyPassport(po);
    }

    /**
     * 修改密码（不校验旧密码）
     * <p>适用场景：管理员重置密码、忘记密码后通过邮箱验证重置。</p>
     */
    // @Override：实现接口方法
    @Override
    // @Transactional：可写事务
    @Transactional
    // updatePassword：修改密码（不校验旧密码，适用于管理员重置或忘记密码场景）
    public void updatePassword(long id, String newPassword) {
        // 根据 ID 查询用户记录
        User po = userRepository.findById(id).get();

        // 断言新密码非空
        Assert.hasLength(newPassword, "密码不能为空!");

        // 新密码 MD5 散列后存储
        po.setPassword(MD5.md5(newPassword));
        // 保存更新
        userRepository.save(po);
    }

    /**
     * 修改密码（校验旧密码）
     * <p>校验流程：旧密码 MD5 散列后与库内密码比对，比对通过才允许更新。</p>
     */
    // @Override：实现接口方法
    @Override
    // @Transactional：可写事务
    @Transactional
    // updatePassword：修改密码（需校验旧密码，适用于用户主动修改密码场景）
    public void updatePassword(long id, String oldPassword, String newPassword) {
        // 根据 ID 查询用户记录
        User po = userRepository.findById(id).get();

        // 断言新密码非空
        Assert.hasLength(newPassword, "密码不能为空!");

        // 校验旧密码：MD5 散列后与库内密码比对
        Assert.isTrue(MD5.md5(oldPassword).equals(po.getPassword()), "当前密码不正确");
        // 新密码 MD5 散列后存储
        po.setPassword(MD5.md5(newPassword));
        // 保存更新
        userRepository.save(po);
    }

    /**
     * 更新用户状态（启用/封禁）
     */
    // @Override：实现接口方法
    @Override
    // @Transactional：可写事务
    @Transactional
    // updateStatus：更新用户状态（启用/封禁）
    public void updateStatus(long id, int status) {
        // 根据 ID 查询用户记录
        User po = userRepository.findById(id).get();

        // 设置新状态（ENABLED=0 正常，DISABLED=1 封禁）
        po.setStatus(status);
        // 保存更新
        userRepository.save(po);
    }

    // @Override：实现接口方法
    @Override
    // count：统计用户总数
    public long count() {
        // 调用仓储的 count 方法获取总数
        return userRepository.count();
    }

}