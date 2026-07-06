package com.mtons.mblog.modules.service.impl;

import com.mtons.mblog.modules.entity.Channel;
import com.mtons.mblog.modules.entity.Links;
import com.mtons.mblog.modules.repository.LinksRepository;
import com.mtons.mblog.modules.service.LinksService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 友情链接 Service 实现
 * <p>
 * 关键依赖：
 * <ul>
 *   <li>{@link LinksRepository}：友情链接 JPA 仓储</li>
 * </ul>
 * </p>
 * <p>
 * 事务策略：类级别只读事务；写操作方法切换为可写事务。
 * </p>
 *
 * @version : 1.0
 * @date : 2019/11/6
 */
@Service
@Transactional(readOnly = true)
public class LinksServiceImpl implements LinksService {
    @Autowired
    private LinksRepository linksRepository;

    @Override
    public List<Links> findAll() {
        return linksRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    /**
     * 新增/更新友情链接
     */
    @Override
    @Transactional
    public void update(Links links) {
        Optional<Links> optional = linksRepository.findById(links.getId());
        Links po = optional.orElse(new Links());
        BeanUtils.copyProperties(links, po, "created", "updated");
        linksRepository.save(po);
    }

    @Override
    @Transactional
    public void delete(long id) {
        linksRepository.deleteById(id);
    }
}
