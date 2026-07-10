package com.sunblog.modules.data;

import com.alibaba.fastjson2.annotation.JSONField;
import com.sunblog.base.lang.Consts;
import com.sunblog.modules.entity.Post;
import com.sunblog.modules.entity.Channel;
import com.sunblog.modules.entity.PostAttribute;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 文章视图对象 —— 给前端看的完整文章数据
 *
 * <h3>为什么需要 PostVO 而不是直接用 Post 实体？</h3>
 * Post 实体只包含文章的基本字段（标题、摘要、发布时间等），
 * 它的两大数据：
 * 1. 正文内容（content）—— 存在 PostAttribute 表中（一对一的扩展表）
 * 2. 作者信息（author）—— 需要关联 User 表查询
 *
 * PostVO 把 Post + PostAttribute + User 三合一，
 * Controller/前端拿到一个 PostVO 就能渲染文章页面，
 * 不用再分别查询。
 *
 * <h3>继承而不是组合？</h3>
 * PostVO extends Post，这意味着 PostVO 有 Post 的所有字段（title, summary, created 等），
 * 再加上自己扩展的 content, editor, author, channel, attribute。
 *
 * 这种"继承-扩展"模式在 Java 的 VO 层比较常见，
 * 比在 Post 实体里直接加 VO 字段更容易维护。
 *
 * <h3>attribute 字段为什么标记 JSONField(serialize = false)？</h3>
 * PostAttribute 包含了 markdown 原始文本等大数据，在列表页序列化为 JSON 时
 * 不需要这些数据（列表页只需要 Post 的基本字段），标记排除可以减小响应体积。
 * 同时避免 PostAttribute → Post → PostVO 的循环引用问题。
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
