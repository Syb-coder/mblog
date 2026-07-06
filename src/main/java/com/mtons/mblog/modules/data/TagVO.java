package com.mtons.mblog.modules.data;

import com.mtons.mblog.modules.entity.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 标签 VO（前端展示 / 服务间传递）
 * <p>
 * 继承自 {@link Tag} Entity，扩展关联文章对象，
 * 用于在标签详情页面携带文章信息渲染。
 * </p>
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TagVO extends Tag implements Serializable {
    private static final long serialVersionUID = -7787865229252467418L;

    /** 关联的文章对象 */
    private PostVO post;
}
