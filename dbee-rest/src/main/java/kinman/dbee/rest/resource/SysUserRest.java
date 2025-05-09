package kinman.dbee.rest.resource;

import java.util.List;

import javax.servlet.http.HttpSession;

import kinman.dbee.api.param.user.PasswordSetParam;
import kinman.dbee.api.param.user.PasswordUpdateParam;
import kinman.dbee.api.param.user.RoleUpdateParam;
import kinman.dbee.api.param.user.UserCreationParam;
import kinman.dbee.api.param.user.UserDeletionParam;
import kinman.dbee.api.param.user.UserLoginParam;
import kinman.dbee.api.param.user.UserPageParam;
import kinman.dbee.api.param.user.UserQueryParam;
import kinman.dbee.api.param.user.UserSearchParam;
import kinman.dbee.api.param.user.UserUpdateParam;
import kinman.dbee.api.response.PageData;
import kinman.dbee.api.response.RestResponse;
import kinman.dbee.api.response.model.SysUser;
import kinman.dbee.infrastructure.annotation.AccessNotLogin;
import kinman.dbee.infrastructure.annotation.AccessOnlyAdmin;
import kinman.dbee.infrastructure.strategy.login.dto.LoginUser;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * 用户
 */
@RestController
@RequestMapping("/sysUser")
public class SysUserRest extends AbstractRest {

	/**
	 * 登录
	 * 
	 * @param userLoginParam 登录参数
	 * @return 登录用户信息
	 */
	@AccessNotLogin
	@PostMapping("/login")
	public RestResponse<LoginUser> login(@RequestBody UserLoginParam userLoginParam) {
		return this.success(sysUserApplicationService.login(userLoginParam));
	}
	
	/**
	 * 登录
	 * 
	 * @param queryLoginUser 登录参数
	 * @return 登录用户信息
	 */
	@PostMapping("/queryLoginUser")
	public RestResponse<LoginUser> login(@CookieValue(name = "login_token", required = false) String loginToken) {
		return this.success(sysUserApplicationService.queryLoginUserByToken(loginToken));
	}

	/**
	 * 退出
	 * 
	 * @param loginToken 登录的token
	 * @return 无
	 */
	@PostMapping("/logout")
	public RestResponse<Void> logout(@CookieValue(name = "login_token", required = false) String loginToken, HttpSession session) {
		session.invalidate();
		return this.success(sysUserApplicationService.logout(this.queryLoginUserByToken(loginToken)));
	}

	/**
	 * 搜索
	 * 
	 * @param usersearchParam 搜索参数
	 * @return 符合条件的用户列表
	 */
	@PostMapping("/search")
	public RestResponse<List<SysUser>> search(@CookieValue(name = "login_token", required = false) String loginToken,
			@RequestBody UserSearchParam usersearchParam) {
		return this.success(sysUserApplicationService.search(this.queryLoginUserByToken(loginToken), usersearchParam));
	}

	/**
	 * 分页查询
	 * 
	 * @param sysUserParam 查询参数
	 * @return 符合条件的用户分页数据
	 */
	@AccessOnlyAdmin
	@PostMapping("/page")
	public RestResponse<PageData<SysUser>> page(@RequestBody UserPageParam userPageParam) {
		return this.success(sysUserApplicationService.page(userPageParam));
	}

	/**
	 * 单个查询
	 * 
	 * @param sysUserParam 查询参数
	 * @return 符合条件的用户
	 */
	@AccessOnlyAdmin
	@PostMapping("/query")
	public RestResponse<SysUser> query(@RequestBody UserQueryParam userQueryParam) {
		return this.success(sysUserApplicationService.query(userQueryParam));
	}

	/**
	 * 创建
	 * 
	 * @param sysUserParam 创建参数
	 * @return 无
	 */
	@AccessOnlyAdmin
	@PostMapping("/createUser")
	public RestResponse<Void> createUser(@RequestBody UserCreationParam userCreationParam) {
		return this.success(sysUserApplicationService.createUser(userCreationParam));
	}

	/**
	 * 修改
	 * 
	 * @param sysUserParam 修改用户参数
	 * @return 无
	 */
	@AccessOnlyAdmin
	@PostMapping("/updateUser")
	public RestResponse<Void> updateUser(@RequestBody UserUpdateParam userUpdateParam) {
		return this.success(sysUserApplicationService.updateUser(userUpdateParam));
	}

	/**
	 * 删除
	 * 
	 * @param sysUserParam 删除参数
	 * @return 无
	 */
	@AccessOnlyAdmin
	@PostMapping("/deleteUser")
	public RestResponse<Void> deleteUser(@RequestBody UserDeletionParam userDeletionParam) {
		return this.success(sysUserApplicationService.deleteUser(userDeletionParam));
	}

	/**
	 * 修改密码
	 * 
	 * @param loginToken   登录token
	 * @param sysUserParam 密码参数
	 * @return 无
	 */
	@PostMapping("/updatePassword")
	public RestResponse<Void> updatePassword(@CookieValue(name = "login_token", required = false) String loginToken,
			@RequestBody PasswordUpdateParam passwordUpdateParam) {
		return this.success(
				sysUserApplicationService.updatePassword(this.queryLoginUserByToken(loginToken), passwordUpdateParam));
	}

	/**
	 * 重置登录密码
	 * <p>
	 * 只有管理员角色才有权限。
	 * 
	 * @param passwordSetParam 密码参数
	 * @return 无
	 */
	@AccessOnlyAdmin
	@PostMapping("/setPassword")
	public RestResponse<Void> setPassword(@RequestBody PasswordSetParam passwordSetParam) {
		return this.success(sysUserApplicationService.setPassword(passwordSetParam));
	}

	/**
	 * 修改角色类型
	 * 
	 * @param roleUpdateParam 参数模型
	 * @return 无
	 */
	@AccessOnlyAdmin
	@PostMapping("/updateUserRole")
	public RestResponse<Void> updateUserRole(@RequestBody RoleUpdateParam roleUpdateParam) {
		return this.success(sysUserApplicationService.updateUserRole(roleUpdateParam));
	}

}
