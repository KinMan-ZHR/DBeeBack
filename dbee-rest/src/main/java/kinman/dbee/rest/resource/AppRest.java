package kinman.dbee.rest.resource;

import java.util.List;

import kinman.dbee.api.param.app.AppCreationParam;
import kinman.dbee.api.param.app.AppDeletionParam;
import kinman.dbee.api.param.app.AppPageParam;
import kinman.dbee.api.param.app.AppQueryParam;
import kinman.dbee.api.param.app.AppUpdateParam;
import kinman.dbee.api.response.PageData;
import kinman.dbee.api.response.RestResponse;
import kinman.dbee.api.response.model.App;
import kinman.dbee.application.service.AppApplicationService;
import kinman.dbee.infrastructure.annotation.AccessNotLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * 应用
 * 
 * @author Dahai
 */
@RestController
@RequestMapping("/app")
public class AppRest extends AbstractRest {

	@Autowired
	private AppApplicationService appApplicationService;

	/**
	 * 分页查询
	 * 
	 * @param appPageParam 查询参数
	 * @return 符合条件的分页数据
	 */
	@AccessNotLogin
	@PostMapping("/page")
	public RestResponse<PageData<App>> page(@CookieValue(name = "login_token", required = false) String loginToken,
			@RequestBody AppPageParam appPageParam) {
		return success(appApplicationService.page(queryLoginUserByToken(loginToken), appPageParam));
	}

	/**
	 * 搜索
	 * 
	 * @param searchParam 搜索参数
	 * @return 符合条件的数据
	 */
	@AccessNotLogin
	@PostMapping("/search")
	public RestResponse<List<App>> search(@RequestBody AppPageParam searchParam) {
		return success(appApplicationService.search(searchParam));
	}

	/**
	 * 查询详情
	 * 
	 * @param appQueryParam 查询参数
	 * @return 符合条件的数据
	 */
	@PostMapping("/query")
	public RestResponse<App> query(@CookieValue(name = "login_token", required = false) String loginToken,
			@RequestBody AppQueryParam appQueryParam) {
		return success(appApplicationService.query(queryLoginUserByToken(loginToken), appQueryParam.getAppId()));
	}

	/**
	 * 添加
	 * 
	 * @param appCreationParam 添加应用参数
	 * @return 应用编号
	 */
	@PostMapping("/add")
	public RestResponse<App> add(@CookieValue(name = "login_token", required = false) String loginToken,
			@RequestBody AppCreationParam appCreationParam) {
		return success(appApplicationService.add(queryLoginUserByToken(loginToken), appCreationParam));
	}

	/**
	 * 修改
	 * 
	 * @param appUpdateParam 修改应用参数
	 * @return 无
	 */
	@PostMapping("/update")
	public RestResponse<Void> update(@CookieValue(name = "login_token", required = false) String loginToken,
			@RequestBody AppUpdateParam appUpdateParam) {
		return success(appApplicationService.update(queryLoginUserByToken(loginToken), appUpdateParam));
	}

	/**
	 * 删除
	 * 
	 * @param appDeletionParam 删除应用参数
	 * @return 无
	 */
	@PostMapping("/delete")
	public RestResponse<Void> delete(@CookieValue(name = "login_token", required = false) String loginToken,
			@RequestBody AppDeletionParam appDeletionParam) {
		return success(appApplicationService.delete(queryLoginUserByToken(loginToken), appDeletionParam));
	}
}