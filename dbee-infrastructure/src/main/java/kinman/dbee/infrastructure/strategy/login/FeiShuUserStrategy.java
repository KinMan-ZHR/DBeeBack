package kinman.dbee.infrastructure.strategy.login;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kinman.dbee.infrastructure.component.SpringBeanContext;
import kinman.dbee.infrastructure.strategy.login.dto.LoginUser;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import kinman.dbee.api.enums.MessageCodeEnum;
import kinman.dbee.api.enums.RegisteredSourceEnum;
import kinman.dbee.api.enums.RoleTypeEnum;
import kinman.dbee.api.param.user.UserLoginParam;
import kinman.dbee.api.response.model.GlobalConfigAgg;
import kinman.dbee.api.response.model.GlobalConfigAgg.FeiShu;
import kinman.dbee.api.response.model.SysUser;
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
import com.github.promeg.pinyinhelper.Pinyin;

public class FeiShuUserStrategy extends UserStrategy {

	private static final Logger logger = LoggerFactory.getLogger(FeiShuUserStrategy.class);
	
	@Override
	public LoginUser login(UserLoginParam userLoginParam, GlobalConfigAgg globalConfig, PasswordEncoder passwordEncoder) {
		FeiShu feishu = globalConfig.getFeishu();
		
		//获取appAccessToken
		String url = "https://open.feishu.cn/open-apis/auth/v3/app_access_token/internal";
		Map<String, Object> param = new HashMap<>();
		param.put("app_id", feishu.getAppID());
		param.put("app_secret", feishu.getAppSecret());
		JsonNode json = post(url, param);
		String appAccessToken = json.get("app_access_token").asText();
		
		//获取用户信息
		url = "https://open.feishu.cn/open-apis/authen/v1/access_token";
		json = post(url, appAccessToken, userLoginParam.getCode()).get("data");
		String userName = json.get("name").textValue();
		String loginName = Pinyin.toPinyin(userName, "").toLowerCase();
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
			bizParam.setRegisteredSource(RegisteredSourceEnum.FEISHU.getCode());
			bizParam.setRoleType(RoleTypeEnum.NORMAL.getCode());
			loginUser.setId(sysUserRepository.add(bizParam));
		}else{
			loginUser.setRoleType(sysUserPO.getRoleType());
			loginUser.setId(sysUserPO.getId());
		}
		
		return loginUser;
	}

	private JsonNode post(String url, Map<String, Object> param) {
		JsonNode json = JsonUtils.parseToNode(HttpUtils.postResponse(url, param));
		if(0 != json.get("code").asInt()){
			logger.error("FeiShu response: {}", json.toString());
			LogUtils.throwException(logger, MessageCodeEnum.HTT_POST_FAILURE);
		}
		return json;
	}
	
	private JsonNode post(String url, String appAccessToken, String code) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(2000)
                .setConnectTimeout(2000)
                .setSocketTimeout(2000)
                .build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
        httpPost.setHeader("Authorization", "Bearer " + appAccessToken);
        httpPost.setEntity(new StringEntity("{\"grant_type\":\"authorization_code\",\"code\":\""+ code +"\"}", "UTF-8"));
        String responseContext = null;
        try (CloseableHttpResponse response = HttpUtils.createHttpClient(url).execute(httpPost);){
        	responseContext = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
        	LogUtils.throwException(logger, MessageCodeEnum.HTT_POST_FAILURE);
        }
        JsonNode json = JsonUtils.parseToNode(responseContext);
		if(0 != json.get("code").asInt()){
			logger.error("post response: {}", json.toString());
			LogUtils.throwException(logger, MessageCodeEnum.HTT_POST_FAILURE);
		}
		return json;
	}
	
	@Override
	public List<SysUser> search(String userName, GlobalConfigAgg globalConfig) {
		return null;
	}
}
