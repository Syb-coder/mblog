/*
 * base/lang/Consts.java
 *
 * 本文件是整个项目的"常量字典"，集中管理所有用到的魔数（Magic Number）
 * 和字符串常量。这样做的好处：
 *   1. 一处修改，全局生效
 *   2. 给常量起个有意义的名称，比散落的数字更可读
 *   3. 方便新人快速了解系统有哪些"边界值"
 *
 * 阅读提示：
 * - 先看接口级别的静态常量（路径、状态码、排序方式）
 * - 再关注内部接口 order（文章排序选项）
 * - 最后看缓存名和配置项键名（用于 Ehcache 和数据库 options 表）
 *   这些键名会出现在 OptionsServiceImpl 和 SiteOptions 中
 */
package com.sunblog.base.lang;


/**
 * 系统全局常量定义接口
 * <p>
 * 这个接口中定义的常量会被项目各层代码直接引用（如 Consts.SEPARATOR）。
 * 之所以用接口而非类，是因为接口中的字段默认是 public static final，
 * 省去了写修饰符的冗余。
 * </p>
 *
 * <h3>常量分类速览</h3>
 * <ul>
 *   <li>路径常量 → thumbnailPath, avatarPath, AVATAR（文件存储路径）</li>
 *   <li>分隔符 → SEPARATOR, SEPARATOR_X（标签/尺寸拼接）</li>
 *   <li>角色标识 → ROLE_ADMIN（Shiro 授权用）</li>
 *   <li>翻页 → PAGE_DEFAULT_SIZE（列表默认每页条数）</li>
 *   <li>步进 → IDENTITY_STEP, DECREASE_STEP（计数增减）</li>
 *   <li>状态 → STATUS_NORMAL, STATUS_LOCKED, STATUS_CLOSED, STATUS_HIDDEN</li>
 *   <li>排序 → order 内部接口（newest / hottest）</li>
 *   <li>缓存 → CACHE_USER, CACHE_POST（Ehcache 缓存区域名）</li>
 *   <li>编辑器 → EDITOR_MARKDOWN</li>
 *   <li>配置键名 → STORAGE_LIMIT_SIZE 等（对应 mto_options 表）</li>
 * </ul>
 */
public interface Consts {
	/**
	 * 文件存储-缩略图目录
	 */
	String thumbnailPath = "/storage/thumbnails";

	/**
	 * 文件存储-头像目录
	 */
	String avatarPath = "/storage/avatars";

	/**
	 * 默认头像地址，当用户未上传头像时使用此 Gravatar 远程图片
	 */
	String AVATAR = "https://en.gravatar.com/userimage/154673030/b9a54b5b990a61cc074668b2e2a0b8c0.png";

	/**
	 * 通用分隔符，用于多值字段拼接（如标签、配置项）
	 */
	String SEPARATOR = ",";

	/**
	 * 尺寸分隔符，用于宽度 x 高度等数值对拼接
	 */
	String SEPARATOR_X = "x";

	/**
	 * 管理员角色标识
	 */
	String ROLE_ADMIN = "admin";

	int PAGE_DEFAULT_SIZE = 10;

	/**
	 * 自增步进，数值型 ID 生成或计数递增时的步长
	 */
	int IDENTITY_STEP = 1; // 自增步进

	/**
	 * 递减步进，用于计数减量场景（与 IDENTITY_STEP 互逆）
	 */
	int DECREASE_STEP = -1; // 递减

	/**
	 * 最小时间单位（毫秒），1 秒
	 */
	int TIME_MIN = 1000;

	/**
	 * 忽略值，用于 status 等过滤参数表示"不过滤"
	 */
	int IGNORE = -1;

	/**
	 * 零值常量，用于初始化或比较场景
	 */
	int ZERO = 0;

	/**
	 * 站点关闭状态，表示站点或某功能处于禁用状态
	 */
	int STATUS_CLOSED = 1;

	/* 状态-正常，表示资源处于可用状态 */
	int STATUS_NORMAL = 0;

	/* 状态-锁定，表示资源因安全等原因被锁定 */
	int STATUS_LOCKED = 1;

	/**
	 * 隐藏状态，用于内容不对外展示的标识
	 */
	int STATUS_HIDDEN = 1;

	/**
	 * 激活状态标志位 - 用于栏目排序等场景的激活判断
	 */
	int FEATURED_ACTIVE = 1;

	/**
	 * 文章排序方式常量集合
	 * <p>
	 * 通过 URL 参数 order 传入，对应值见各常量字符串。
	 * </p>
	 */
	interface order {
		/**
		 * 最新排序：按创建时间倒序
		 */
		String NEWEST = "newest";

		/**
		 * 热门排序：按评论数、浏览数综合倒序
		 */
		String HOTTEST = "hottest";

	}

	/**
	 * 用户缓存名
	 */
	String CACHE_USER = "userCaches";

	/**
	 * 文章缓存名
	 */
	String CACHE_POST = "postCaches";

	/**
	 * Markdown 编辑器类型标识
	 */
	String EDITOR_MARKDOWN = "markdown";

	/**
	 * 站点配置项键名 - 存储上限大小
	 */
	String STORAGE_LIMIT_SIZE = "storage_limit_size";

	/**
	 * 站点配置项键名 - 存储最大宽度
	 */
	String STORAGE_MAX_WIDTH = "storage_max_width";

	/**
	 * 站点配置项键名 - 文章缩略图尺寸
	 */
	String THUMBNAIL_POST_SIZE = "thumbnail_post_size";
}
