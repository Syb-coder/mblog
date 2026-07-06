package com.mtons.mblog.base.utils;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 资源并发计数锁
 * <p>
 * 基于 {@link ConcurrentHashMap} 与 {@link AtomicInteger} 实现对资源操作的并发计数，
 * </p>
 *
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
