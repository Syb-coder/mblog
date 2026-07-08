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
 * 功能开关指令 —— 根据配置决定是否渲染某块内容
 *
 * <h3>为什么需要这个？</h3>
 * 管理员可能想临时关闭"发布文章"功能（比如站点维护期间），
 * 但又不想影响管理员自己的操作。
 *
 * &lt;@controls name="post"&gt; 检查 SiteOptions 中的 controls.post 配置，
 * 如果为 true 则渲染，如果为 false 则：
 * - 普通用户：不渲染（看不到发表按钮）
 * - 管理员：仍然渲染（可以继续发表）
 *
 * 这个设计实现了"分级控制"：普通用户受限，管理员不受限。
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
