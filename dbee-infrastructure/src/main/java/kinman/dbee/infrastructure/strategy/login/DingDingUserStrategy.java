package kinman.dbee.infrastructure.strategy.login;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kinman.dbee.api.enums.MessageCodeEnum;
import kinman.dbee.api.enums.RegisteredSourceEnum;
import kinman.dbee.api.enums.RoleTypeEnum;
import kinman.dbee.api.param.user.UserLoginParam;
import kinman.dbee.api.response.model.GlobalConfigAgg;
import kinman.dbee.api.response.model.GlobalConfigAgg.DingDing;
import kinman.dbee.api.response.model.SysUser;
import kinman.dbee.infrastructure.component.SpringBeanContext;
import kinman.dbee.infrastructure.strategy.login.dto.LoginUser;
import kinman.dbee.infrastructure.param.SysUserParam;
import kinman.dbee.infrastructure.repository.SysUserRepository;
import kinman.dbee.infrastructure.repository.po.SysUserPO;
import kinman.dbee.infrastructure.utils.HttpUtils;
import kinman.dbee.infrastructure.utils.JsonUtils;
import kinman.dbee.infrastructure.utils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.databind.JsonNode;

public class DingDingUserStrategy extends UserStrategy {

	private static final Logger logger = LoggerFactory.getLogger(DingDingUserStrategy.class);
	
	@Override
	public LoginUser login(UserLoginParam userLoginParam, GlobalConfigAgg globalConfig, PasswordEncoder passwordEncoder) {
		DingDing dingding = globalConfig.getDingding();
		//获取accessToken
		String url = "https://api.dingtalk.com/v1.0/oauth2/userAccessToken";
		Map<String, Object> param = new HashMap<>();
		param.put("clientId", dingding.getAppKey());
		param.put("clientSecret", dingding.getAppSecret());
		param.put("code", userLoginParam.getCode());
		param.put("grantType", "authorization_code");
		JsonNode json = dingding(url, param);
		String accessToken = json.get("accessToken").textValue();
		
		//获取用户信息
		url = "https://api.dingtalk.com/v1.0/contact/users/me";
		param = new HashMap<>();
		param.put("x-acs-dingtalk-access-token", accessToken);
		json = dingdingGet(url, param);
		JsonNode emailNode = json.get("email");
		String userName = json.get("nick").textValue();
		String loginName = null;
		if(emailNode != null) {
			loginName = emailNode.textValue();
		}else {
			loginName = json.get("mobile").textValue();
		}
		
		LoginUser loginUser = new LoginUser();
		loginUser.setLoginName(loginName);
		loginUser.setUserName(userName);
		
		//保存用户到Dbee
		SysUserRepository sysUserRepository = SpringBeanContext.getBean(SysUserRepository.class);
		SysUserPO sysUserPO = sysUserRepository.queryByLoginName(loginName);
		if(sysUserPO == null) {
			//如果用户第一次登录，登记到内部系统
			SysUserParam bizParam = new SysUserParam();
			bizParam.setLoginName(loginUser.getLoginName());
			bizParam.setUserName(loginUser.getUserName());
			bizParam.setEmail(loginUser.getEmail());
			bizParam.setRegisteredSource(RegisteredSourceEnum.DINGDING.getCode());
			bizParam.setRoleType(RoleTypeEnum.NORMAL.getCode());
			loginUser.setId(sysUserRepository.add(bizParam));
		}else{
			loginUser.setRoleType(sysUserPO.getRoleType());
			loginUser.setId(sysUserPO.getId());
		}
		
		return loginUser;
	}

	/**
	 * 微信返回的数据格式：
	 *{"expireIn":7200,"accessToken":"xxx","refreshToken":"xxx", "code":"invalidParameter","message":"参数错误:不合法的参数 grant_type"}
	 */
	private JsonNode dingding(String url, Map<String, Object> param) {
		JsonNode json = JsonUtils.parseToNode(HttpUtils.postResponse(url, param));
		if(null != json.get("code")){
			logger.error("Dingding response: {}", json.toString());
			LogUtils.throwException(logger, MessageCodeEnum.REQUEST_DINGDING_FAILURE);
		}
		return json;
	}
	
	private JsonNode dingdingGet(String url, Map<String, Object> cookies) {
		JsonNode json = JsonUtils.parseToNode(HttpUtils.getResponse(url, cookies));
		if(null != json.get("code")){
			logger.error("Dingding response: {}", json.toString());
			LogUtils.throwException(logger, MessageCodeEnum.REQUEST_DINGDING_FAILURE);
		}
		return json;
	}
	
	@Override
	public List<SysUser> search(String userName, GlobalConfigAgg globalConfig) {
		return null;
	}
}
