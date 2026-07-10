package com.sunblog.base.utils;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 资源并发锁 —— 防止同一篇文章同时被多人编辑
 *
 * <h3>为什么需要这个锁？</h3>
 * 当两个管理员同时编辑同一篇文章时，后保存的人会覆盖先保存的内容。
 * ResourceLock 使用 ConcurrentHashMap + AtomicInteger 实现了一个轻量级的"编辑锁"，
 * 用于在进入编辑页面时标记资源正在被编辑，离开时释放标记。
 *
 * <h3>和 synchronized / ReentrantLock 的区别</h3>
 * 传统锁是"互斥"的（阻塞等待），
 * ResourceLock 是"计数"的（只是记录有多少人在编辑），
 * 实际的控制逻辑在 Controller 层处理：如果计数 > 1 则提示"该文章正在被 XXX 编辑"。
 *
 * <h3>为什么用 ConcurrentHashMap？</h3>
 * 每篇文章的锁是独立的（key = "POST_OPERATE_{postId}"），
 * ConcurrentHashMap 保证了并发情况下 Key 级别的线程安全。
 * AtomicInteger 保证了计数的原子性。
 */
public class ResourceLock {

    /** 资源锁映射表，按资源 key 维护各自的原子计数器 */
    private static final ConcurrentHashMap<String, AtomicInteger> lockMap = new ConcurrentHashMap<>();

    /**
     * 获取指定 key 的递增计数器
     * <p>首次访问时懒初始化为 0 再自增，保证多线程下计数准确</p>
     *
     * @param key 资源标识
     * @return 递增后的计数器
     */
    public static AtomicInteger getAtomicInteger(String key) {
        if (lockMap.get(key) == null) {
            lockMap.putIfAbsent(key, new AtomicInteger(0));
        }
        int count = lockMap.get(key).incrementAndGet();
        return lockMap.get(key);
    }

    /**
     * 释放指定 key 的计数
     *
     * @param key 资源标识
     */
    public static void giveUpAtomicInteger(String key) {
        if (lockMap.get(key) != null) {
            int source = lockMap.get(key).decrementAndGet();
            if (source <= 0) {
                lockMap.remove(key);
            }
        }
    }

    /**
     * 生成文章操作的锁 key
     *
     * @param postId 文章 ID
     * @return 文章操作锁 key
     */
    public static String getPostKey(Long postId){
        return "POST_OPERATE_{postId}".replace("{postId}", String.valueOf(postId));
    }

    /**
     * 生成图片操作的锁 key
     *
     * @param picId 图片 ID
     * @return 图片操作锁 key
     */
    public static String getPicKey(Long picId){
        return "PIC_OPERATE_{pic}".replace("{pic}", String.valueOf(picId));
    }

}
