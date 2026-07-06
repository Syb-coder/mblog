package com.mtons.mblog.modules.service.impl;

import com.mtons.mblog.modules.data.FavoriteVO;
import com.mtons.mblog.modules.data.PostVO;
import com.mtons.mblog.modules.repository.FavoriteRepository;
import com.mtons.mblog.base.utils.BeanMapUtils;
import com.mtons.mblog.modules.entity.Favorite;
import com.mtons.mblog.modules.service.FavoriteService;
import com.mtons.mblog.modules.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

/**
 * 收藏记录 Service 实现
 * <p>
 * 关键依赖：
 * <ul>
 *   <li>{@link FavoriteRepository}：收藏记录 JPA 仓储</li>
 *   <li>{@link PostService}：用于回填收藏对应文章的 VO 信息</li>
 * </ul>
 * </p>
 * <p>
 * 事务策略：类级别只读事务；写操作方法 {@code rollbackFor = Throwable.class} 全异常回滚，
 * 保证收藏记录与文章收藏数计数的一致性（与 {@link PostServiceImpl#favor} 在外层事务内联动）。
 * </p>
 *
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class FavoriteServiceImpl implements FavoriteService {
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private PostService postService;

    @Override
    public Page<FavoriteVO> pagingByUserId(Pageable pageable, long userId) {
        Page<Favorite> page = favoriteRepository.findAllByUserId(pageable, userId);

        List<FavoriteVO> rets = new ArrayList<>();
        Set<Long> postIds = new HashSet<>();
        for (Favorite po : page.getContent()) {
            rets.add(BeanMapUtils.copy(po));
            postIds.add(po.getPostId());
        }

        if (postIds.size() > 0) {
            Map<Long, PostVO> posts = postService.findMapByIds(postIds);

            for (FavoriteVO t : rets) {
                PostVO p = posts.get(t.getPostId());
                t.setPost(p);
            }
        }
        return new PageImpl<>(rets, pageable, page.getTotalElements());
    }

    /**
     * 新增收藏记录
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void add(long userId, long postId) {
        Favorite po = favoriteRepository.findByUserIdAndPostId(userId, postId);

        Assert.isNull(po, "您已经收藏过此文章");

        // 未收藏则新增记录，配合 PostService#favor 同步累加文章收藏计数
        po = new Favorite();
        po.setUserId(userId);
        po.setPostId(postId);
        po.setCreated(new Date());

        favoriteRepository.save(po);
    }

    /**
     * 取消收藏
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void delete(long userId, long postId) {
        Favorite po = favoriteRepository.findByUserIdAndPostId(userId, postId);
        Assert.notNull(po, "还没有喜欢过此文章");
        favoriteRepository.delete(po);
    }

    /**
     * 删除某文章下的全部收藏记录（文章删除时级联清理）
     * <p>级联清理：直接调用仓储批量删除，返回受影响行数用于日志审计。</p>
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteByPostId(long postId) {
        int rows = favoriteRepository.deleteByPostId(postId);
        log.info("favoriteRepository delete {}", rows);
    }

}
