package com.mtons.mblog.modules.service;

import com.mtons.mblog.modules.entity.Links;

import java.util.List;

/**
 * 友情链接 Service
 * <p>
 * </p>
 *
 * @version : 1.0
 * @date : 2019/11/6
 */
public interface LinksService {
    /* *
     * @return 友情链接列表
     */
    List<Links> findAll();

    /* *
     * @param links 友情链接对象
     */
    void update(Links links);

    /**
     * 删除友情链接
     *
     * @param id 友情链接 ID
     */
    void delete(long id);
}
