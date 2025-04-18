package kinman.dbee.infrastructure.strategy.login;

import java.util.List;

import kinman.dbee.api.enums.MessageCodeEnum;
import kinman.dbee.api.enums.RegisteredSourceEnum;
import kinman.dbee.api.enums.RoleTypeEnum;
import kinman.dbee.api.param.user.UserLoginParam;
import kinman.dbee.api.response.model.GlobalConfigAgg;
import kinman.dbee.api.response.model.GlobalConfigAgg.WeChat;
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

public class WeChatUserStrategy extends UserStrategy {

	private static final Logger logger = LoggerFactory.getLogger(WeChatUserStrategy.class);
	
	@Override
	public LoginUser login(UserLoginParam userLoginParam, GlobalConfigAgg globalConfig, PasswordEncoder passwordEncoder) {
		WeChat wechat = globalConfig.getWechat();
		//获取accessToken
		String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid="+ wechat.getCorpId() +"&corpsecret=" + wechat.getSecret();
		JsonNode json = wechat(url);
		String accessToken = json.get("access_token").textValue();
		
		//获取userId
		url = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token="+ accessToken +"&code=" + userLoginParam.getCode();
		json = wechat(url);
		String userId = json.get("UserId").textValue();
		
		//获取name
		url = "https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token="+ accessToken +"&userid=" + userId;
		json = wechat(url);
		String userName = json.get("name").textValue();
		JsonNode emailNode = json.get("email");
		String loginName = null;
		if(emailNode != null) {
			loginName = emailNode.textValue();
		}else {
			loginName = userId;
		}
		
		LoginUser loginUser = new LoginUser();
		loginUser.setLoginName(loginName);
		loginUser.setUserName(userName);
		
		//保存用户到Dbee
		SysUserRepository sysUserRepository = SpringBeanContext.getBean(SysUserRepository.class);
		SysUserPO sysUserPO = sysUserRepository.queryByLoginName(userId);
		if(sysUserPO == null) {
			//如果用户第一次登录，登记到内部系统
			SysUserParam bizParam = new SysUserParam();
			bizParam.setLoginName(loginUser.getLoginName());
			bizParam.setUserName(loginUser.getUserName());
			bizParam.setEmail(loginUser.getEmail());
			bizParam.setRegisteredSource(RegisteredSourceEnum.WECHAT.getCode());
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
	 *{"UserId":"xxx","DeviceId":"","errcode":0,"errmsg":"ok"}
	 */
	private JsonNode wechat(String url) {
		JsonNode json = JsonUtils.parseToNode(HttpUtils.getResponse(url));
		if(0 != json.get("errcode").asInt()){
			logger.error("WeChat response: {}", json.toString());
			LogUtils.throwException(logger, MessageCodeEnum.REQUEST_WECHAT_FAILURE);
		}
		return json;
	}
	
	@Override
	public List<SysUser> search(String userName, GlobalConfigAgg globalConfig) {
		return null;
	}
}
