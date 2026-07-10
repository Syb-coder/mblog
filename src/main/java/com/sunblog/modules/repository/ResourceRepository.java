package com.sunblog.modules.repository;

import com.sunblog.modules.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

/**
 * 资源/文件数据访问层
 *
 * <h3>功能</h3>
 * 记录每个上传文件的元信息（MD5、访问路径、引用计数），核心能力：
 * 1. MD5 去重：上传时先查 MD5，命中则直接复用已有路径（AbstractStorage.writeToStore）
 * 2. 引用计数管理：amount 字段记录文件被多少篇文章引用，引用归零后可清理
 * 3. 垃圾清理：find0Before 找出引用计数 <= 0 且超过一定时间的文件，定期清理
 *
 * <h3>为什么需要 amount 引用计数？</h3>
 * 同一张图片可能被多篇文章引用。如果直接删除文件，
 * 其他文章就会出现图片无法显示。通过引用计数，
 * 只有所有引用都释放后才能删除物理文件。
 * 类似于 C++ 的 shared_ptr 或操作系统的硬链接计数。
 */
public interface ResourceRepository extends JpaRepository<Resource, Long>, JpaSpecificationExecutor<Resource> {

    /**
     * @param md5 文件 MD5 摘要
     * @return 资源记录，未命中返回 null
     */
    Resource findByMd5(String md5);

    /**
     * @param md5s MD5 摘要列表
     * @return 资源记录列表
     */
    List<Resource> findByMd5In(List<String> md5s);

    /**
     * @param time 截止时间
     * @return 待清理的资源列表
     */
    @Query(value = "SELECT * FROM mto_resource WHERE amount <= 0 AND update_time < :time ", nativeQuery = true)
    List<Resource> find0Before(@Param("time")String time);

    /**
     * 按 MD5 集合原子调整资源引用计数
     * <p>
     * 通过 JPQL update 批量调整 amount 字段，避免逐条加载；
     * :increment 可为负值用于引用释放场景。
     * </p>
     *
     * @param md5s      MD5 摘要集合
     * @param increment 引用计数增量（可为负）
     * @return 受影响行数
     */
    @Modifying
    @Query("update Resource set amount = amount + :increment where md5 in (:md5s)")
    int updateAmount(@Param("md5s") Collection<String> md5s, @Param("increment") long increment);

    /**
     * 按资源 ID 集合原子调整引用计数
     * <p>
     * 通过 JPQL update 批量调整 amount 字段，避免逐条加载；
     * :increment 可为负值用于引用释放场景。
     * </p>
     *
     * @param ids       资源 ID 集合
     * @param increment 引用计数增量（可为负）
     * @return 受影响行数
     */
    @Modifying
    @Query("update Resource set amount = amount + :increment where id in (:ids)")
    int updateAmountByIds(@Param("ids") Collection<Long> ids, @Param("increment") long increment);
}