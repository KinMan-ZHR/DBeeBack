package kinman.dbee.application.service;

import java.io.Serializable;

import kinman.dbee.infrastructure.utils.StringUtils;
import kinman.dbee.api.enums.MessageCodeEnum;
import kinman.dbee.api.param.app.env.affinity.AffinityTolerationCreationParam;
import kinman.dbee.api.param.app.env.affinity.AffinityTolerationDeletionParam;
import kinman.dbee.api.param.app.env.affinity.AffinityTolerationPageParam;
import kinman.dbee.api.param.app.env.affinity.AffinityTolerationQueryParam;
import kinman.dbee.api.param.app.env.affinity.AffinityTolerationUpdateParam;
import kinman.dbee.api.response.PageData;
import kinman.dbee.api.response.model.AffinityToleration;
import kinman.dbee.infrastructure.exception.ApplicationException;
import kinman.dbee.infrastructure.param.AffinityTolerationParam;
import kinman.dbee.infrastructure.repository.po.AffinityTolerationPO;
import kinman.dbee.infrastructure.strategy.login.dto.LoginUser;
import kinman.dbee.infrastructure.utils.BeanUtils;
import kinman.dbee.infrastructure.utils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 亲和容忍配置应用服务
 */
@Service
public class AffinityTolerationApplicationService
		extends BaseApplicationService<AffinityToleration, AffinityTolerationPO> {

	private static final Logger logger = LoggerFactory.getLogger(AffinityTolerationApplicationService.class);

	public PageData<AffinityToleration> page(LoginUser loginUser, AffinityTolerationPageParam param) {
		return affinityTolerationRepository.page(loginUser, buildBizParam(param));
	}
	
	public AffinityToleration query(LoginUser loginUser, AffinityTolerationQueryParam param) {
		AffinityTolerationParam bizParam = buildBizParam(param);
		bizParam.setId(param.getAffinityTolerationId());
		return affinityTolerationRepository.query(loginUser, bizParam);
	}

	public Void add(AffinityTolerationCreationParam addParam) {
		validateAddParam(addParam);
		AffinityTolerationParam bizParam = buildBizParam(addParam);
		if (affinityTolerationRepository.add(bizParam) == null) {
			LogUtils.throwException(logger, MessageCodeEnum.FAILURE);
		}
		return null;
	}

	public Void update(LoginUser loginUser, AffinityTolerationUpdateParam updateParam) {
		validateAddParam(updateParam);
		AffinityTolerationParam bizParam = buildBizParam(updateParam);
		bizParam.setId(updateParam.getAffinityTolerationId());
		if (!affinityTolerationRepository.update(bizParam)) {
			LogUtils.throwException(logger, MessageCodeEnum.FAILURE);
		}
		return null;
	}

	public Void openStatus(LoginUser loginUser, AffinityTolerationUpdateParam updateParam) {
		AffinityTolerationParam bizParam = buildBizParam(updateParam);
		bizParam.setId(updateParam.getAffinityTolerationId());
		if (!affinityTolerationRepository.update(bizParam)) {
			LogUtils.throwException(logger, MessageCodeEnum.FAILURE);
		}
		return null;
	}
	
	public Void delete(LoginUser loginUser, AffinityTolerationDeletionParam deleteParam) {
		if (StringUtils.isBlank(deleteParam.getAffinityTolerationId())) {
			throw new ApplicationException(MessageCodeEnum.INVALID_PARAM.getCode(), "亲和容忍配置编号不能为空");
		}
		AffinityTolerationParam bizParam = new AffinityTolerationParam();
		bizParam.setId(deleteParam.getAffinityTolerationId());
		bizParam.setAppId(deleteParam.getAppId());
		bizParam.setEnvId(deleteParam.getEnvId());
		if (!affinityTolerationRepository.delete(loginUser, bizParam)) {
			LogUtils.throwException(logger, MessageCodeEnum.FAILURE);
		}
		return null;
	}

	private void validateAddParam(AffinityTolerationCreationParam addParam) {
		if (StringUtils.isBlank(addParam.getEnvId())) {
			LogUtils.throwException(logger, MessageCodeEnum.APP_ENV_TAG_IS_EMPTY);
		}
		if (StringUtils.isBlank(addParam.getAppId())) {
			LogUtils.throwException(logger, MessageCodeEnum.APP_ID_IS_NULL);
		}
		if (addParam.getSchedulingType() == null) {
			throw new ApplicationException(MessageCodeEnum.INVALID_PARAM.getCode(), "调度类型不能为空");
		}
		if (StringUtils.isBlank(addParam.getKeyName())) {
			throw new ApplicationException(MessageCodeEnum.INVALID_PARAM.getCode(), "键不能为空");
		}
		if (StringUtils.isBlank(addParam.getOperator())) {
			throw new ApplicationException(MessageCodeEnum.INVALID_PARAM.getCode(), "操作符不能为空");
		}
	}

	private AffinityTolerationParam buildBizParam(Serializable requestParam) {
		AffinityTolerationParam bizParam = new AffinityTolerationParam();
		BeanUtils.copyProperties(requestParam, bizParam);
		return bizParam;
	}
}
