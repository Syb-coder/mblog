package com.mtons.mblog.base.utils;

/**
 * 文件路径生成工具 —— 头像散列目录 + 文件签名路径
 *
 * <h3>为什么需要散列目录？</h3>
 * 如果所有用户的头像都直接放在 /avatars/ 目录下，
 * 当用户数量很多时（比如 10 万），单目录下文件过多会导致文件系统性能下降。
 *
 * getAvatar(key) 把用户 ID 格式化为 9 位数字，按 3-3-3 切分目录：
 * 用户 ID=50 → "000/000/050" → 头像存储在 /avatars/000/000/050_100.jpg
 * 这样每个目录最多 1000 个文件，分散了 I/O 压力。
 *
 * <h3>为什么文件要用 MD5 命名？</h3>
 * wholePathName 用 key（MD5 摘要）作为文件名，实现了：
 * 1. 内容可寻址：同一张图片即使上传多次，MD5 相同 → 文件名相同 → 自动去重
 * 2. 防冲突：MD5 碰撞概率极低，不用担心文件名重复
 * 3. 防盗链：MD5 文件名不易被猜测
 */
public class FilePathUtils {
	/** 头像路径每级目录的位数切分规则，3-3-3 共 9 位 */
	private static final int[] AVATAR_GRIDS = {3, 3, 3};
	/** 头像 key 补齐后的总长度，对应 9 位数字 */
	private static final int AVATAR_LENGTH = 9;

	/**
	 * 根据用户 key 生成头像散列路径
	 * <p>
	 * 将 key 补齐到 9 位后按 3-3-3 切分作为多级目录，避免单目录下文件过多。
	 * </p>
	 * @param key 用户标识（如用户 ID）
	 * @return 形如 "000/000/050" 的多级目录路径
	 */
	public static String getAvatar(long key) {
		String r = String.format("%09d", key);
		StringBuffer buf = new StringBuffer(32);
		
		int pos = 0;
		for (int t: AVATAR_GRIDS) {
			buf.append(r.substring(pos, pos + t));
			pos += t;
			if (pos < AVATAR_LENGTH) {
				buf.append('/');
			}
		}
        return buf.toString();
	}

	/**
	 * 生成路径和文件名
	 *
	 * @param originalFilename 原始文件名
	 * @param key 用于命名的内容（如 MD5）
	 * @return 10位长度文件名+文件后缀
	 */
	public static String wholePathName(String originalFilename, String key) {
		StringBuilder builder = new StringBuilder(52);
		builder.append("/_signature/");
		builder.append(key);
		builder.append(FileKit.getSuffix(originalFilename));
		return builder.toString();
	}

	/**
	 * 在指定基路径下拼接带签名的文件名
	 *
	 * @param ext      原始文件名（用于推断扩展名）
	 * @param key      用于命名的内容（如 MD5）
	 * @return 完整相对路径
	 */
	public static String wholePathName(String basePath, String ext, String key) {
		return basePath + wholePathName(ext, key);
	}
	
	/**
	 * 工具类自测入口，用于本地验证路径生成结果
	 *
	 * @param args 命令行参数（未使用）
	 */
	public static void main(String[] args) {
		String base = FilePathUtils.getAvatar(50);
		System.out.println(String.format("/%s_%d.jpg", base, 100));
		System.out.println(FilePathUtils.wholePathName("a.jpg", "123"));
	}
	
}
