package com.sunblog.web.controller.admin;

import com.sunblog.base.lang.Result;
import com.sunblog.base.lang.Consts;
import com.sunblog.modules.data.UserVO;
import com.sunblog.modules.entity.Role;
import com.sunblog.modules.service.RoleService;
import com.sunblog.modules.service.UserRoleService;
import com.sunblog.modules.service.UserService;
import com.sunblog.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 后台用户管理控制器
 *
 * <h3>功能</h3>
 * 1. 用户列表（分页 + 按用户名搜索 + 角色信息关联展示）
 * 2. 编辑用户角色授权（update_role）
 * 3. 管理员重置密码（pwd）
 * 4. 启用/禁用账号（open / close）
 *
 * <h3>@RequiresPermissions 为什么被注释掉了？</h3>
 * 项目当前使用后台拦截器统一鉴权（AdminController 级别的权限控制），
 * 没有细化到具体方法。如果未来需要细粒度权限控制，可以取消注释。
 */
@Controller("adminUserController")
@RequestMapping("/admin/user")
public class UserController extends BaseController {
	@Autowired
	private UserService userService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private UserRoleService userRoleService;

	/**
	 * 用户列表页面（支持按用户名筛选）。 *
	 * @param name  用户名筛选关键字
	 * @param model 视图模型
	 * @return 视图名 {@code /admin/user/list}
	 */
	@RequestMapping("/list")
	public String list(String name, ModelMap model) {
		Pageable pageable = wrapPageable();
		Page<UserVO> page = userService.paging(pageable, name);

		List<UserVO> users = page.getContent();
		List<Long> userIds = new ArrayList<>();

		users.forEach(item -> {
			userIds.add(item.getId());
		});

		Map<Long, List<Role>> map = userRoleService.findMapByUserIds(userIds);
		users.forEach(item -> {
			item.setRoles(map.get(item.getId()));
		});

		model.put("name", name);
		model.put("page", page);
		return "/admin/user/list";
	}

	/**
	 * 用户详情 / 编辑页面。
	 *
	 * <p>加载用户信息及其角色列表，并加载所有可选角色供授权选择。</p>
	 *
	 * @param id    用户 ID
	 * @param model 视图模型
	 * @return 视图名 {@code /admin/user/view}
	 */
	@RequestMapping("/view")
	public String view(Long id, ModelMap model) {
		UserVO view = userService.get(id);
		view.setRoles(userRoleService.listRoles(view.getId()));
		model.put("view", view);
		// 加载全部角色供前端授权选择
		model.put("roles", roleService.list());
		return "/admin/user/view";
	}

	/**
	 * 更新用户角色授权。
	 *
	 * <p>将指定用户的角色集合更新为 roleIds，覆盖原有授权。</p>
	 *
	 * @param id      用户 ID
	 * @param roleIds 角色 ID 集合，可为空（清空角色）
	 * @param model   视图模型
	 * @return 重定向到用户列表
	 */
	@PostMapping("/update_role")
//	@RequiresPermissions("user:role")  // 所需权限：用户角色授权
	public String postAuthc(Long id, @RequestParam(value = "roleIds", required=false) Set<Long> roleIds, ModelMap model) {
		userRoleService.updateRole(id, roleIds);
		model.put("data", Result.success());
		// 重定向到列表页，避免重复提交
		return "redirect:/admin/user/list";
	}

	/**
	 * 密码修改页面。
	 *
	 * @param id    用户 ID
	 * @param model 视图模型
	 * @return 视图名 {@code /admin/user/pwd}
	 */
	@RequestMapping(value = "/pwd", method = RequestMethod.GET)
//	@RequiresPermissions("user:pwd")  // 所需权限：用户密码修改
	public String pwsView(Long id, ModelMap model) {
		UserVO ret = userService.get(id);
		model.put("view", ret);
		return "/admin/user/pwd";
	}

	/**
	 * 提交密码修改。
	 *
	 * 其他异常由全局异常处理器处理。</p>
	 *
	 * @param id          用户 ID
	 * @param newPassword 新密码
	 * @param model       视图模型
	 * @return 视图名 {@code /admin/user/pwd}
	 */
	@RequestMapping(value = "/pwd", method = RequestMethod.POST)
//	@RequiresPermissions("user:pwd")  // 所需权限：用户密码修改
	public String pwd(Long id, String newPassword, ModelMap model) {
		UserVO ret = userService.get(id);
		model.put("view", ret);

		try {
			userService.updatePassword(id, newPassword);
			model.put("data", Result.successMessage("修改成功"));
		} catch (IllegalArgumentException e) {
			// 参数校验异常，返回具体错误信息给前端
			model.put("data", Result.failure(e.getMessage()));
		}
		return "/admin/user/pwd";
	}

	/**
	 * 启用用户账号。
	 *
	 * <p>将用户状态置为 {@link Consts#STATUS_NORMAL}，恢复登录权限。</p>
	 *
	 * @param id 用户 ID
	 * @return 操作结果 JSON
	 */
	@RequestMapping("/open")
//	@RequiresPermissions("user:open")  // 所需权限：启用用户
	@ResponseBody
	public Result open(Long id) {
		userService.updateStatus(id, Consts.STATUS_NORMAL);
		return Result.success();
	}

	/**
	 * 禁用用户账号。
	 *
	 * <p>将用户状态置为 {@link Consts#STATUS_CLOSED}，阻止其登录。</p>
	 *
	 * @param id 用户 ID
	 * @return 操作结果 JSON
	 */
	@RequestMapping("/close")
//	@RequiresPermissions("user:close")  // 所需权限：禁用用户
	@ResponseBody
	public Result close(Long id) {
		userService.updateStatus(id, Consts.STATUS_CLOSED);
		return Result.success();
	}
}
