package com.mtons.mblog.shiro.tags;

import freemarker.core.Environment;
import freemarker.log.Logger;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.Map;

/**
 * 属性输出指定 principal 的指定属性值。
 * <p>用法示例：</p>
 * <ul>
 *   <li><code>&lt;@shiro.principal/&gt;</code>：输出默认 principal 的 toString()</li>
 *   <li><code>&lt;@shiro.principal type="com.mtons.mblog.modules.data.AccountProfile"/&gt;</code>：
 *       输出指定类型 principal 的 toString()</li>
 *   <li><code>&lt;@shiro.principal property="username"/&gt;</code>：
 *       输出默认 principal 的 username 属性值</li>
 * </ul>
 * <p>若用户未登录或未找到对应 principal，标签不输出任何内容。</p>
 * <p>对应 Shiro 的 {@link org.apache.shiro.web.tags.PrincipalTag} 能力。</p>
 *
 * @since 0.2
 */
public class PrincipalTag extends SecureTag {
    static final Logger log = Logger.getLogger("PrincipalTag");

    /**
     * 从标签参数中获取 principal 类型名。
     *
     * @param params 标签参数集
     * @return principal 类型的全限定类名；未指定时返回 null
     */
    String getType(Map params) {
        return getParam(params, "type");
    }

    /**
     * 从标签参数中获取 principal 的属性名。
     *
     * @param params 标签参数集
     * @return 属性名；未指定时返回 null（表示输出 toString()）
     */
    String getProperty(Map params) {
        return getParam(params, "property");
    }

    /**
     * 渲染入口：解析 principal 并按属性输出字符串。
     *
     * @param env   FreeMarker 环境
     * @param params 标签参数
     * @param body   标签体（本标签不使用）
     * @throws IOException      IO 异常
     */
    @SuppressWarnings("unchecked")
    @Override
    public void render(Environment env, Map params, TemplateDirectiveBody body) throws IOException, TemplateException {
        String result = null;

        if (getSubject() != null) {
            // 取出要输出的 principal
            Object principal;

            if (getType(params) == null) {
                principal = getSubject().getPrincipal();
            } else {
                principal = getPrincipalFromClassName(params);
            }

            // 解析 principal 的字符串值
            if (principal != null) {
                String property = getProperty(params);

                if (property == null) {
                    result = principal.toString();
                } else {
                    result = getPrincipalProperty(principal, property);
                }
            }
        }

        // 输出 principal 字符串
        if (result != null) {
            try {
                env.getOut().write(result);
            } catch (IOException ex) {
                throw new TemplateException("Error writing [" + result + "] to Freemarker.", ex, env);
            }
        }
    }

    /**
     * 根据 type 参数指定的全限定类名，从 principals 集合中取出对应类型的 principal。
     *
     * @param params 标签参数集
     * @return 与类型匹配的 principal；类找不到时返回 null
     */
    @SuppressWarnings("unchecked")
    Object getPrincipalFromClassName(Map params) {
        String type = getType(params);

        try {
            Class cls = Class.forName(type);

            return getSubject().getPrincipals().oneByType(cls);
        } catch (ClassNotFoundException ex) {
            log.error("Unable to find class for name [" + type + "]", ex);
        }

        return null;
    }

    /**
     * 通过 JavaBean 内省机制读取 principal 的指定属性值并转为字符串。
     *
     * @param principal principal 实例
     * @param property  属性名
     * @return 属性值的字符串形式
     */
    String getPrincipalProperty(Object principal, String property) throws TemplateModelException {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(principal.getClass());

            for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                if (propertyDescriptor.getName().equals(property)) {
                    Object value = propertyDescriptor.getReadMethod().invoke(principal, (Object[]) null);

                    return String.valueOf(value);
                }
            }

            throw new TemplateModelException("Property [" + property + "] not found in principal of type [" + principal.getClass().getName() + "]");
        } catch (Exception ex) {
            throw new TemplateModelException("Error reading property [" + property + "] from principal of type [" + principal.getClass().getName() + "]", ex);
        }
    }
}
