package com.sunblog.web.menu;

import com.alibaba.fastjson2.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

/**
 * 后台菜单 JSON 解析工具类
 * <p>
 * 职责说明：
 * <ul>
 *   <li>从 classpath 下的 {@code /scripts/menu.json} 文件读取菜单配置；</li>
 *   <li>使用 FastJson {@link JSON#parseArray(String, Class)} 将 JSON 字符串解析为
 *       {@link Menu} 列表，构建完整的后台菜单树；</li>
 *   <li>采用懒加载 + 同步方式确保菜单数据仅加载一次，且线程安全。</li>
 * </ul>
 *
 * 加载流程：
 * <ol>
 *   <li>首次调用 {@link #getMenus()} 时触发 {@link #loadJson()} 加载；</li>
 *   <li>读取 {@code /scripts/menu.json} 文件内容为字符串；</li>
 *   <li>使用 FastJson 反序列化为 {@code List<Menu>}；</li>
 * </ol>
 *
 * @create - 2018/5/18
 */
public class MenuJsonUtils {
    /** 菜单配置文件路径，位于 classpath 下 */
    private static String config = "/scripts/menu.json";

    /** 菜单列表缓存，懒加载 */
    private static List<Menu> menus;

    /**
     * 加载并解析菜单配置文件
     * <p>
     * 使用同步方法保证多线程环境下仅加载一次。读取 classpath 下
     * {@code /scripts/menu.json} 文件，并通过 FastJson 解析为 {@link Menu} 列表。
     *
     * @return 解析后的菜单列表
     * @throws IOException 读取文件或解析过程中发生的 IO 异常
     */
    private static synchronized List<Menu> loadJson() throws IOException {
        InputStream inStream = MenuJsonUtils.class.getResourceAsStream(config);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, Charset.forName("UTF-8")));

        StringBuilder json = new StringBuilder();
        String tmp;
        try {
            while ((tmp = reader.readLine()) != null) {
                json.append(tmp);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            reader.close();
            inStream.close();
        }

        // 使用 FastJson 将 JSON 字符串解析为菜单列表
        List<Menu> menus = JSON.parseArray(json.toString(), Menu.class);
        return menus;
    }

    /**
     * <p>
     * 若加载过程中抛出 {@link IOException}，则打印异常堆栈并返回 null。
     *
     * @return 菜单列表（加载失败时返回 null）
     */
    public static List<Menu> getMenus() {
        if (null == menus) {
            try {
                menus = loadJson();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return menus;
    }
}
