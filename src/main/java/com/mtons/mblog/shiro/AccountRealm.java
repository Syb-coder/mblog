package com.mtons.mblog.shiro;

import com.mtons.mblog.base.lang.Consts;
import com.mtons.mblog.modules.data.AccountProfile;
import com.mtons.mblog.modules.data.UserVO;
import com.mtons.mblog.modules.entity.Role;
import com.mtons.mblog.modules.service.UserRoleService;
import com.mtons.mblog.modules.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 账户 Realm：mblog 自定义的 Shiro 安全数据源。
 * <p>
 * 该 Realm 同时承担两类职责：
 * <ul>
 *   <li>认证（Authentication）：根据 {@link UsernamePasswordToken} 提供的用户名/密码，
 *       委托 {@link UserService#login} 完成账户校验，并构建 {@link AccountProfile}
 *       作为登录态主体。</li>
 *   <li>授权（Authorization）：根据当前登录用户的角色列表，加载其拥有的角色与权限，
 *       供 Shiro 的权限/角色校验使用。</li>
 * </ul>
 * </p>
 * <p>
 * <b>支持的 Token 类型</b>：仅处理 {@link UsernamePasswordToken}，由构造方法通过
 * {@link #setAuthenticationTokenClass} 限定。
 * </p>
 * <p>
 * <b>密码校验机制</b>：构造时通过 {@link AllowAllCredentialsMatcher} 关闭 Shiro 默认的
 * 密码比对逻辑——真正的用户名/密码校验下沉至业务层
 * {@link UserService#login(String, String)} 完成，Realm 仅负责将业务层返回的
 * {@link AccountProfile} 装配为 {@link SimpleAuthenticationInfo}。
 * </p>
 */
public class AccountRealm extends AuthorizingRealm {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRoleService userRoleService;

    /**
     * 构造方法：配置凭证匹配器与所支持的 Token 类型。
     * <p>
     * 由于密码校验下沉至业务层（{@link UserService#login}），此处采用
     * {@link AllowAllCredentialsMatcher} 跳过 Shiro 默认的密码比对，
     * 并将支持的 Token 限定为 {@link UsernamePasswordToken}。
     * </p>
     */
    public AccountRealm() {
        super(new AllowAllCredentialsMatcher());
        setAuthenticationTokenClass(UsernamePasswordToken.class);
    }

    /**
     * <p>
     * 授权流程：
     * <ol>
     *   <li>从 {@link SecurityUtils#getSubject()} 取出当前登录主体 {@link AccountProfile}；</li>
     *   <li>调用 {@link UserRoleService#listRoles} 加载用户全部角色；</li>
     * </ol>
     * </p>
     *
     * @param principals 当前 Subject 的身份集合
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // 取出当前登录主体；此处直接取 Subject.principal 而非入参 principals，
        // 便于统一使用业务层封装的 AccountProfile
        AccountProfile profile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        if (profile != null) {
            // 复核用户当前状态，防止被关闭/删除的用户仍具备权限
            UserVO user = userService.get(profile.getId());
            if (user != null) {
                SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
                List<Role> roles = userRoleService.listRoles(user.getId());

                //赋予角色
                roles.forEach(role -> {
                    info.addRole(role.getName());

                    //赋予权限
                    role.getPermissions().forEach(permission -> info.addStringPermission(permission.getName()));
                });
                return info;
            }
        }
        return null;
    }

    /**
     * 认证信息加载：根据 Token 中的用户名/密码完成登录校验并构建登录态。
     * <p>
     * 认证流程：
     * <ol>
     *   <li>将 {@link AuthenticationToken} 强转为 {@link UsernamePasswordToken}；</li>
     *   <li>委托 {@link #getAccount} 调用 {@link UserService#login} 完成用户名/密码校验，
     *       返回 {@link AccountProfile}；</li>
     *   <li>账户被关闭（{@link Consts#STATUS_CLOSED}）时抛出 {@link LockedAccountException}；</li>
     *   <li>构建 {@link SimpleAuthenticationInfo}，并将 profile 写入 Session，
     *       便于后续请求从 Session 中直接读取登录信息。</li>
     * </ol>
     * </p>
     *
     * @param token 待校验的认证令牌，预期类型为 {@link UsernamePasswordToken}
     * @return 装配完成的 {@link SimpleAuthenticationInfo}
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        // Token 类型限定为 UsernamePasswordToken，由构造方法配置；此处强转安全
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        // 委托业务层完成用户名/密码校验，密码校验逻辑下沉至 UserService
        AccountProfile profile = getAccount(userService, token);

        if (null == profile) {
            throw new UnknownAccountException(upToken.getUsername());
        }

        // 账户被关闭，禁止登录
        if (profile.getStatus() == Consts.STATUS_CLOSED) {
            throw new LockedAccountException(profile.getName());
        }

        // 以 AccountProfile 作为主身份构造 AuthenticationInfo
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(profile, token.getCredentials(), getName());
        Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute("profile", profile);
        return info;
    }

    /**
     * 委托 {@link UserService#login} 执行实际的用户名/密码校验，返回登录主体信息。
     *
     * @param userService 用户业务服务
     * @param token       认证令牌，预期为 {@link UsernamePasswordToken}
     * @return 校验成功时返回 {@link AccountProfile}；失败返回 null
     */
    protected AccountProfile getAccount(UserService userService, AuthenticationToken token) {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        return userService.login(upToken.getUsername(), String.valueOf(upToken.getPassword()));
    }
}
