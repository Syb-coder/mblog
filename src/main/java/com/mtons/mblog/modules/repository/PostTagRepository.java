package com.mtons.mblog.modules.repository;

import com.mtons.mblog.modules.entity.PostTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * 文章-标签关联（PostTag）数据访问层
 * <p>
 * 对应 Entity：{@link PostTag}，主键类型 Long。
 * 业务职责：管理文章与标签的多对多关联关系，
 * </p>
 *
 */
@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long>, JpaSpecificationExecutor<PostTag> {

    /**
     * @param pageable 分页参数
     * @param tagId    标签 ID
     * @return 关联记录分页结果
     */
    Page<PostTag> findAllByTagId(Pageable pageable, long tagId);

    /**
     * <p>用于校验文章是否已绑定某标签。</p>
     *
     * @param postId 文章 ID
     * @param tagId  标签 ID
     * @return 关联记录，未命中返回 null
     */
    PostTag findByPostIdAndTagId(long postId, long tagId);

    /**
     * <p>
     * 用于文章详情页渲染标签列表及标签更新时差异比对。
     * </p>
     *
     * @param postId 文章 ID
     * @return 该文章关联的标签 ID 集合
     */
    @Query("select tagId from PostTag where postId = ?1")
    Set<Long> findTagIdByPostId(long postId);

    /**
     * 按文章 ID 删除该文章的所有标签关联（用于文章删除 / 标签重置时级联清理）
     *
     * @param postId 文章 ID
     * @return 被删除的关联记录数
     */
    int deleteByPostId(long postId);
}