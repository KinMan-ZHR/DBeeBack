package kinman.dbee.rest.resource;

import kinman.dbee.api.param.app.branch.deploy.AbortDeploymentParam;
import kinman.dbee.api.param.app.branch.deploy.AbortDeploymentThreadParam;
import kinman.dbee.api.param.app.branch.deploy.DeploymentApprovalParam;
import kinman.dbee.api.param.app.branch.deploy.DeploymentDetailDeletionParam;
import kinman.dbee.api.param.app.branch.deploy.DeploymentDetailPageParam;
import kinman.dbee.api.param.app.branch.deploy.RollbackApplicationParam;
import kinman.dbee.api.response.PageData;
import kinman.dbee.api.response.RestResponse;
import kinman.dbee.api.response.model.DeploymentDetail;
import kinman.dbee.application.service.DeploymentDetailApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * 部署历史
 */
@RestController
@RequestMapping("/app/deployment/detail")
public class DeploymentDetailRest extends AbstractRest {

	@Autowired
	private DeploymentDetailApplicationService deploymentDetailApplicationService;

	/**
	 * 分页查询
	 * 
	 * @param deploymentDetailPageParam 分页参数
	 * @return 符合条件的分页数据
	 */
	@PostMapping("/page")
	public RestResponse<PageData<DeploymentDetail>> page(@CookieValue(name = "login_token", required = false) String loginToken,
			@RequestBody DeploymentDetailPageParam deploymentDetailPageParam) {
		return success(deploymentDetailApplicationService.page(queryLoginUserByToken(loginToken),
				deploymentDetailPageParam));
	}

	/**
	 * 审批部署
	 * 
	 * @param deploymentApplicationParam 审批参数
	 * @return 审批后的明细编号
	 */
	@RequestMapping("/approveToDeploy")
	public RestResponse<Void> approveToDeploy(@CookieValue(name = "login_token", required = false) String loginToken,
			@RequestBody DeploymentApprovalParam deploymentApplicationParam) {
		return this.success(deploymentDetailApplicationService
				.approveToDeploy(queryLoginUserByToken(loginToken), deploymentApplicationParam));
	}

	/**
	 * 提交回滚
	 * 
	 * @param requestParam 提交参数
	 * @return 提交回滚的参数
	 */
	@RequestMapping("/submitToRollback")
	public RestResponse<Void> submitToRollback(@CookieValue(name = "login_token", required = false) String loginToken,
			@RequestBody RollbackApplicationParam requestParam) {
		return this.success(deploymentDetailApplicationService
				.submitToRollback(queryLoginUserByToken(loginToken), requestParam));
	}

	/**
	 * 终止部署
	 * 
	 * @param abortParam 终止参数
	 * @return 无
	 */
	@RequestMapping("/abortDeployment")
	public RestResponse<Void> abortDeployment(@CookieValue(name = "login_token", required = false) String loginToken,
			@RequestBody AbortDeploymentParam abortParam) {
		return this.success(
				deploymentDetailApplicationService.abortDeployment(queryLoginUserByToken(loginToken), abortParam));
	}
	
	/**
	 * 终止部署线程
	 * 
	 * @param abortParam 终止参数
	 * @return 无
	 */
	@PostMapping("/abortDeploymentThread")
	public RestResponse<Void> abortDeploymentThread(@CookieValue(name = "login_token", required = false) String loginToken,
			@RequestBody AbortDeploymentThreadParam abortParam) {
		return this.success(
				deploymentDetailApplicationService.abortDeploymentThread(queryLoginUserByToken(loginToken), abortParam));
	}
	
	/**
	 * 删除
	 * 
	 * @param requestParam 删除参数
	 * @return 无
	 */
	@RequestMapping("/delete")
	public RestResponse<Void> delete(@CookieValue(name = "login_token", required = false) String loginToken,
			@RequestBody DeploymentDetailDeletionParam requestParam) {
		return this.success(
				deploymentDetailApplicationService.delete(queryLoginUserByToken(loginToken), requestParam));
	}
}