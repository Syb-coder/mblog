package com.mtons.mblog.modules.data;

import com.mtons.mblog.modules.entity.PostTag;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 文章-标签关联 VO
 * <p>
 * 继承自 {@link PostTag} Entity，扩展关联的文章对象 {@link PostVO}，
 * 用于在标签详情页面展示文章列表时携带每篇文章的完整信息。
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PostTagVO extends PostTag implements Serializable {
    private static final long serialVersionUID = 73354108587481371L;

    private PostVO post;
}
