package com.mtons.mblog.modules.data;

import java.io.Serializable;
import java.util.Date;

/**
 * 账户档案信息 —— 登录会话中携带的用户核心身份
 *
 * <h3>用途</h3>
 * 当用户成功登录后，这个对象会被 Shiro 放入 Session 中（即"登录凭证"）。
 * 后续请求可以从 SecurityUtils.getSubject().getPrincipal() 获取它，
 * 而不需要每次都查数据库。
 *
 * <h3>为什么叫 AccountProfile 而不是直接用 User 实体？</h3>
 * User 实体包含了密码、邮箱、文章数等大量字段，如果放在 Session 中，
 * 每次请求都要序列化/反序列化这些数据，浪费性能。
 * AccountProfile 只保留了登录态必需的几个字段（id, username, avatar, name, email, status），
 * 是一种"轻量级会话对象"。
 *
 * 此外，ACcountProfile 不包含密码等敏感信息，即使 Session 被泄露，
 * 攻击者也拿不到密码。
 *
 * <h3>在哪里被创建？</h3>
 * - BeanMapUtils.copyPassport(User) 方法负责从 User 实体创建 AccountProfile
 * - UserServiceImpl.login() 创建它并返回
 * - AccountRealm.doGetAuthenticationInfo() 把它放入 Shiro 的认证信息中
 * - 之后可以在任何 Controller 中通过 BaseController.getProfile() 获取
 */
public class AccountProfile implements Serializable {
    private static final long serialVersionUID = 1748764917028425871L;

    /** 用户主键 */
    private long id;
    /** 登录用户名 */
    private String username;
    /** 头像路径 */
    private String avatar;
    /** 用户昵称 */
    private String name;
    /** 邮箱地址 */
    private String email;
    /** 最近登录时间 */
    private Date lastLogin;
    /** 账户状态值 */
    private int status;

    /**
     * 构造账户档案。
     *
     * @param id       用户主键
     * @param username 登录用户名
     */
    public AccountProfile(long id, String username) {
        this.id = id;
        this.username = username;
    }

    /**
     * 获取用户主键。
     *
     * @return 用户主键
     */
    public long getId() {
        return id;
    }

    /**
     * 设置用户主键。
     *
     * @param id 用户主键
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * 获取登录用户名。
     *
     * @return 登录用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置登录用户名。
     *
     * @param username 登录用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取头像路径。
     *
     * @return 头像路径
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * 设置头像路径。
     *
     * @param avatar 头像路径
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * 获取用户昵称。
     *
     * @return 用户昵称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置用户昵称。
     *
     * @param name 用户昵称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取邮箱地址。
     *
     * @return 邮箱地址
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置邮箱地址。
     *
     * @param email 邮箱地址
     */
    public void setEmail(String email) {
        this.email = email;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    /**
     * 获取账户状态。
     *
     * @return 账户状态值
     */
    public int getStatus() {
        return status;
    }

    /**
     * 设置账户状态。
     *
     * @param status 账户状态值
     */
    public void setStatus(int status) {
        this.status = status;
    }
}
