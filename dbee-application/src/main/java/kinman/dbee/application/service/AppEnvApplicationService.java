package kinman.dbee.application.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import kinman.dbee.infrastructure.utils.StringUtils;
import kinman.dbee.api.enums.MessageCodeEnum;
import kinman.dbee.api.enums.PackageFileTypeEnum;
import kinman.dbee.api.enums.TechTypeEnum;
import kinman.dbee.api.enums.YesOrNoEnum;
import kinman.dbee.api.param.app.env.AppEnvCreationParam;
import kinman.dbee.api.param.app.env.AppEnvDeletionParam;
import kinman.dbee.api.param.app.env.AppEnvPageParam;
import kinman.dbee.api.param.app.env.AppEnvQueryParam;
import kinman.dbee.api.param.app.env.AppEnvResoureUpdateParam;
import kinman.dbee.api.param.app.env.AppEnvSearchParam;
import kinman.dbee.api.param.app.env.AppEnvUpdateParam;
import kinman.dbee.api.param.app.env.TraceUpdateParam;
import kinman.dbee.api.response.PageData;
import kinman.dbee.api.response.model.AppEnv;
import kinman.dbee.api.response.model.AppExtendJava;
import kinman.dbee.api.response.model.GlobalConfigAgg.TraceTemplate;
import kinman.dbee.infrastructure.exception.ApplicationException;
import kinman.dbee.infrastructure.param.AppEnvParam;
import kinman.dbee.infrastructure.repository.po.AppEnvPO;
import kinman.dbee.infrastructure.repository.po.AppPO;
import kinman.dbee.infrastructure.repository.po.ClusterPO;
import kinman.dbee.infrastructure.repository.po.DeploymentVersionPO;
import kinman.dbee.infrastructure.repository.po.GlobalConfigPO;
import kinman.dbee.infrastructure.strategy.cluster.ClusterStrategy;
import kinman.dbee.infrastructure.strategy.cluster.model.Replica;
import kinman.dbee.infrastructure.strategy.login.dto.LoginUser;
import kinman.dbee.infrastructure.utils.BeanUtils;
import kinman.dbee.infrastructure.utils.JsonUtils;
import kinman.dbee.infrastructure.utils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 
 * 应用环境应用服务
 */
@Service
public class AppEnvApplicationService extends BaseApplicationService<AppEnv, AppEnvPO> {

	private static final Logger logger = LoggerFactory.getLogger(AppEnvApplicationService.class);
	
	public List<AppEnv> search(LoginUser loginUser, AppEnvSearchParam param) {
		return appEnvRepository.list(loginUser, buildBizParam(param));
	}
	
	public PageData<AppEnv> page(LoginUser loginUser, AppEnvPageParam param) {
		PageData<AppEnv> page = appEnvRepository.page(loginUser, buildBizParam(param));
		if(page.getItemCount() == 0) {
			return page;
		}
		
		Map<String, AppPO> appCache = new HashMap<>();
		Map<String, ClusterPO> clusterCache = new HashMap<>();
		Map<String, DeploymentVersionPO> versionCache = new HashMap<>();
		for(AppEnv env : page.getItems()) {
			AppPO appPO = appCache.get(env.getAppId());
			if(appPO == null) {
				appPO = appRepository.queryById(env.getAppId());
			}
			if(appPO == null) {
				continue;
			}
			ClusterPO clusterPO = clusterCache.get(env.getClusterId());
			if(clusterPO == null) {
				clusterPO = clusterRepository.queryById(env.getClusterId());
			}
			if(clusterPO == null) {
				continue;
			}
			ClusterStrategy clusterStrategy = clusterStrategy(clusterPO.getClusterType());
			Replica replica = null;
			try {
				replica = clusterStrategy.readDeployment(clusterPO, env, appPO);
			}catch(Exception e) {
				logger.error("Failed to read image", e);
			}
			if(replica == null) {
				continue;
			}
			
			if(!StringUtils.isBlank(replica.getImageName())) {
				String versionName = replica.getImageName().substring(replica.getImageName().lastIndexOf("/") + 1);
				env.setVersionName(versionName);
				DeploymentVersionPO deploymentVersionPO = versionCache.get(versionName);
				if(deploymentVersionPO == null) {
					deploymentVersionPO = deploymentVersionRepository.queryByVersionName(versionName);
				}
				env.setBranchName(deploymentVersionPO != null ? deploymentVersionPO.getBranchName() : null);
			}
		}
		
		return page;
	}
	
	public AppEnv query(LoginUser loginUser, AppEnvQueryParam queryParam) {
		if(StringUtils.isBlank(queryParam.getAppId())){
			LogUtils.throwException(logger, MessageCodeEnum.APP_ID_IS_NULL);
		}
		if(StringUtils.isBlank(queryParam.getAppEnvId())){
			LogUtils.throwException(logger, MessageCodeEnum.APP_ENV_ID_IS_EMPTY);
		}
		AppEnvParam param = new AppEnvParam();
		param.setAppId(queryParam.getAppId());
		param.setId(queryParam.getAppEnvId());
		AppEnv appEnv =  appEnvRepository.query(loginUser, param);
		ClusterPO clusterPO = clusterRepository.queryById(appEnv.getClusterId());
		if(clusterPO != null) {
			appEnv.setClusterName(clusterPO.getClusterName());
		}
		if(YesOrNoEnum.YES.getCode().equals(appEnv.getTraceStatus())) {
			GlobalConfigPO globalConfigPO = globalConfigRepository.queryById(appEnv.getTraceTemplateId());
			if(globalConfigPO != null) {
				appEnv.setTraceTemplateName(JsonUtils.parseToObject(globalConfigPO.getItemValue(), TraceTemplate.class).getName());
			}
		}
		return appEnv;
	}
	
	public Void add(AppEnvCreationParam addParam) {
		AppPO appPO = validateAddParam(addParam);
		initAddParam(addParam);
		AppEnvParam param = new AppEnvParam();
		param.setTag(addParam.getTag());
		param.setAppId(addParam.getAppId());
		if(appEnvRepository.query(param) != null) {
			LogUtils.throwException(logger, MessageCodeEnum.APP_ENV_TAG_EXISTENCE);
		}
		AppEnvParam appEnvParam = buildCreationParam(appPO, addParam);
		if(appEnvRepository.add(appEnvParam) == null) {
			LogUtils.throwException(logger, MessageCodeEnum.FAILURE);
		}
		return null;
	}
	
	public Void updateTrace(LoginUser loginUser, TraceUpdateParam updateTraceParam) {
		if(StringUtils.isBlank(updateTraceParam.getAppId())){
			LogUtils.throwException(logger, MessageCodeEnum.APP_ID_IS_NULL);
		}
		if(StringUtils.isBlank(updateTraceParam.getAppEnvId())){
			LogUtils.throwException(logger, MessageCodeEnum.APP_ENV_ID_IS_EMPTY);
		}
		if(Objects.isNull(updateTraceParam.getTraceStatus())){
			LogUtils.throwException(logger, MessageCodeEnum.APP_ENV_TRACE_STATUS_IS_EMPTY);
		}
		if(Objects.isNull(updateTraceParam.getTraceTemplateId())){
			LogUtils.throwException(logger, MessageCodeEnum.ID_IS_EMPTY);
		}
		
		AppEnvParam appEnvParam = new AppEnvParam();
		appEnvParam.setAppId(updateTraceParam.getAppId());
		appEnvParam.setId(updateTraceParam.getAppEnvId());
		appEnvParam.setTraceStatus(updateTraceParam.getTraceStatus());
		appEnvParam.setTraceTemplateId(updateTraceParam.getTraceTemplateId());
		if(!appEnvRepository.update(loginUser, appEnvParam)) {
			LogUtils.throwException(logger, MessageCodeEnum.FAILURE);
		}
		return null;
	}
	
	public Void update(LoginUser loginUser, AppEnvUpdateParam updateParam) {
		AppPO appPO = validateAddParam(updateParam);
		AppEnvParam appEnvParam = buildCreationParam(appPO, updateParam);
		appEnvParam.setId(updateParam.getAppEnvId());
		if(!appEnvRepository.update(loginUser, appEnvParam)) {
			LogUtils.throwException(logger, MessageCodeEnum.FAILURE);
		}
		return null;
	}
	
	public Void updateResource(LoginUser loginUser, AppEnvResoureUpdateParam updateParam) {
		if(updateParam.getAppId() == null) {
			LogUtils.throwException(logger, MessageCodeEnum.APP_ID_IS_NULL);
		}
		if(updateParam.getAppEnvId() == null) {
			LogUtils.throwException(logger, MessageCodeEnum.APP_ENV_ID_IS_EMPTY);
		}
		AppEnvParam appEnvParam = buildBizParam(updateParam);
		appEnvParam.setId(updateParam.getAppEnvId());
		if(!appEnvRepository.update(loginUser, appEnvParam)) {
			LogUtils.throwException(logger, MessageCodeEnum.FAILURE);
		}
		AppPO appPO = appRepository.queryById(updateParam.getAppId());
		AppEnvPO appEnvPO = appEnvRepository.queryById(updateParam.getAppEnvId());
		ClusterPO clusterPO = clusterRepository.queryById(appEnvPO.getClusterId());
		clusterStrategy(clusterPO.getClusterType())
			.autoScaling(appPO, appEnvPO, clusterPO);
		return null;
	}
	
	public Void delete(LoginUser loginUser, AppEnvDeletionParam deleteParam) {
		if(deleteParam.getAppId() == null) {
			LogUtils.throwException(logger, MessageCodeEnum.APP_ID_IS_NULL);
		}
		if(deleteParam.getAppEnvId() == null) {
			LogUtils.throwException(logger, MessageCodeEnum.APP_ENV_ID_IS_EMPTY);
		}
		
		//1.首先判断权限
		hasRights(loginUser, deleteParam.getAppId());
		
		//2.再删除deployment
		AppPO appPO = appRepository.queryById(deleteParam.getAppId());
		AppEnvParam bizParam = new AppEnvParam();
		bizParam.setAppId(deleteParam.getAppId());
		bizParam.setId(deleteParam.getAppEnvId());
		AppEnvPO appEnvPO = appEnvRepository.query(bizParam);
		if(appEnvPO == null) {
			LogUtils.throwException(logger, MessageCodeEnum.APP_ENV_NONEXISTENCE);
		}
		ClusterPO clusterPO = clusterRepository.queryById(appEnvPO.getClusterId());
		if(clusterPO != null) {
			boolean deleteResult = clusterStrategy(clusterPO.getClusterType())
					.deleteDeployment(clusterPO, appPO, appEnvPO);
			if(!deleteResult) {
				LogUtils.throwException(logger, MessageCodeEnum.FAILURE);
			}
		}
		
		//3.最后删除环境信息
		if(!appEnvRepository.delete(deleteParam.getAppEnvId())) {
			LogUtils.throwException(logger, MessageCodeEnum.FAILURE);
		}
		return null;
	}
	
	private AppPO validateAddParam(AppEnvCreationParam addParam) {
		if(StringUtils.isBlank(addParam.getEnvName())){
			LogUtils.throwException(logger, MessageCodeEnum.APP_ENV_NAME_IS_EMPTY);
		}
		if(StringUtils.isBlank(addParam.getTag())){
			LogUtils.throwException(logger, MessageCodeEnum.APP_ENV_TAG_IS_EMPTY);
		}
		if(StringUtils.isBlank(addParam.getAppId())){
			LogUtils.throwException(logger, MessageCodeEnum.APP_ID_IS_NULL);
		}
		if(StringUtils.isBlank(addParam.getClusterId())){
			LogUtils.throwException(logger, MessageCodeEnum.CLUSTER_ID_IS_EMPTY);
		}
		if(StringUtils.isBlank(addParam.getNamespaceName())){
			LogUtils.throwException(logger, MessageCodeEnum.NAMESPACE_NAME_EMPTY);
		}
		if(YesOrNoEnum.YES.getCode().equals(addParam.getTraceStatus())){
			if(StringUtils.isBlank(addParam.getTraceTemplateId())) {
				LogUtils.throwException(logger, MessageCodeEnum.ID_IS_EMPTY);
			}
		}else {
			addParam.setTraceTemplateId(null);
		}
		if(addParam.getEnvName().length() > 16) {
			throw new ApplicationException(MessageCodeEnum.INVALID_PARAM.getCode(), "环境名称不能大于16个字符");
		}
		if(addParam.getTag().length() > 16) {
			throw new ApplicationException(MessageCodeEnum.INVALID_PARAM.getCode(), "环境标识不能大于16个字符");
		}
		if(addParam.getDescription() != null && addParam.getDescription().length() > 128) {
			throw new ApplicationException(MessageCodeEnum.INVALID_PARAM.getCode(), "环境描述不能大于128个字符");
		}
		AppPO appPO = validateApp(addParam.getAppId());
		if(warFileType(appPO) && 8080 != addParam.getServicePort()) {
			LogUtils.throwException(logger, MessageCodeEnum.WAR_APP_SERVICE_PORT_8080);
		}
		return appPO;
	}
	
	private boolean warFileType(AppPO appPO) {
		if(!TechTypeEnum.SPRING_BOOT.getCode().equals(appPO.getTechType())) {
			return false;
		}
		AppExtendJava appExtend = JsonUtils.parseToObject(appPO.getExt(), AppExtendJava.class);
		return PackageFileTypeEnum.WAR.getCode().equals(appExtend.getPackageFileType());
	}
	
	private void initAddParam(AppEnvCreationParam addParam) {
		if(Objects.isNull(addParam.getDeploymentOrder())){
			addParam.setDeploymentOrder(0);
		}
		if(Objects.isNull(addParam.getMinReplicas())){
			addParam.setMinReplicas(1);
		}
		if(Objects.isNull(addParam.getMaxReplicas())){
			addParam.setMaxReplicas(1);
		}
		if(Objects.isNull(addParam.getReplicaCpu())){
			addParam.setReplicaCpu(1000);
		}
		if(Objects.isNull(addParam.getReplicaMemory())){
			addParam.setReplicaMemory(1024);
		}
		if(Objects.isNull(addParam.getAutoScalingCpu())){
			addParam.setAutoScalingCpu(80);
		}
		if(Objects.isNull(addParam.getAutoScalingMemory())){
			addParam.setAutoScalingMemory(80);
		}
		if(Objects.isNull(addParam.getRequiredDeployApproval())){
			addParam.setRequiredDeployApproval(0);
		}
		if(Objects.isNull(addParam.getRequiredMerge())){
			addParam.setRequiredMerge(0);
		}
		if(Objects.isNull(addParam.getTraceStatus())){
			addParam.setTraceStatus(0);
		}
	}
	
	private AppEnvParam buildCreationParam(AppPO appPO, AppEnvCreationParam addParam) {
		AppEnvParam appEnvParam = buildBizParam(addParam);
		if(TechTypeEnum.SPRING_BOOT.getCode().equals(appPO.getTechType())){
			appEnvParam.setExt(JsonUtils.toJsonString(addParam.getExtendSpringBootParam()));
		}else if(TechTypeEnum.VUE.getCode().equals(appPO.getTechType())
				|| TechTypeEnum.REACT.getCode().equals(appPO.getTechType())){
			appEnvParam.setExt(JsonUtils.toJsonString(addParam.getExtendNodeParam()));
		}
		return appEnvParam;
	}
	
	private AppEnvParam buildBizParam(Serializable requestParam) {
		AppEnvParam bizParam = new AppEnvParam();
		BeanUtils.copyProperties(requestParam, bizParam);
		return bizParam;
	}
}
