// 包声明：标签服务实现类所在包
package com.mtons.mblog.modules.service.impl;

// 导入常量定义类，包含分隔符常量等
import com.mtons.mblog.base.lang.Consts;
// 导入文章标签关联 VO
import com.mtons.mblog.modules.data.PostTagVO;
// 导入文章 VO，用于回填最新关联文章信息
import com.mtons.mblog.modules.data.PostVO;
// 导入标签 VO
import com.mtons.mblog.modules.data.TagVO;
// 导入文章标签关联实体类
import com.mtons.mblog.modules.entity.PostTag;
// 导入标签实体类
import com.mtons.mblog.modules.entity.Tag;
// 导入文章标签关联仓储接口
import com.mtons.mblog.modules.repository.PostTagRepository;
// 导入标签仓储接口
import com.mtons.mblog.modules.repository.TagRepository;
// 导入标签服务接口，本类实现该接口
import com.mtons.mblog.modules.service.TagService;
// 导入文章服务接口，用于回填最新关联文章 VO
import com.mtons.mblog.modules.service.PostService;
// 导入 Bean 属性拷贝工具
import com.mtons.mblog.base.utils.BeanMapUtils;
// 导入 Apache Commons 集合工具类，用于判断集合非空
import org.apache.commons.collections.CollectionUtils;
// 导入 Apache Commons 字符串工具类
import org.apache.commons.lang3.StringUtils;
// 导入 Spring 自动注入注解
import org.springframework.beans.factory.annotation.Autowired;
// 导入 Spring Data 分页相关类
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
// 导入 Spring Service 注解
import org.springframework.stereotype.Service;
// 导入 Spring 事务注解
import org.springframework.transaction.annotation.Transactional;
// 导入 Spring 断言工具
import org.springframework.util.Assert;

// 导入 Java 工具类包
import java.util.*;
// 导入 Stream 收集器
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
// @Service 注解：将该类注册为 Spring 业务服务组件
@Service
// @Transactional(readOnly = true)：类级别只读事务
@Transactional(readOnly = true)
// TagServiceImpl：标签管理服务实现类
public class TagServiceImpl implements TagService {
    // @Autowired：Spring 自动注入标签仓储实例
    @Autowired
    // tagRepository：标签 JPA 仓储，提供标签表的 CRUD 操作
    private TagRepository tagRepository;
    // @Autowired：Spring 自动注入文章标签关联仓储实例
    @Autowired
    // postTagRepository：文章-标签关联 JPA 仓储
    private PostTagRepository postTagRepository;
    // @Autowired：Spring 自动注入文章服务实例
    @Autowired
    // postService：文章服务，用于批量查询并回填最新关联文章信息
    private PostService postService;

    // @Override：实现接口方法
    @Override
    // pagingQueryTags：分页查询标签列表，每个标签附带最新关联文章信息
    public Page<TagVO> pagingQueryTags(Pageable pageable) {
        // 分页查询所有标签
        Page<Tag> page = tagRepository.findAll(pageable);

        // 收集所有标签的最新关联文章 ID
        Set<Long> postIds = new HashSet<>();
        // 将标签实体转换为 VO 并收集文章 ID
        List<TagVO> rets = page.getContent().stream().map(po -> {
            // 收集每个标签的最新关联文章 ID
            postIds.add(po.getLatestPostId());
            // 将标签实体拷贝为 VO
            return BeanMapUtils.copy(po);
        }).collect(Collectors.toList());

        // 批量查询文章信息，返回以文章 ID 为 key 的 Map
        Map<Long, PostVO> posts = postService.findMapByIds(postIds);
        // 遍历标签 VO 列表，将最新关联文章信息设置到每个标签上
        rets.forEach(n -> n.setPost(posts.get(n.getLatestPostId())));
        // 返回带文章信息的标签分页结果
        return new PageImpl<>(rets, pageable, page.getTotalElements());
    }

    // @Override：实现接口方法
    @Override
    // pagingQueryPosts：根据标签名分页查询关联的文章列表
    public Page<PostTagVO> pagingQueryPosts(Pageable pageable, String tagName) {
        // 根据标签名查找对应的标签实体
        Tag tag = tagRepository.findByName(tagName);
        // 断言标签必须存在，不存在则抛出异常
        Assert.notNull(tag, "标签不存在");
        // 根据标签 ID 分页查询文章标签关联记录
        Page<PostTag> page = postTagRepository.findAllByTagId(pageable, tag.getId());

        // 收集所有关联的文章 ID
        Set<Long> postIds = new HashSet<>();
        // 将关联实体转换为 VO 并收集文章 ID
        List<PostTagVO> rets = page.getContent().stream().map(po -> {
            // 收集每条关联记录中的文章 ID
            postIds.add(po.getPostId());
            // 将关联实体拷贝为 VO
            return BeanMapUtils.copy(po);
        }).collect(Collectors.toList());

        // 批量查询文章信息
        Map<Long, PostVO> posts = postService.findMapByIds(postIds);
        // 将文章信息设置到每条关联 VO 上
        rets.forEach(n -> n.setPost(posts.get(n.getPostId())));
        // 返回带文章信息的关联分页结果
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
    // @Override：实现接口方法
    @Override
    // @Transactional：可写事务，批量更新需要事务保护
    @Transactional
    // batchUpdate：批量更新文章标签关联，names 为逗号分隔的标签名字符串
    public void batchUpdate(String names, long latestPostId) {
        // 如果标签名字符串为空或仅含空白字符，直接返回不做操作
        if (StringUtils.isBlank(names.trim())) {
            return;
        }

        // 按分隔符（逗号）切分标签名数组
        String[] ns = names.split(Consts.SEPARATOR);
        // 获取当前时间戳，用于更新时间字段
        Date current = new Date();
        // 遍历每个标签名
        for (String n : ns) {
            // 去除首尾空白
            String name = n.trim();
            // 如果处理后的名称为空，跳过
            if (StringUtils.isBlank(name)) {
                continue;
            }

            // 在数据库中查找是否已存在同名标签
            Tag po = tagRepository.findByName(name);
            if (po != null) {
                // 标签已存在：检查与当前文章是否已有关联
                PostTag pt = postTagRepository.findByPostIdAndTagId(latestPostId, po.getId());
                // 如果已有关联记录，更新其权重时间戳
                if (null != pt) {
                    // 更新权重为当前时间戳（越新越靠前）
                    pt.setWeight(System.currentTimeMillis());
                    // 保存更新的关联记录
                    postTagRepository.save(pt);
                    // 跳过后续逻辑，继续下一个标签
                    continue;
                }
                // 无已有关联：增加标签的文章计数 +1
                po.setPosts(po.getPosts() + 1);
                // 更新最后修改时间
                po.setUpdated(current);
            } else {
                // 标签不存在：创建新的标签实体
                po = new Tag();
                // 设置标签名
                po.setName(name);
                // 设置创建时间
                po.setCreated(current);
                // 设置修改时间
                po.setUpdated(current);
                // 初始文章计数为 1
                po.setPosts(1);
            }

            // 设置标签的最新关联文章 ID
            po.setLatestPostId(latestPostId);
            // 保存标签（新增或更新）
            tagRepository.save(po);

            // 建立文章-标签关联记录
            PostTag pt = new PostTag();
            // 设置关联的文章 ID
            pt.setPostId(latestPostId);
            // 设置关联的标签 ID
            pt.setTagId(po.getId());
            // 设置权重为当前时间戳
            pt.setWeight(System.currentTimeMillis());
            // 保存关联记录
            postTagRepository.save(pt);
        }
    }

    /**
     * 删除某文章的全部标签关联
     * <p>级联维护：先获取关联标签 ID 集合，批量减少标签的 posts 计数，再删除关联记录；
     * 保证标签计数与关联记录一致性。</p>
     */
    // @Override：实现接口方法
    @Override
    // @Transactional：可写事务，删除操作需要事务保护
    @Transactional
    // deteleMappingByPostId：删除某文章的全部标签关联
    public void deteleMappingByPostId(long postId) {
        // 查询该文章关联的所有标签 ID 集合
        Set<Long> tagIds = postTagRepository.findTagIdByPostId(postId);
        // 如果有关联的标签
        if (CollectionUtils.isNotEmpty(tagIds)) {
            // 批量减少这些标签的文章数计数（避免逐条更新）
            tagRepository.decrementPosts(tagIds);
        }
        // 删除该文章的所有标签关联记录
        postTagRepository.deleteByPostId(postId);
    }
}