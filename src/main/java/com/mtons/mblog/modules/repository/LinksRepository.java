package com.mtons.mblog.modules.repository;

import com.mtons.mblog.modules.entity.Links;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 友情链接（Links）数据访问层
 * <p>
 * 对应 Entity：{@link Links}，主键类型 Long。
 * 业务职责：管理博客的友情链接数据，依赖 JpaRepository 提供的标准 CRUD 能力。
 * </p>
 *
 */
public interface LinksRepository extends JpaRepository<Links, Long>, JpaSpecificationExecutor<Links> {
}
