package com.mtons.mblog.modules.data;

import com.alibaba.fastjson2.annotation.JSONField;
import com.mtons.mblog.base.lang.Consts;
import com.mtons.mblog.modules.entity.Post;
import com.mtons.mblog.modules.entity.Channel;
import com.mtons.mblog.modules.entity.PostAttribute;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 文章视图对象 PostVO。
 * <p>
 * 继承自 {@link Post}，扩展出编辑器类型、正文内容、作者、栏目及扩展属性等视图层字段，
 * 用于在 Controller 与前端之间传递完整文章信息。
 * </p>
 * attribute 字段通过 {@link JSONField}(serialize = false) 标注禁止序列化，
 * 避免循环引用及冗余数据传输。
 *
 */
public class PostVO extends Post implements Serializable {
	private static final long serialVersionUID = -1144627551517707139L;

	/**
	 * 编辑器类型（如 markdown / html）
	 */
	private String editor;

	/**
	 * 文章正文内容
	 */
	private String content;

	/**
	 * 作者视图对象
	 */
	private UserVO author;

	/**
	 * 所属栏目
	 */
	private Channel channel;

	/**
	 * 文章扩展属性（不参与 JSON 序列化，避免循环引用与冗余传输）
	 */
	@JSONField(serialize = false)
	private PostAttribute attribute;

	/**
	 * 将 tags 字符串按分隔符拆分为数组。
	 * <p>当 tags 为空时返回 null，避免调用方对 null 调用 split 导致空指针。</p>
	 *
	 * @return 标签数组；tags 为空时返回 null
	 */
	public String[] getTagsArray() {
		if (StringUtils.isNotBlank(super.getTags())) {
			return super.getTags().split(Consts.SEPARATOR);
		}
		return null;
	}

	/**
	 * 获取作者
	 * @return 作者
	 */
	public UserVO getAuthor() {
		return author;
	}

	/**
	 * 设置作者
	 * @param author 作者
	 */
	public void setAuthor(UserVO author) {
		this.author = author;
	}

	/**
	 * 获取文章扩展属性
	 * @return 文章扩展属性
	 */
	public PostAttribute getAttribute() {
		return attribute;
	}

	/**
	 * 设置文章扩展属性
	 * @param attribute 文章扩展属性
	 */
	public void setAttribute(PostAttribute attribute) {
		this.attribute = attribute;
	}

	/**
	 * 获取编辑器类型
	 * @return 编辑器类型
	 */
	public String getEditor() {
		return editor;
	}

	/**
	 * 设置编辑器类型
	 * @param editor 编辑器类型
	 */
	public void setEditor(String editor) {
		this.editor = editor;
	}

	/**
	 * 获取文章正文内容
	 * @return 文章正文内容
	 */
	public String getContent() {
		return content;
	}

	/**
	 * 设置文章正文内容
	 * @param content 文章正文内容
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * 获取所属栏目
	 * @return 所属栏目
	 */
	public Channel getChannel() {
		return channel;
	}

	/**
	 * 设置所属栏目
	 * @param channel 所属栏目
	 */
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
}
