package com.mtons.mblog.modules.repository;

import com.mtons.mblog.modules.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

/**
 * 资源（Resource）数据访问层
 * <p>
 * 对应 Entity：{@link Resource}，主键类型 Long。
 * 业务职责：管理上传文件资源元数据，支持按 MD5 检索去重、按 MD5/ID 批量调整引用计数，
 * </p>
 *
 */
public interface ResourceRepository extends JpaRepository<Resource, Long>, JpaSpecificationExecutor<Resource> {

    /* *
     * @param md5 文件 MD5 摘要
     * @return 资源记录，未命中返回 null
     */
    Resource findByMd5(String md5);

    /* *
     * @param md5 MD5 摘要列表
     * @return 资源记录列表
     */
    List<Resource> findByMd5In(List<String> md5);

    /**
     * <p>
     * </p>
     *
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
    int updateAmountByIds(@Param("ids") Collection<Long> md5s, @Param("increment") long increment);
}
