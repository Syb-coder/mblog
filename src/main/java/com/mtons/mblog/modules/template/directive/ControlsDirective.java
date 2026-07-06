package com.mtons.mblog.modules.template.directive;

import com.mtons.mblog.base.lang.Consts;
import com.mtons.mblog.config.SiteOptions;
import com.mtons.mblog.modules.template.DirectiveHandler;
import com.mtons.mblog.modules.template.TemplateDirective;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 控制器开关指令
 * <p>
 * FreeMarker 使用方式：
 * <pre>{@code
 * <@controls name="post">
 *     <!-- 当开关开启时渲染此处内容 -->
 * </@controls>
 * }</pre>
 * <p>
 * 支持参数：
 * <ul>
 *     <li>name：控制项名称（对应 SiteOptions 中的配置项）</li>
 * </ul>
 * 输出：当控制项开启时渲染指令体；当控制项为 post 且关闭时，
 * 若当前用户为管理员则仍然渲染指令体。
 */
@Component
public class ControlsDirective extends TemplateDirective {
    @Autowired
    private SiteOptions siteOptions;

    /**
     * 获取指令名称
     *
     * @return 指令名称 "controls"
     */
    @Override
    public String getName() {
        return "controls";
    }

    /**
     * 执行控制项判断
     *
     * @param handler 指令处理器
     * @throws Exception 异常
     */
    @Override
    public void execute(DirectiveHandler handler) throws Exception {
        String control = handler.getString("name");

        if (StringUtils.isBlank(control)) {
            return;
        }

        String value = BeanUtils.getProperty(siteOptions.getControls(), control);
        if ("true".equalsIgnoreCase(value)) {
            handler.render();
        } else {
            // 当控制项 post 为关闭时，继续判断角色：管理员仍可访问
            if ("post".equalsIgnoreCase(control) && SecurityUtils.getSubject() != null && SecurityUtils.getSubject().hasRole(Consts.ROLE_ADMIN)) {
                handler.render();
            }
        }
    }
}
