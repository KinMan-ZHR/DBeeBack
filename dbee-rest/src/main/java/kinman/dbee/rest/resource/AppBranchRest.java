package kinman.dbee.rest.resource;

import java.util.List;

import kinman.dbee.api.enums.MessageCodeEnum;
import kinman.dbee.api.param.app.branch.AppBranchCreationParam;
import kinman.dbee.api.param.app.branch.AppBranchDeletionParam;
import kinman.dbee.api.param.app.branch.AppBranchListParam;
import kinman.dbee.api.param.app.branch.AppBranchPageParam;
import kinman.dbee.api.param.app.branch.BuildParam;
import kinman.dbee.api.response.PageData;
import kinman.dbee.api.response.RestResponse;
import kinman.dbee.api.response.model.AppBranch;
import kinman.dbee.application.service.AppBranchApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 应用分支
 */
@RestController
@RequestMapping("/app/branch")
public class AppBranchRest extends AbstractRest {

	@Autowired
	private AppBranchApplicationService appBranchApplicationService;

	/**
	 * 分页查询
	 * 
	 * @param appBranchPageParam 分页参数
	 * @return 符合条件的分页数据
	 */
	@PostMapping("/page")
	public RestResponse<PageData<AppBranch>> page(@CookieValue(name = "login_token",
			required = false) String loginToken,
			@RequestBody AppBranchPageParam appBranchPageParam) {
		return success(appBranchApplicationService.page(queryLoginUserByToken(loginToken),
				appBranchPageParam));
	}
	
	/**
	 * 搜索分支
	 * 
	 * @param appBranchListParam 分页参数
	 * @return 符合条件的数据
	 */
	@PostMapping("/search")
	public RestResponse<List<AppBranch>> search(@CookieValue(name = "login_token",
			required = false) String loginToken,
			@RequestBody AppBranchListParam appBranchListParam) {
		return success(appBranchApplicationService.list(queryLoginUserByToken(loginToken),
				appBranchListParam));
	}

	/**
	 * 添加
	 * 
	 * @param appBranchCreationParam 添加应用参数
	 * @return 无
	 */
	@PostMapping("/add")
	public RestResponse<Void> add(@CookieValue(name = "login_token", required = false) String loginToken,
			@RequestBody AppBranchCreationParam appBranchCreationParam) {
		return success(appBranchApplicationService.add(queryLoginUserByToken(loginToken),
				appBranchCreationParam));
	}

	/**
	 * 删除
	 * 
	 * @param appBranchDeletionParam 删除参数
	 * @return 无
	 */
	@PostMapping("/delete")
	public RestResponse<Void> delete(@CookieValue(name = "login_token", required = false) String loginToken,
			@RequestBody AppBranchDeletionParam appBranchDeletionParam) {
		return success(appBranchApplicationService.delete(queryLoginUserByToken(loginToken),
				appBranchDeletionParam));
	}

	/**
	 * 构建版本-集群模式
	 * 
	 * @param buildParam 参数
	 * @return 无
	 */
	@RequestMapping("/buildVersionWithClusterMode")
	public RestResponse<Void> buildVersionWithClusterMode(@CookieValue(name = "login_token",
			required = false) String loginToken,
			@RequestBody BuildParam buildParam) {
		return this.success(appBranchApplicationService
				.asyncBuildVersionWithClusterMode(this.queryLoginUserByToken(loginToken), buildParam));
	}
	
	/**
	 * 构建版本
	 * 
	 * @param buildParam 参数
	 * @return 无
	 */
	@RequestMapping("/buildVersion")
	public RestResponse<Void> buildVersion(@CookieValue(name = "login_token",
			required = false) String loginToken,
			@RequestBody BuildParam buildParam) {
		if(appBranchApplicationService.asyncBuildVersion(
				this.queryLoginUserByToken(loginToken), buildParam)) {
			return success();
		}
		return error(MessageCodeEnum.RESOURCES_NOT_ENOUGH);
		
	}
}