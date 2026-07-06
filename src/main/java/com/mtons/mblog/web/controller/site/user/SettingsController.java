package com.mtons.mblog.web.controller.site.user;

import com.mtons.mblog.base.lang.Result;
import com.mtons.mblog.base.lang.Consts;
import com.mtons.mblog.base.utils.FileKit;
import com.mtons.mblog.base.utils.FilePathUtils;
import com.mtons.mblog.base.utils.ImageUtils;
import com.mtons.mblog.modules.data.AccountProfile;
import com.mtons.mblog.modules.data.UserVO;
import com.mtons.mblog.modules.service.UserService;
import com.mtons.mblog.web.controller.BaseController;
import com.mtons.mblog.web.controller.site.Views;
import com.mtons.mblog.web.controller.site.posts.UploadController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 用户设置 Controller，负责个人资料、头像、密码等账户设置项的页面渲染与提交处理。
 *
 * @version : 1.0
 */
@Controller
@RequestMapping("/settings")
public class SettingsController extends BaseController {

    /**
     * 渲染个人资料设置页。
     *
     * @param model 视图模型
     * @return 资料设置页视图
     */
    @GetMapping(value = "/profile")
    public String view(ModelMap model) {
        AccountProfile profile = getProfile();
        UserVO view = userService.get(profile.getId());
        model.put("view", view);
        return view(Views.SETTINGS_PROFILE);
    }

    /**
     * 渲染头像设置页。
     *
     * @return 头像设置页视图
     */
    @GetMapping(value = "/avatar")
    public String avatar() {
        return view(Views.SETTINGS_AVATAR);
    }

    /**
     * 渲染密码修改页。
     *
     * @return 密码修改页视图
     */
    @GetMapping(value = "/password")
    public String password() {
        return view(Views.SETTINGS_PASSWORD);
    }

    /**
     * 提交更新个人资料（昵称、签名等）。
     * <p>
     * 更新成功后刷新会话中的 Profile 并回写最新资料以便页面回显。
     * </p>
     *
     * @param name      昵称
     * @param signature 个性签名
     * @param model     视图模型
     * @return 资料设置页视图
     */
    @PostMapping(value = "/profile")
    public String updateProfile(String name, String signature, ModelMap model) {
        Result data;
        AccountProfile profile = getProfile();

        try {
            UserVO user = new UserVO();
            user.setId(profile.getId());
            user.setName(name);
            user.setSignature(signature);

            putProfile(userService.update(user));

            UserVO view = userService.get(profile.getId());
            model.put("view", view);

            data = Result.success();
        } catch (Exception e) {
            data = Result.failure(e.getMessage());
        }
        model.put("data", data);
        return view(Views.SETTINGS_PROFILE);
    }

    /**
     * 提交修改登录密码。
     *
     * @param oldPassword 原密码
     * @param password    新密码
     * @param model       视图模型
     * @return 密码修改页视图
     */
    @PostMapping(value = "/password")
    public String updatePassword(String oldPassword, String password, ModelMap model) {
        Result data;
        try {
            AccountProfile profile = getProfile();
            userService.updatePassword(profile.getId(), oldPassword, password);

            data = Result.success();
        } catch (Exception e) {
            data = Result.failure(e.getMessage());
        }
        model.put("data", data);
        return view(Views.SETTINGS_PASSWORD);
    }

    /**
     * 上传并更新用户头像。
     * <p>
     * 任一步骤失败均以错误信息返回前端展示。
     * </p>
     *
     * @param file 上传的头像文件
     * @return 上传结果（含路径、名称、大小或错误信息）
     * @throws IOException 文件读写异常
     */
    @PostMapping("/avatar")
    @ResponseBody
    public UploadController.UploadResult updateAvatar(@RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        UploadController.UploadResult result = new UploadController.UploadResult();
        AccountProfile profile = getProfile();

        if (null == file || file.isEmpty()) {
            return result.error(UploadController.errorInfo.get("NOFILE"));
        }

        String fileName = file.getOriginalFilename();

        // 类型不合法直接返回，避免恶意文件落盘
        if (!FileKit.checkFileType(fileName)) {
            return result.error(UploadController.errorInfo.get("TYPE"));
        }

        try {
            String ava100 = Consts.avatarPath + getAvaPath(profile.getId(), 240);
            byte[] bytes = ImageUtils.screenshot(file, 240, 240);
            String path = storageFactory.get().writeToStore(bytes, ava100);

            AccountProfile user = userService.updateAvatar(profile.getId(), path);
            putProfile(user);

            result.ok(UploadController.errorInfo.get("SUCCESS"));
            result.setName(fileName);
            result.setPath(path);
            result.setSize(file.getSize());
        } catch (Exception e) {
            // 上传过程异常统一以未知错误返回，避免暴露内部堆栈
            result.error(UploadController.errorInfo.get("UNKNOWN"));
        }
        return result;
    }

    /* *
     * @param uid  用户主键
     * @param size 头像边长（像素）
     */
    private String getAvaPath(long uid, int size) {
        String base = FilePathUtils.getAvatar(uid);
        return String.format("/%s_%d.jpg", base, size);
    }
}
