package com.mtons.mblog.modules.service.impl;

import com.mtons.mblog.modules.aspect.PostStatusFilter;
import com.mtons.mblog.modules.data.PostVO;
import com.mtons.mblog.modules.data.UserVO;
import com.mtons.mblog.modules.entity.Post;
import com.mtons.mblog.modules.service.PostSearchService;
import com.mtons.mblog.modules.service.UserService;
import com.mtons.mblog.base.utils.BeanMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.hibernate.search.engine.search.query.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文章全文检索 Service 实现
 * <p>
 * 基于 Hibernate Search（Lucene）实现文章的全文检索能力。
 * </p>
 * 关键依赖：
 * <ul>
 *   <li>{@link EntityManager}：用于获取 Hibernate Search 的 {@link SearchSession}</li>
 *   <li>{@link UserService}：用于检索结果回填作者信息</li>
 * </ul>
 * <p>
 * 事务策略：类级别 {@code @Transactional}，检索过程在事务内保证读取一致性。
 * </p>
 *
 */
@Slf4j
@Service
@Transactional
public class PostSearchServiceImpl implements PostSearchService {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserService userService;

    /**
     * 全文检索文章
     * <p>对 title、summary、tags 三个字段执行 fuzzy=1 的模糊匹配；
     * 命中结果回填作者信息后返回分页对象。</p>
     */
    @Override
    @PostStatusFilter
    public Page<PostVO> search(Pageable pageable, String term) throws Exception {
        SearchSession searchSession = Search.session(entityManager);

        SearchResult<Post> result = searchSession.search(Post.class)
                .where(f -> f.bool()
                        .should(f.match().fields("title", "summary", "tags").matching(term).fuzzy(1))
                )
                .fetch((int) pageable.getOffset(), pageable.getPageSize());

        List<Post> hits = result.hits();
        long totalHitCount = result.total().hitCount();

        List<PostVO> rets = hits.stream().map(BeanMapUtils::copy).collect(Collectors.toList());
        buildUsers(rets);
        return new PageImpl<>(rets, pageable, totalHitCount);
    }

    /**
     * 重建文章索引
     * <p>调用 Hibernate Search 的 MassIndexer 全量重建索引；
     * 中断异常处理：捕获 InterruptedException 后恢复中断标志，避免丢失中断信号。</p>
     */
    @Override
    public void resetIndexes() {
        SearchSession searchSession = Search.session(entityManager);
        try {
            searchSession.massIndexer(Post.class).startAndWait();
        } catch (InterruptedException e) {
            log.error("Indexing interrupted", e);
            // 恢复中断标志，供上层感知并处理
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 批量回填文章列表的作者信息
     */
    private void buildUsers(List<PostVO> list) {
        if (null == list) {
            return;
        }
        HashSet<Long> uids = new HashSet<>();
        list.forEach(n -> uids.add(n.getAuthorId()));
        Map<Long, UserVO> userMap = userService.findMapByIds(uids);
        list.forEach(p -> p.setAuthor(userMap.get(p.getAuthorId())));
    }
}
