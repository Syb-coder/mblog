package com.mtons.mblog.modules.repository;

import com.mtons.mblog.modules.entity.PostAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 文章扩展属性（PostAttribute）数据访问层
 * <p>
 * 对应 Entity：{@link PostAttribute}，主键类型 Long。
 * 业务职责：管理文章的扩展内容（如 Markdown 源、HTML 渲染结果等），
 * 与文章主表一对一关联，依赖 JpaRepository 提供标准 CRUD 能力。
 * </p>
 *
 */
public interface PostAttributeRepository extends JpaRepository<PostAttribute, Long>, JpaSpecificationExecutor<PostAttribute> {
}
