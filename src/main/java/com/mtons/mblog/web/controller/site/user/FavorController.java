package com.mtons.mblog.web.controller.site.user;

import com.mtons.mblog.base.lang.Result;
import com.mtons.mblog.modules.data.AccountProfile;
import com.mtons.mblog.modules.service.PostService;
import com.mtons.mblog.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**

 */
@RestController
@RequestMapping("/user")
public class FavorController extends BaseController {
    @Autowired
    private PostService postService;

    /**
     * 收藏文章
     * @param id
     * @return
     */
    @RequestMapping("/favor")
    public Result favor(Long id) {
        Result data = Result.failure("操作失败");
        if (id != null) {
            try {
                AccountProfile up = getProfile();
                postService.favor(up.getId(), id);
                data = Result.success();
            } catch (Exception e) {
                data = Result.failure(e.getMessage());
            }
        }
        return data;
    }

    /**
     * 取消收藏
     * @param id
     * @return
     */
    @RequestMapping("/unfavor")
    public Result unfavor(Long id) {
        Result data = Result.failure("操作失败");
        if (id != null) {
            try {
                AccountProfile up = getProfile();
                postService.unfavor(up.getId(), id);
                data = Result.success();
            } catch (Exception e) {
                data = Result.failure(e.getMessage());
            }
        }
        return data;
    }
}
