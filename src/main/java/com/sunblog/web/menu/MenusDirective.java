package com.sunblog.web.menu;

import com.sunblog.modules.template.DirectiveHandler;
import com.sunblog.modules.entity.Role;
import com.sunblog.modules.template.TemplateDirective;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * FreeMarker 菜单渲染指令
 * <p>
 * 职责说明：
 * <ul>
 *   <li>继承自 {@link TemplateDirective}，作为自定义 FreeMarker 指令在视图层使用，
 *       指令名称为 {@code menus}；</li>
 *   <li>基于当前登录用户（Shiro Subject）的权限对菜单进行过滤，
 *       仅向视图层返回当前用户有权访问的菜单项；</li>
 *   <li>管理员（{@link Role#ROLE_ADMIN}）直接返回全部菜单，无需逐项校验。</li>
 * </ul>
 *
 * 使用示例：
 * <pre>
 *   &lt;@menus&gt;
 *     &lt;#list results as menu&gt;
 *       &lt;a href="${menu.url}"&gt;${menu.name}&lt;/a&gt;
 *     &lt;/#list&gt;
 *   &lt;/@menus&gt;
 * </pre>
 *
 * @see TemplateDirective
 * @see MenuJsonUtils
 */
@Component
public class MenusDirective extends TemplateDirective {
    /**
     * 获取指令名称
     *
     * @return FreeMarker 指令名称 {@code "menus"}
     */
    @Override
    public String getName() {
        return "menus";
    }

    /**
     * 执行菜单指令
     * <p>
     * 注入到 FreeMarker 渲染上下文中。
     *
     * @param handler 指令处理器，提供参数读取与结果渲染能力
     * @throws Exception 指令执行过程中抛出的异常
     */
    @Override
    public void execute(DirectiveHandler handler) throws Exception {
        // 基于当前登录用户的权限过滤菜单
        List<Menu> menus = filterMenu(SecurityUtils.getSubject());
        handler.put(RESULTS, menus).render();
    }

    /**
     * 根据当前用户的角色与权限过滤菜单
     * <p>
     * 若用户为管理员（{@link Role#ROLE_ADMIN}），直接返回全部菜单；
     * 否则调用 {@link #check(Subject, List)} 逐项校验菜单权限。
     *
     * @param subject 当前登录用户的 Shiro Subject
     * @return 过滤后的菜单列表
     */
    private List<Menu> filterMenu(Subject subject) {
        List<Menu> menus = MenuJsonUtils.getMenus();
        if (!subject.hasRole(Role.ROLE_ADMIN)) {
            // 非管理员：逐项校验菜单权限
            menus = check(subject, menus);
        }
        return menus;
    }

    /**
     * 逐项校验菜单列表的访问权限
     *
     * @param subject 当前登录用户的 Shiro Subject
     * @param menus  待校验的菜单列表
     * @return 通过权限校验的菜单列表
     */
    private List<Menu> check(Subject subject, List<Menu> menus) {
        List<Menu> results = new LinkedList<>();
        for (Menu menu : menus) {
            // 仅保留通过权限校验的菜单项
            if (check(subject, menu)) {
                results.add(menu);
            }
        }

        return results;
    }

    /**
     * 校验单个菜单项的访问权限
     * <p>
     * <ul>
     *   <li>若菜单未配置 permission（空白），则默认允许访问；</li>
     *   <li>否则将 permission 按英文逗号拆分为多个权限标识，用户具备其中任意一个即视为有权限。</li>
     * </ul>
     *
     * @param subject 当前登录用户的 Shiro Subject
     * @param menu    待校验的菜单项
     * @return true 表示用户有权访问该菜单；false 表示无权访问
     */
    private boolean check(Subject subject, Menu menu) {
        boolean authorized = false;
        if (StringUtils.isBlank(menu.getPermission())) {
            // 未配置权限标识，默认放行
            authorized = true;
        } else {
            // 多个权限以英文逗号分隔，满足任一即放行
            for(String perm : menu.getPermission().split(",")){
                if(subject.isPermitted(perm)){
                    authorized = true;
                    break;
                }
            }
        }
        return authorized;
    }

}
