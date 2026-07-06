package com.mtons.mblog.web.controller.site;

import com.mtons.mblog.modules.data.PostTagVO;
import com.mtons.mblog.modules.data.TagVO;
import com.mtons.mblog.modules.service.TagService;
import com.mtons.mblog.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 前台标签控制器。
 *
 * <p>URL 前缀：映射 {@code /tags} 与 {@code /tag/{name}}</p>
 * <p>权限要求：公开访问，无需登录</p>
 *
 */
@Controller
public class TagController extends BaseController {
    @Autowired
    private TagService tagService;

    /**
     * 标签列表页面。 *
     * @param model 视图模型
     */
    @RequestMapping("/tags")
    public String index(ModelMap model) {
        Pageable pageable = wrapPageable(Sort.by(Sort.Direction.DESC, "updated"));
        Page<TagVO> page = tagService.pagingQueryTags(pageable);
        model.put("results", page);
        return view(Views.TAG_INDEX);
    }

    /**
     * 标签下的文章列表页面。 *
     * @param name  标签名称
     * @param model 视图模型
     */
    @RequestMapping("/tag/{name}")
    public String tag(@PathVariable String name, ModelMap model) {
        // 按权重倒序，权重越高越靠前
        Pageable pageable = wrapPageable(Sort.by(Sort.Direction.DESC, "weight"));
        Page<PostTagVO> page = tagService.pagingQueryPosts(pageable, name);
        model.put("results", page);

        // 回显标签名供前端展示
        model.put("name", name);
        return view(Views.TAG_VIEW);
    }

}
