package com.mtons.mblog.web.controller;

import com.mtons.mblog.base.lang.Result;
import com.mtons.mblog.base.storage.StorageFactory;
import com.mtons.mblog.base.utils.MD5;
import com.mtons.mblog.config.SiteOptions;
import com.mtons.mblog.modules.data.AccountProfile;
import com.mtons.mblog.modules.service.UserService;
import com.mtons.mblog.web.formatter.StringEscapeEditor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Controller 基类，为所有 Web 控制器提供通用能力。
 *
 * <p>子类继承本类后可直接使用以下能力：</p>
 * <ul>
 *   <li>当前用户获取：通过 {@link #getProfile()} 获取已登录的 {@link AccountProfile}</li>
 *   <li>登录态判断：通过 {@link #isAuthenticated()} 判断当前 Subject 是否已认证或 RememberMe</li>
 *   <li>分页参数封装：通过 {@link #wrapPageable()} 系列方法从请求中提取 pageNo/pageSize 并构造 {@link PageRequest}</li>
 *   <li>统一登录执行：通过 {@link #executeLogin(String, String, boolean)} 完成 Shiro 登录流程并返回 {@link Result}</li>
 *   <li>数据绑定初始化：通过 {@link #initBinder(ServletRequestDataBinder)} 注册日期格式化与 XSS 转义编辑器</li>
 * </ul>
 *
 * <p>子类使用方式：直接继承并调用 protected 方法即可，无需重复声明 StorageFactory、SiteOptions。</p>
 *
 * @since 3.0
 */
@Slf4j
public class BaseController {
    @Autowired
    protected StorageFactory storageFactory;
    @Autowired
    protected SiteOptions siteOptions;
    @Autowired
    protected UserService userService;

    /**
     * 初始化数据绑定器，注册自定义类型转换器。
     *
     * <p>注册两类编辑器：</p>
     * <ul>
     *   <li>Date 类型：使用 yyyy-MM-dd HH:mm:ss 格式解析，允许空值</li>
     *   <li>String 类型：使用 {@link StringEscapeEditor} 进行 XSS 转义，防止脚本注入</li>
     * </ul>
     *
     * @param binder Spring 提供的数据绑定器
     */
    @InitBinder
    public void initBinder(ServletRequestDataBinder binder) {
        // 注册日期格式编辑器，统一解析前端传入的日期字符串
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), true));

        // 注册字符串编辑器，对入参进行 HTML 转义以防御 XSS 攻击
        binder.registerCustomEditor(String.class, new StringEscapeEditor(true, false));
    }

    /**
     * 获取当前登录用户信息。
     *
     * <p>从 Shiro Subject 中取出已认证的主体，类型为 {@link AccountProfile}。</p>
     *
     * @return 当前登录用户资料；未登录时返回 null
     */
    protected AccountProfile getProfile() {
        Subject subject = SecurityUtils.getSubject();
        return (AccountProfile) subject.getPrincipal();
    }

    /**
     * @param profile 待写入 Session 的用户资料
     */
    protected void putProfile(AccountProfile profile) {
        SecurityUtils.getSubject().getSession(true).setAttribute("profile", profile);
    }

    /**
     * 判断当前请求是否已通过身份认证（包含 RememberMe 状态）。
     *
     * @return 已认证或为 RememberMe 时返回 true；否则返回 false
     */
    protected boolean isAuthenticated() {
        return SecurityUtils.getSubject() != null && (SecurityUtils.getSubject().isAuthenticated() || SecurityUtils.getSubject().isRemembered());
    }

    /**
     * 从请求参数中提取分页参数并构造 {@link PageRequest}（无排序）。
     *
     * @return 默认排序的分页请求对象
     */
    protected PageRequest wrapPageable() {
        return wrapPageable(null);
    }

    /**
     * 从请求参数中提取分页参数并构造带排序的 {@link PageRequest}。
     *
     * <p>从 HTTP 请求中读取 pageNo（默认 1）与 pageSize（默认 10），
     * 因 PageRequest 页码从 0 开始计数，故对 pageNo 减 1。</p>
     *
     * @param sort 排序条件，为 null 时使用 {@link Sort#unsorted()}
     * @return 分页请求对象
     */
    protected PageRequest wrapPageable(Sort sort) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        int pageSize = ServletRequestUtils.getIntParameter(request, "pageSize", 10);
        int pageNo = ServletRequestUtils.getIntParameter(request, "pageNo", 1);

        if (null == sort) {
            sort = Sort.unsorted();
        }
        // PageRequest 页码从 0 开始，故 pageNo - 1
        return PageRequest.of(pageNo - 1, pageSize, sort);
    }

    /**
     * 根据显式传入的页码与每页大小构造分页请求（无排序）。
     *
     * <p>当 pn 为空或 0 时默认取第 1 页；pageSize 为空或 0 时默认每页 10 条。</p>
     *
     * @param pn       页码，从 1 开始
     * @param pageSize 每页条数
     * @return 分页请求对象
     */
    protected PageRequest wrapPageable(Integer pn, Integer pageSize) {
        if (pn == null || pn == 0) {
            pn = 1;
        }
        if (pageSize == null || pageSize == 0) {
            pageSize = 10;
        }
        // PageRequest 页码从 0 开始，故 pn - 1
        return PageRequest.of(pn - 1, pageSize);
    }

    /**
     * <p>从站点配置中读取 theme 配置项，将视图名拼接为 /{theme}{view} 形式，
     * 便于多主题切换。</p>
     *
     * @param view 视图相对路径（如 /index）
     * @return 完整视图路径
     */
    protected String view(String view) {
        return "/" + siteOptions.getValue("theme") + view;
    }

    /**
     * 执行 Shiro 登录流程，封装各类认证异常为统一 {@link Result}。
     *
     * <p>登录流程：</p>
     * <ol>
     *   <li>校验用户名/密码非空，为空直接返回失败结果</li>
     *   <li>使用 {@link MD5#md5(String)} 对密码进行 MD5 加密后构造 {@link UsernamePasswordToken}</li>
     *   <li>调用 {@link Subject#login(org.apache.shiro.authc.AuthenticationToken)} 执行认证</li>
     *   <li>捕获未知账号、账号锁定、认证失败异常并返回对应中文提示</li>
     * </ol>
     *
     * @param username  用户名
     * @param password 明文密码（方法内部会做 MD5）
     * @param rememberMe 是否启用 RememberMe
     * @return 登录结果，成功时携带 {@link AccountProfile}
     */
    protected Result<AccountProfile> executeLogin(String username, String password, boolean rememberMe) {
        Result<AccountProfile> ret = Result.failure("登录失败");

        if (StringUtils.isAnyBlank(username, password)) {
            return ret;
        }

        UsernamePasswordToken token = new UsernamePasswordToken(username, MD5.md5(password), rememberMe);

        try {
            SecurityUtils.getSubject().login(token);
            ret = Result.success(getProfile());
        } catch (UnknownAccountException e) {
            log.error(e.getMessage());
            ret = Result.failure("账号不存在");
        } catch (LockedAccountException e) {
            log.error(e.getMessage());
            ret = Result.failure("账号已被锁定");
        } catch (AuthenticationException e) {
            log.error(e.getMessage());
            ret = Result.failure("账号或密码错误");
        }
        return ret;
    }
}