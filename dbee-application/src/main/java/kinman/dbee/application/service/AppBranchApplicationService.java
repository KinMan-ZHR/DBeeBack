package kinman.dbee.application.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import kinman.dbee.api.enums.GlobalConfigItemTypeEnum;
import kinman.dbee.api.enums.MessageCodeEnum;
import kinman.dbee.api.enums.RoleTypeEnum;
import kinman.dbee.api.param.app.branch.AppBranchCreationParam;
import kinman.dbee.api.param.app.branch.AppBranchDeletionParam;
import kinman.dbee.api.param.app.branch.AppBranchListParam;
import kinman.dbee.api.param.app.branch.AppBranchPageParam;
import kinman.dbee.api.param.app.branch.BuildParam;
import kinman.dbee.api.response.PageData;
import kinman.dbee.api.response.RestResponse;
import kinman.dbee.api.response.model.AppBranch;
import kinman.dbee.api.response.model.GlobalConfigAgg;
import kinman.dbee.infrastructure.exception.ApplicationException;
import kinman.dbee.infrastructure.param.GlobalConfigParam;
import kinman.dbee.infrastructure.repository.po.AppMemberPO;
import kinman.dbee.infrastructure.repository.po.AppPO;
import kinman.dbee.infrastructure.strategy.login.dto.LoginUser;
import kinman.dbee.infrastructure.strategy.repo.param.BranchListParam;
import kinman.dbee.infrastructure.strategy.repo.param.BranchPageParam;
import kinman.dbee.infrastructure.utils.Constants;
import kinman.dbee.infrastructure.utils.HttpUtils;
import kinman.dbee.infrastructure.utils.JsonUtils;
import kinman.dbee.infrastructure.utils.LogUtils;
import kinman.dbee.infrastructure.utils.StringUtils;
import kinman.dbee.infrastructure.utils.ThreadPoolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


/**
 * 
 * 应用分支服务
 */
@Service
public class AppBranchApplicationService extends DeploymentApplicationService {

	private static final Logger logger = LoggerFactory.getLogger(AppBranchApplicationService.class);

	public PageData<AppBranch> page(LoginUser loginUser, AppBranchPageParam pageParam) {
		if (!RoleTypeEnum.ADMIN.getCode().equals(loginUser.getRoleType())) {
			AppMemberPO appMember = appMemberRepository
					.queryByLoginNameAndAppId(loginUser.getLoginName(), pageParam.getAppId());
			if (appMember == null) {
				return zeroPageData(pageParam.getPageSize());
			}
		}
		AppPO appPO = validateApp(pageParam.getAppId());
		GlobalConfigAgg globalConfigAgg = this.globalConfig();
		if(globalConfigAgg.getCodeRepo() == null) {
			return zeroPageData(pageParam.getPageSize());
		}
		BranchPageParam branchPageParam = new BranchPageParam();
		branchPageParam.setPageNum(pageParam.getPageNum());
		branchPageParam.setPageSize(pageParam.getPageSize());
		branchPageParam.setAppIdOrPath(appPO.getCodeRepoPath());
		branchPageParam.setBranchName(pageParam.getBranchName());
		PageData<AppBranch> pageData = buildCodeRepo(globalConfigAgg.getCodeRepo().getType())
				.branchPage(globalConfigAgg.getCodeRepo(), branchPageParam);
		return pageData;
	}

	public List<AppBranch> list(LoginUser loginUser, AppBranchListParam listParam) {
		if (!RoleTypeEnum.ADMIN.getCode().equals(loginUser.getRoleType())) {
			AppMemberPO appMember = appMemberRepository
					.queryByLoginNameAndAppId(loginUser.getLoginName(), listParam.getAppId());
			if (appMember == null) {
				return Collections.emptyList();
			}
		}
		AppPO appPO = validateApp(listParam.getAppId());
		GlobalConfigAgg globalConfigAgg = this.globalConfig();
		BranchListParam branchListParam = new BranchListParam();
		branchListParam.setAppIdOrPath(appPO.getCodeRepoPath());
		branchListParam.setBranchName(listParam.getBranchName());
		return buildCodeRepo(globalConfigAgg.getCodeRepo().getType())
				.branchList(globalConfigAgg.getCodeRepo(), branchListParam);
	}
	
	public Void add(LoginUser loginUser, AppBranchCreationParam addParam) {
		validateAddParam(addParam);
		hasRights(loginUser, addParam.getAppId());
		AppPO appPO = validateApp(addParam.getAppId());
		// 创建仓库分支
		GlobalConfigAgg globalConfigAgg = this.globalConfig();
		buildCodeRepo(globalConfigAgg.getCodeRepo().getType())
			.createBranch(globalConfigAgg.getCodeRepo(),
				appPO.getCodeRepoPath(), addParam.getBranchName(), addParam.getOrgBranchName());
		return null;
	}

	public Void delete(LoginUser loginUser, AppBranchDeletionParam deleteParam) {
		validateAddParam(deleteParam);
		if (!RoleTypeEnum.ADMIN.getCode().equals(loginUser.getRoleType())) {
			AppMemberPO appMember = appMemberRepository
					.queryByLoginNameAndAppId(loginUser.getLoginName(), deleteParam.getAppId());
			if (appMember == null) {
				LogUtils.throwException(logger, MessageCodeEnum.NO_ACCESS_RIGHT);
			}
		}
		AppPO appPO = validateApp(deleteParam.getAppId());
		// 创建仓库分支
		GlobalConfigAgg globalConfigAgg = this.globalConfig();
		buildCodeRepo(globalConfigAgg.getCodeRepo().getType())
			.deleteBranch(globalConfigAgg.getCodeRepo(),
				appPO.getCodeRepoPath(), deleteParam.getBranchName());
		return null;
	}

	private void validateAddParam(AppBranchCreationParam addParam) {
		if (StringUtils.isBlank(addParam.getAppId())) {
			LogUtils.throwException(logger, MessageCodeEnum.APP_ID_IS_NULL);
		}
		if (StringUtils.isBlank(addParam.getBranchName())) {
			LogUtils.throwException(logger, MessageCodeEnum.APP_BRANCH_NAME_IS_EMPTY);
		}
	}

	/**
	 * 向集群提交构建版本请求。<p/>
	 * 轮询集群的各个Server，找出存在可用线程资源的目标Server，并向其提交构建请求。
	 */
	public Void asyncBuildVersionWithClusterMode(LoginUser loginUser, BuildParam buildParam) {
		if (StringUtils.isBlank(buildParam.getAppId())) {
			LogUtils.throwException(logger, MessageCodeEnum.APP_ID_IS_NULL);
		}
		
		hasRights(loginUser, buildParam.getAppId());
		
		GlobalConfigParam globalConfigParam = new GlobalConfigParam();
		globalConfigParam.setItemTypes(Arrays.asList(
				GlobalConfigItemTypeEnum.CODE_REPO.getCode(),
				GlobalConfigItemTypeEnum.IMAGE_REPO.getCode(),
				GlobalConfigItemTypeEnum.SERVER_IP.getCode()));
		GlobalConfigAgg config = globalConfigRepository.queryAgg(globalConfigParam);
		if(config.getCodeRepo() == null || StringUtils.isBlank(config.getCodeRepo().getType())) {
			LogUtils.throwException(logger, MessageCodeEnum.CODE_REPO_IS_EMPTY);
		}
		if(config.getImageRepo() == null || StringUtils.isBlank(config.getImageRepo().getUrl())) {
			LogUtils.throwException(logger, MessageCodeEnum.IMAGE_REPO_IS_EMPTY);
		}
		
		buildParam.setSubmitter(loginUser.getLoginName());
		//1.如果当前server有可用的线程资源，则用当前server进行版本构建
		if(CollectionUtils.isEmpty(config.getServerIps()) || hasUsableThread()) {
			asyncBuildVersion(buildParam);
			return null;
		}
		
		String localServer = Constants.hostIp() + ":" + componentConstants.getServerPort();
		Map<String, Object> cookieParam = Collections.singletonMap("login_token", loginUser.getLastLoginToken());
		String url = "http://%s/app/branch/buildVersion";
		String responseStr = null;
		//2.如果当前server没有可用的线程资源，则轮询集群中的其他server进行构建
		for(String ip : config.getServerIps()) {
			if(ip.equals(localServer)) {
				continue;
			}
			try {
				responseStr = HttpUtils.postResponse(String.format(url, ip), JsonUtils.toJsonString(buildParam), cookieParam);
			}catch(Exception e) {
				logger.error("Failed to build version", e);
				//如果有请求异常，比如请求超时，此时并不能确定有没有真正请求到目标server
				//因此，只要出现异常，统一提示为操作失败，由用户重新提交请求
				throw new ApplicationException(MessageCodeEnum.FAILURE);
			}
			RestResponse<?> response = JsonUtils.parseToObject(responseStr, RestResponse.class);
			if(MessageCodeEnum.SUCCESS.getCode().equals(response.getCode())) {
				return null;
			}
			if(MessageCodeEnum.RESOURCES_NOT_ENOUGH.getCode().equals(response.getCode())) {
				continue;
			}
			throw new ApplicationException(response.getCode(), response.getMessage());
		}
		
		//3.如果集群没有可用的线程资源，则用当前server进行构建
		asyncBuildVersion(buildParam);
		
		return null;
	}
	
	public boolean asyncBuildVersion(LoginUser loginUser, BuildParam buildParam) {
		if(!hasUsableThread()) {
			return false;
		}
		buildParam.setSubmitter(loginUser.getLoginName());
		asyncBuildVersion(buildParam);
		return true;
	}
	
	/**
	 * 如果构建线程池的队列为空，则代表具有可用的线程
	 */
	private boolean hasUsableThread() {
		ThreadPoolExecutor pool = ThreadPoolUtils.getBuildVersionPool();
		return pool.getActiveCount() < pool.getMaximumPoolSize();
	}
}