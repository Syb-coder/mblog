package com.mtons.mblog.modules.service.impl;

import com.mtons.mblog.base.lang.Consts;
import com.mtons.mblog.modules.data.PostTagVO;
import com.mtons.mblog.modules.data.PostVO;
import com.mtons.mblog.modules.data.TagVO;
import com.mtons.mblog.modules.entity.PostTag;
import com.mtons.mblog.modules.entity.Tag;
import com.mtons.mblog.modules.repository.PostTagRepository;
import com.mtons.mblog.modules.repository.TagRepository;
import com.mtons.mblog.modules.service.TagService;
import com.mtons.mblog.modules.service.PostService;
import com.mtons.mblog.base.utils.BeanMapUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 标签管理 Service 实现
 * <p>
 * 关键依赖：
 * <ul>
 *   <li>{@link TagRepository}：标签 JPA 仓储</li>
 *   <li>{@link PostTagRepository}：文章-标签关联仓储</li>
 *   <li>{@link PostService}：用于回填最新关联文章 VO</li>
 * </ul>
 * </p>
 * <p>
 * 事务策略：类级别只读事务；写操作方法切换为可写事务。
 * </p>
 *
 */
@Service
@Transactional(readOnly = true)
public class TagServiceImpl implements TagService {
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private PostTagRepository postTagRepository;
    @Autowired
    private PostService postService;

    @Override
    public Page<TagVO> pagingQueryTags(Pageable pageable) {
        Page<Tag> page = tagRepository.findAll(pageable);

        Set<Long> postIds = new HashSet<>();
        List<TagVO> rets = page.getContent().stream().map(po -> {
            postIds.add(po.getLatestPostId());
            return BeanMapUtils.copy(po);
        }).collect(Collectors.toList());

        Map<Long, PostVO> posts = postService.findMapByIds(postIds);
        rets.forEach(n -> n.setPost(posts.get(n.getLatestPostId())));
        return new PageImpl<>(rets, pageable, page.getTotalElements());
    }

    @Override
    public Page<PostTagVO> pagingQueryPosts(Pageable pageable, String tagName) {
        Tag tag = tagRepository.findByName(tagName);
        Assert.notNull(tag, "标签不存在");
        Page<PostTag> page = postTagRepository.findAllByTagId(pageable, tag.getId());

        Set<Long> postIds = new HashSet<>();
        List<PostTagVO> rets = page.getContent().stream().map(po -> {
            postIds.add(po.getPostId());
            return BeanMapUtils.copy(po);
        }).collect(Collectors.toList());

        Map<Long, PostVO> posts = postService.findMapByIds(postIds);
        rets.forEach(n -> n.setPost(posts.get(n.getPostId())));
        return new PageImpl<>(rets, pageable, page.getTotalElements());
    }

    /**
     * 批量更新文章-标签关联
     * <p>核心逻辑：
     * <ol>
     *   <li>按分隔符拆分标签名集合</li>
     *   <li>新关联则建立 PostTag 记录</li>
     * </ol>
     * </p>
     */
    @Override
    @Transactional
    public void batchUpdate(String names, long latestPostId) {
        if (StringUtils.isBlank(names.trim())) {
            return;
        }

        String[] ns = names.split(Consts.SEPARATOR);
        Date current = new Date();
        for (String n : ns) {
            String name = n.trim();
            if (StringUtils.isBlank(name)) {
                continue;
            }

            Tag po = tagRepository.findByName(name);
            if (po != null) {
                PostTag pt = postTagRepository.findByPostIdAndTagId(latestPostId, po.getId());
                if (null != pt) {
                    pt.setWeight(System.currentTimeMillis());
                    postTagRepository.save(pt);
                    continue;
                }
                po.setPosts(po.getPosts() + 1);
                po.setUpdated(current);
            } else {
                po = new Tag();
                po.setName(name);
                po.setCreated(current);
                po.setUpdated(current);
                po.setPosts(1);
            }

            po.setLatestPostId(latestPostId);
            tagRepository.save(po);

            // 建立文章-标签关联记录
            PostTag pt = new PostTag();
            pt.setPostId(latestPostId);
            pt.setTagId(po.getId());
            pt.setWeight(System.currentTimeMillis());
            postTagRepository.save(pt);
        }
    }

    /**
     * 删除某文章的全部标签关联
     * <p>级联维护：先获取关联标签 ID 集合，批量减少标签的 posts 计数，再删除关联记录；
     * 保证标签计数与关联记录一致性。</p>
     */
    @Override
    @Transactional
    public void deteleMappingByPostId(long postId) {
        Set<Long> tagIds = postTagRepository.findTagIdByPostId(postId);
        if (CollectionUtils.isNotEmpty(tagIds)) {
            // 批量减少标签文章数计数，避免逐条更新
            tagRepository.decrementPosts(tagIds);
        }
        postTagRepository.deleteByPostId(postId);
    }
}
