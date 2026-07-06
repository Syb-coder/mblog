package com.mtons.mblog.modules.service.impl;

import com.mtons.mblog.base.lang.EntityStatus;
import com.mtons.mblog.base.lang.MtonsException;
import com.mtons.mblog.base.utils.MD5;
import com.mtons.mblog.modules.data.AccountProfile;
import com.mtons.mblog.modules.data.UserVO;
import com.mtons.mblog.modules.entity.User;
import com.mtons.mblog.modules.repository.RoleRepository;
import com.mtons.mblog.modules.repository.UserRepository;
import com.mtons.mblog.modules.service.UserService;
import com.mtons.mblog.base.utils.BeanMapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.persistence.criteria.Predicate;
import java.util.*;

/**
 * 用户管理 Service 实现
 * <p>
 * 关键依赖：
 * <ul>
 *   <li>{@link UserRepository}：用户 JPA 仓储</li>
 *   <li>{@link RoleRepository}：角色仓储</li>
 * </ul>
 * </p>
 * <p>
 * 事务策略：类级别只读事务；写操作方法切换为可写事务。
 * </p>
 * <p>
 * </p>
 *
 */
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    /**
     * <p>使用 JPA Criteria 动态拼接 name 模糊匹配条件。</p>
     */
    @Override
    public Page<UserVO> paging(Pageable pageable, String name) {
        Page<User> page = userRepository.findAll((root, query, builder) -> {
            Predicate predicate = builder.conjunction();

            if (StringUtils.isNoneBlank(name)) {
                predicate.getExpressions().add(
                        builder.like(root.get("name"), "%" + name + "%"));
            }

            query.orderBy(builder.desc(root.get("id")));
            return predicate;
        }, pageable);

        List<UserVO> rets = new ArrayList<>();
        page.getContent().forEach(n -> rets.add(BeanMapUtils.copy(n)));
        return new PageImpl<>(rets, pageable, page.getTotalElements());
    }

    @Override
    public Map<Long, UserVO> findMapByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyMap();
        }
        List<User> list = userRepository.findAllById(ids);
        Map<Long, UserVO> ret = new HashMap<>();

        list.forEach(po -> ret.put(po.getId(), BeanMapUtils.copy(po)));
        return ret;
    }

    /**
     * 用户登录
     */
    @Override
    @Transactional
    public AccountProfile login(String username, String password) {
        User po = userRepository.findByUsername(username);

        if (null == po) {
            return null;
        }

//		Assert.state(po.getStatus() != Const.STATUS_CLOSED, "您的账户已被封禁");

        Assert.state(StringUtils.equals(po.getPassword(), password), "密码错误");

        po.setLastLogin(Calendar.getInstance().getTime());
        userRepository.save(po);
        AccountProfile u = BeanMapUtils.copyPassport(po);

        return u;
    }

    @Override
    @Transactional
    public AccountProfile findProfile(Long id) {
        User po = userRepository.findById(id).orElse(null);

        Assert.notNull(po, "账户不存在");

//		Assert.state(po.getStatus() != Const.STATUS_CLOSED, "您的账户已被封禁");
        po.setLastLogin(Calendar.getInstance().getTime());

        AccountProfile u = BeanMapUtils.copyPassport(po);

        return u;
    }

    /**
     * 用户注册
     * <p>校验流程：参数非空 -> 用户名唯一性 -> 邮箱唯一性（如有）；
     */
    @Override
    @Transactional
    public UserVO register(UserVO user) {
        Assert.notNull(user, "Parameter user can not be null!");

        Assert.hasLength(user.getUsername(), "用户名不能为空");
        Assert.hasLength(user.getPassword(), "密码不能为空!");

        User check = userRepository.findByUsername(user.getUsername());

        Assert.isNull(check, "用户名已经存在");

        if (StringUtils.isNotBlank(user.getEmail())) {
            User emailCheck = userRepository.findByEmail(user.getEmail());
            Assert.isNull(emailCheck, "邮箱已经存在!");
        }

        User po = new User();

        BeanUtils.copyProperties(user, po);

        if (StringUtils.isBlank(po.getName())) {
            po.setName(user.getUsername());
        }

        Date now = Calendar.getInstance().getTime();
        po.setPassword(MD5.md5(user.getPassword()));
        po.setStatus(EntityStatus.ENABLED);
        po.setCreated(now);

        userRepository.save(po);

        return BeanMapUtils.copy(po);
    }

    /**
     * 更新用户基本信息（昵称、签名）
     */
    @Override
    @Transactional
    public AccountProfile update(UserVO user) {
        User po = userRepository.findById(user.getId()).get();
        po.setName(user.getName());
        po.setSignature(user.getSignature());
        userRepository.save(po);
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
    @Override
    @Transactional
    public AccountProfile updateEmail(long id, String email) {
        User po = userRepository.findById(id).get();

        if (email.equals(po.getEmail())) {
            throw new MtonsException("邮箱地址没做更改");
        }

        User check = userRepository.findByEmail(email);

        if (check != null && check.getId() != po.getId()) {
            throw new MtonsException("该邮箱地址已经被使用了");
        }
        po.setEmail(email);
        userRepository.save(po);
        return BeanMapUtils.copyPassport(po);
    }

    @Override
    public UserVO get(long userId) {
        Optional<User> optional = userRepository.findById(userId);
        if (optional.isPresent()) {
            return BeanMapUtils.copy(optional.get());
        }
        return null;
    }

    @Override
    public UserVO getByUsername(String username) {
        return BeanMapUtils.copy(userRepository.findByUsername(username));
    }

    @Override
    public UserVO getByEmail(String email) {
        return BeanMapUtils.copy(userRepository.findByEmail(email));
    }

    /**
     * 更新用户头像
     */
    @Override
    @Transactional
    public AccountProfile updateAvatar(long id, String path) {
        User po = userRepository.findById(id).get();
        po.setAvatar(path);
        userRepository.save(po);
        return BeanMapUtils.copyPassport(po);
    }

    /**
     * 修改密码（不校验旧密码）
     */
    @Override
    @Transactional
    public void updatePassword(long id, String newPassword) {
        User po = userRepository.findById(id).get();

        Assert.hasLength(newPassword, "密码不能为空!");

        po.setPassword(MD5.md5(newPassword));
        userRepository.save(po);
    }

    /**
     * 修改密码（校验旧密码）
     * <p>校验流程：旧密码 MD5 散列后与库内密码比对，比对通过才允许更新。</p>
     */
    @Override
    @Transactional
    public void updatePassword(long id, String oldPassword, String newPassword) {
        User po = userRepository.findById(id).get();

        Assert.hasLength(newPassword, "密码不能为空!");

        Assert.isTrue(MD5.md5(oldPassword).equals(po.getPassword()), "当前密码不正确");
        po.setPassword(MD5.md5(newPassword));
        userRepository.save(po);
    }

    /**
     * 更新用户状态（启用/封禁）
     */
    @Override
    @Transactional
    public void updateStatus(long id, int status) {
        User po = userRepository.findById(id).get();

        po.setStatus(status);
        userRepository.save(po);
    }

    @Override
    public long count() {
        return userRepository.count();
    }

}
