package kinman.dbee.application.service;

import java.util.Collections;
import java.util.List;

import kinman.dbee.api.enums.MessageCodeEnum;
import kinman.dbee.api.enums.RoleTypeEnum;
import kinman.dbee.api.param.app.branch.AppTagListParam;
import kinman.dbee.api.param.app.tag.AppTagCreationParam;
import kinman.dbee.api.param.app.tag.AppTagDeletionParam;
import kinman.dbee.api.param.app.tag.AppTagPageParam;
import kinman.dbee.api.response.PageData;
import kinman.dbee.api.response.model.AppTag;
import kinman.dbee.api.response.model.GlobalConfigAgg;
import kinman.dbee.infrastructure.repository.po.AppMemberPO;
import kinman.dbee.infrastructure.repository.po.AppPO;
import kinman.dbee.infrastructure.strategy.login.dto.LoginUser;
import kinman.dbee.infrastructure.strategy.repo.param.BranchPageParam;
import kinman.dbee.infrastructure.strategy.repo.param.TagListParam;
import kinman.dbee.infrastructure.utils.LogUtils;
import kinman.dbee.infrastructure.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 
 * 应用标签服务
 */
@Service
public class AppTagApplicationService extends DeploymentApplicationService {

	private static final Logger logger = LoggerFactory.getLogger(AppTagApplicationService.class);

	public PageData<AppTag> page(LoginUser loginUser, AppTagPageParam pageParam) {
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
		branchPageParam.setBranchName(pageParam.getTagName());
		PageData<AppTag> pageData = buildCodeRepo(globalConfigAgg.getCodeRepo().getType())
				.tagPage(globalConfigAgg.getCodeRepo(), branchPageParam);
		return pageData;
	}

	public List<AppTag> list(LoginUser loginUser, AppTagListParam listParam) {
		if (!RoleTypeEnum.ADMIN.getCode().equals(loginUser.getRoleType())) {
			AppMemberPO appMember = appMemberRepository
					.queryByLoginNameAndAppId(loginUser.getLoginName(), listParam.getAppId());
			if (appMember == null) {
				return Collections.emptyList();
			}
		}
		AppPO appPO = validateApp(listParam.getAppId());
		GlobalConfigAgg globalConfigAgg = this.globalConfig();
		TagListParam tagListParam = new TagListParam();
		tagListParam.setAppIdOrPath(appPO.getCodeRepoPath());
		tagListParam.setTagName(listParam.getTagName());
		return buildCodeRepo(globalConfigAgg.getCodeRepo().getType())
				.tagList(globalConfigAgg.getCodeRepo(), tagListParam);
	}
	
	public Void add(LoginUser loginUser, AppTagCreationParam addParam) {
		validateAddParam(addParam);
		if (StringUtils.isBlank(addParam.getOrgBranchName())) {
			LogUtils.throwException(logger, MessageCodeEnum.APP_BRANCH_NAME_IS_EMPTY);
		}
		hasRights(loginUser, addParam.getAppId());
		AppPO appPO = validateApp(addParam.getAppId());
		// 创建仓库分支
		GlobalConfigAgg globalConfigAgg = this.globalConfig();
		buildCodeRepo(globalConfigAgg.getCodeRepo().getType())
			.createTag(globalConfigAgg.getCodeRepo(),
				appPO.getCodeRepoPath(), addParam.getTagName(), addParam.getOrgBranchName());
		return null;
	}

	public Void delete(LoginUser loginUser, AppTagDeletionParam deleteParam) {
		validateAddParam(deleteParam);
		if (!RoleTypeEnum.ADMIN.getCode().equals(loginUser.getRoleType())) {
			AppMemberPO appMember = appMemberRepository
					.queryByLoginNameAndAppId(loginUser.getLoginName(), deleteParam.getAppId());
			if (appMember == null) {
				LogUtils.throwException(logger, MessageCodeEnum.NO_ACCESS_RIGHT);
			}
		}
		AppPO appPO = validateApp(deleteParam.getAppId());
		GlobalConfigAgg globalConfigAgg = this.globalConfig();
		buildCodeRepo(globalConfigAgg.getCodeRepo().getType())
			.deleteTag(globalConfigAgg.getCodeRepo(),
				appPO.getCodeRepoPath(), deleteParam.getTagName());
		return null;
	}

	private void validateAddParam(AppTagCreationParam addParam) {
		if (StringUtils.isBlank(addParam.getAppId())) {
			LogUtils.throwException(logger, MessageCodeEnum.APP_ID_IS_NULL);
		}
		if (StringUtils.isBlank(addParam.getTagName())) {
			LogUtils.throwException(logger, MessageCodeEnum.APP_TAG_NAME_IS_EMPTY);
		}
	}
}