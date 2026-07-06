package com.mtons.mblog.modules.data;

import java.io.Serializable;
import java.util.Date;

/**
 * 账户档案信息，用于在会话中承载当前登录用户的核心身份信息。
 * <p>
 * 该对象实现 {@link Serializable}，可作为会话属性在分布式环境下安全传输。
 * </p>
 *
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
