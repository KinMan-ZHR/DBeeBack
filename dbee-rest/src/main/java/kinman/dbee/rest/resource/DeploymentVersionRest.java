package kinman.dbee.rest.resource;

import kinman.dbee.api.param.app.branch.deploy.DeploymentParam;
import kinman.dbee.api.param.app.branch.deploy.DeploymentVersionDeletionParam;
import kinman.dbee.api.param.app.branch.deploy.DeploymentVersionPageParam;
import kinman.dbee.api.response.PageData;
import kinman.dbee.api.response.RestResponse;
import kinman.dbee.api.response.model.DeploymentVersion;
import kinman.dbee.application.service.DeploymentVersionApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * 部署版本
 */
@RestController
@RequestMapping("/app/deployment/version")
public class DeploymentVersionRest extends AbstractRest {

	@Autowired
	private DeploymentVersionApplicationService deploymentVersionApplicationService;

	/**
	 * 分页查询
	 * 
	 * @param pageParam 分页参数
	 * @return 符合条件的分页数据
	 */
	@PostMapping("/page")
	public RestResponse<PageData<DeploymentVersion>> page(@CookieValue(name = "login_token", required = false) String loginToken,
			@RequestBody DeploymentVersionPageParam pageParam) {
		return success(deploymentVersionApplicationService.page(queryLoginUserByToken(loginToken),
				pageParam));
	}
	
	/**
	 * 搜索
	 * 
	 * @param pageParam 参数
	 * @return 符合条件的数据
	 */
	@PostMapping("/search")
	public RestResponse<PageData<DeploymentVersion>> search(@CookieValue(name = "login_token", required = false) String loginToken,
			@RequestBody DeploymentVersionPageParam pageParam) {
		return success(deploymentVersionApplicationService.search(queryLoginUserByToken(loginToken),
				pageParam));
	}
	
	/**
	 * 删除
	 * 
	 * @param deletionParam 删除参数
	 * @return 无
	 */
	@PostMapping("/delete")
	public RestResponse<Void> delete(@CookieValue(name = "login_token", required = false) String loginToken,
			@RequestBody DeploymentVersionDeletionParam deletionParam) {
		return success(deploymentVersionApplicationService.delete(queryLoginUserByToken(loginToken),
				deletionParam));
	}
	
	/**
	 * 部署
	 * 
	 * @param deploymentParam 提交部署参数
	 * @return 无
	 */
	@RequestMapping("/submitToDeploy")
	public RestResponse<Void> submitToDeploy(@CookieValue(name = "login_token", required = false) String loginToken,
			@RequestBody DeploymentParam deploymentParam) {
		return this.success(deploymentVersionApplicationService
				.submitToDeploy(this.queryLoginUserByToken(loginToken), deploymentParam));
	}
}