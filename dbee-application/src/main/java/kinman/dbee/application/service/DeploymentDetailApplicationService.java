package kinman.dbee.application.service;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import kinman.dbee.infrastructure.utils.StringUtils;
import kinman.dbee.api.enums.AppMemberRoleTypeEnum;
import kinman.dbee.api.enums.DeploymentStatusEnum;
import kinman.dbee.api.enums.MessageCodeEnum;
import kinman.dbee.api.enums.RoleTypeEnum;
import kinman.dbee.api.enums.YesOrNoEnum;
import kinman.dbee.api.param.app.branch.deploy.DeploymentApprovalParam;
import kinman.dbee.api.param.app.branch.deploy.DeploymentDetailDeletionParam;
import kinman.dbee.api.param.app.branch.deploy.DeploymentDetailPageParam;
import kinman.dbee.api.param.app.branch.deploy.RollbackApplicationParam;
import kinman.dbee.api.response.PageData;
import kinman.dbee.api.response.model.AppEnv;
import kinman.dbee.api.response.model.DeploymentDetail;
import kinman.dbee.infrastructure.param.DeployParam;
import kinman.dbee.infrastructure.param.DeploymentDetailParam;
import kinman.dbee.infrastructure.repository.po.ClusterPO;
import kinman.dbee.infrastructure.repository.po.DeploymentDetailPO;
import kinman.dbee.infrastructure.repository.po.AppEnvPO;
import kinman.dbee.infrastructure.repository.po.AppMemberPO;
import kinman.dbee.infrastructure.repository.po.AppPO;
import kinman.dbee.infrastructure.strategy.cluster.ClusterStrategy;
import kinman.dbee.infrastructure.strategy.cluster.model.Replica;
import kinman.dbee.infrastructure.strategy.login.dto.LoginUser;
import kinman.dbee.infrastructure.utils.BeanUtils;
import kinman.dbee.infrastructure.utils.Constants;
import kinman.dbee.infrastructure.utils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * 
 * 部署历史应用服务
 */
@Service
public class DeploymentDetailApplicationService extends DeploymentApplicationService {

	private static final Logger logger = LoggerFactory.getLogger(DeploymentDetailApplicationService.class);

	public PageData<DeploymentDetail> page(LoginUser loginUser, DeploymentDetailPageParam param) {
		PageData<DeploymentDetail> page = deploymentDetailRepository.page(loginUser, buildBizParam(param));
		if(CollectionUtils.isEmpty(page.getItems())) {
			return page;
		}
		
		AppEnvPO appEnvPO = appEnvRepository.queryById(param.getEnvId());
		if (appEnvPO == null) {
			return page;
		}
		
		AppMemberPO appUser = appMemberRepository
				.queryByLoginNameAndAppId(loginUser.getLoginName(), param.getAppId());
		Set<Integer> roleSet = new HashSet<>();
		if(RoleTypeEnum.ADMIN.getCode().equals(loginUser.getRoleType())) {
			roleSet.add(AppMemberRoleTypeEnum.ADMIN.getCode());
		}else {
			String[] roleTypes = appUser.getRoleType().split(",");
			for (String role : roleTypes) {
				roleSet.add(Integer.valueOf(role));
			}
		}
		
		String currentVersionName = currentVersionName(appEnvPO);
		for(DeploymentDetail e : page.getItems()) {
			e.setEnvName(appEnvPO.getEnvName());
			//审批权限
			if(RoleTypeEnum.ADMIN.getCode().equals(loginUser.getRoleType())) {
				e.setDeployApprovalRights(YesOrNoEnum.YES.getCode());
				e.setRollbackApprovalRights(YesOrNoEnum.YES.getCode());
			} else {
				List<Integer> adminRole = Constants.ROLE_OF_OPERATE_APP_USER.stream()
						.filter(item -> roleSet.contains(item))
						.collect(Collectors.toList());
				if(adminRole.size() > 0) {
					e.setDeployApprovalRights(YesOrNoEnum.YES.getCode());
					e.setRollbackApprovalRights(YesOrNoEnum.YES.getCode());
				}
			}
			if(!DeploymentStatusEnum.DEPLOYING_APPROVAL.getCode().equals(e.getDeploymentStatus())) {
				e.setDeployApprovalRights(YesOrNoEnum.NO.getCode());
			}
			if(!DeploymentStatusEnum.ROLLBACK_APPROVAL.getCode().equals(e.getDeploymentStatus())) {
				e.setRollbackApprovalRights(YesOrNoEnum.NO.getCode());
			}
			e.setRollbackRights(DeploymentStatusEnum.ROLLBACK_FAILURE.getCode().equals(e.getDeploymentStatus())
					|| (canRollback(e.getDeploymentStatus()) && !e.getVersionName().equals(currentVersionName)) ? 1 : 0);
		}
		return page;
	}
	
	private boolean canRollback(Integer deploymentStatus) {
		boolean show = DeploymentStatusEnum.DEPLOYED_SUCCESS.getCode().equals(deploymentStatus)
				|| DeploymentStatusEnum.MERGED_SUCCESS.getCode().equals(deploymentStatus)
				|| DeploymentStatusEnum.MERGED_FAILURE.getCode().equals(deploymentStatus);
		return show;
	}

	private String currentVersionName(AppEnvPO appEnvPO) {
		AppPO appPO = appRepository.queryById(appEnvPO.getAppId());
		ClusterPO clusterPO = clusterRepository.queryById(appEnvPO.getClusterId());
		ClusterStrategy clusterStrategy = clusterStrategy(clusterPO.getClusterType());
		AppEnv appEnv = new AppEnv();
		appEnv.setNamespaceName(appEnvPO.getNamespaceName());
		appEnv.setTag(appEnvPO.getTag());
		Replica replica = null;
		try {
			replica = clusterStrategy.readDeployment(clusterPO, appEnv, appPO);
		}catch(Exception e) {
			logger.error("Failed to read image", e);
			return null;
		}
		if(replica == null) {
			return null;
		}
		String iamgeName = replica.getImageName();
		if(iamgeName == null) {
			return null;
		}
		
		return iamgeName.substring(iamgeName.lastIndexOf("/") + 1);
	}
	
	public Void approveToDeploy(LoginUser loginUser, DeploymentApprovalParam requestParam) {
		DeploymentDetailPO deploymentDetailPO = deployParamValidate(loginUser, requestParam);
		if (!DeploymentStatusEnum.DEPLOYING_APPROVAL.getCode().equals(deploymentDetailPO.getDeploymentStatus())
				&& !DeploymentStatusEnum.ROLLBACK_APPROVAL.getCode().equals(deploymentDetailPO.getDeploymentStatus())) {
			LogUtils.throwException(logger, MessageCodeEnum.DEPLOYING_BRANCH_IS_APPROVED);
		}
		DeployParam deployParam = new DeployParam();
		deployParam.setDeployer(deploymentDetailPO.getDeployer());
		deployParam.setApprover(loginUser.getLoginName());
		deployParam.setVersionName(deploymentDetailPO.getVersionName());
		deployParam.setBranchName(deploymentDetailPO.getBranchName());
		deployParam.setEnvId(deploymentDetailPO.getEnvId());
		deployParam.setDeploymentDetailId(deploymentDetailPO.getId());
		deployParam.setDeploymentStartTime(new Date());
		if(DeploymentStatusEnum.DEPLOYING_APPROVAL.getCode().equals(deploymentDetailPO.getDeploymentStatus())) {
			deploy(deployParam);
		}else if(DeploymentStatusEnum.ROLLBACK_APPROVAL.getCode().equals(deploymentDetailPO.getDeploymentStatus())) {
			rollback(deployParam);
		}
		
		return null;
	}

	public Void submitToRollback(LoginUser loginUser, RollbackApplicationParam requestParam) {
		DeploymentDetailPO deploymentDetailPO = deployParamValidate(loginUser, requestParam);
		if (!canRollback(deploymentDetailPO.getDeploymentStatus())
				&& !DeploymentStatusEnum.ROLLBACK_FAILURE.getCode().equals(deploymentDetailPO.getDeploymentStatus())) {
			LogUtils.throwException(logger, MessageCodeEnum.DEPLOYED_STATUS_NOT_ROLLBACK);
		}
		DeploymentDetailParam deploymentDetailParam = new DeploymentDetailParam();
		deploymentDetailParam.setId(deploymentDetailPO.getId());
		deploymentDetailParam.setDeployer(loginUser.getLoginName());
		// 需要审核
		AppEnvPO appEnv = appEnvRepository.queryById(deploymentDetailPO.getEnvId());
		if (YesOrNoEnum.YES.getCode().equals(appEnv.getRequiredDeployApproval())) {
			deploymentDetailParam.setDeploymentStatus(DeploymentStatusEnum.ROLLBACK_APPROVAL.getCode());
			deploymentDetailRepository.update(deploymentDetailParam);
			LogUtils.throwException(logger, MessageCodeEnum.APPROVE);
		}
		DeployParam deployParam = new DeployParam();
		deployParam.setApprover(loginUser.getLoginName());
		deployParam.setVersionName(deploymentDetailPO.getVersionName());
		deployParam.setBranchName(deploymentDetailPO.getBranchName());
		deployParam.setEnvId(deploymentDetailPO.getEnvId());
		deployParam.setDeploymentDetailId(deploymentDetailPO.getId());
		deployParam.setDeploymentStartTime(new Date());
		
		// 回滚
		rollback(deployParam);

		return null;
	}

	private DeploymentDetailPO deployParamValidate(LoginUser loginUser, DeploymentApprovalParam requestParam) {
		if (StringUtils.isBlank(requestParam.getAppId())) {
			LogUtils.throwException(logger, MessageCodeEnum.APP_ID_IS_NULL);
		}
		if (StringUtils.isBlank(requestParam.getDeploymentDetailId())) {
			LogUtils.throwException(logger, MessageCodeEnum.BRANCH_DEPLOYED_DETAIL_ID_IS_EMPTY);
		}
		hasRights(loginUser, requestParam.getAppId());
		DeploymentDetailParam deploymentDetailParam = new DeploymentDetailParam();
		deploymentDetailParam.setAppId(requestParam.getAppId());
		deploymentDetailParam.setId(requestParam.getDeploymentDetailId());
		DeploymentDetailPO deploymentDetailPO = deploymentDetailRepository.query(deploymentDetailParam);
		if (deploymentDetailPO == null) {
			LogUtils.throwException(logger, MessageCodeEnum.RECORD_IS_NONEXISTENCE);
		}
		return deploymentDetailPO;
	}

	public Void delete(LoginUser loginUser, DeploymentDetailDeletionParam requestParam) {
		DeploymentDetailPO deploymentDetailPO = deployParamValidate(loginUser, requestParam);
		deploymentDetailRepository.delete(deploymentDetailPO.getId());
		return null;
	}

	private DeploymentDetailParam buildBizParam(Serializable requestParam) {
		DeploymentDetailParam bizParam = new DeploymentDetailParam();
		BeanUtils.copyProperties(requestParam, bizParam);
		return bizParam;
	}
}