package com.mtons.mblog.modules.repository;

import com.mtons.mblog.modules.entity.PostResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

/**
 * 文章-资源关联（PostResource）数据访问层
 * <p>
 * 对应 Entity：{@link PostResource}，主键类型 Long。
 * 业务职责：管理文章与上传资源（如图片）的关联关系，
 * </p>
 *
 */
public interface PostResourceRepository extends JpaRepository<PostResource, Long>, JpaSpecificationExecutor<PostResource> {

    /**
     * 按文章 ID 删除该文章的所有资源关联（用于文章删除时级联清理）
     *
     * @param postId 文章 ID
     * @return 被删除的关联记录数
     */
    int deleteByPostId(long postId);

    /**
     * 按文章 ID + 资源 ID 集合删除指定关联（用于文章编辑时移除部分图片）
     *
     * @param postId     文章 ID
     * @param resourceId 待删除的资源 ID 集合
     * @return 被删除的关联记录数
     */
    int deleteByPostIdAndResourceIdIn(long postId, Collection<Long> resourceId);

    /**
     * <p>用于资源清理前判断是否仍被文章引用。</p>
     *
     * @param resourceId 资源 ID
     * @return 关联记录列表
     */
    List<PostResource> findByResourceId(long resourceId);

    /* *
     * @param postId 文章 ID
     * @return 关联记录列表
     */
    List<PostResource> findByPostId(long postId);

}
