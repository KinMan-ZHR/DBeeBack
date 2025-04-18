package kinman.dbee.rest.resource;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kinman.dbee.api.enums.RegisteredSourceEnum;
import kinman.dbee.api.param.user.UserLoginParam;
import kinman.dbee.infrastructure.annotation.AccessNotLogin;
import kinman.dbee.infrastructure.strategy.login.dto.LoginUser;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ThirdPartyLoginRest extends AbstractRest {

	@AccessNotLogin
	@RequestMapping("/wechat")
	public String wechat(@RequestParam("code") String code, @RequestParam("state") String state,
			HttpServletResponse response) {
		UserLoginParam userLoginParam = new UserLoginParam();
		userLoginParam.setLoginSource(RegisteredSourceEnum.WECHAT.getCode());
		userLoginParam.setCode(code);
		LoginUser loginUser = sysUserApplicationService.login(userLoginParam);
		Cookie c = new Cookie("login_token", loginUser.getLastLoginToken());
		response.addCookie(c);
		return "index.html";
	}

	@AccessNotLogin
	@RequestMapping("/dingding")
	public String dingding(@RequestParam("authCode") String code, @RequestParam("state") String state,
			HttpServletResponse response) {
		UserLoginParam userLoginParam = new UserLoginParam();
		userLoginParam.setLoginSource(RegisteredSourceEnum.DINGDING.getCode());
		userLoginParam.setCode(code);
		LoginUser loginUser = sysUserApplicationService.login(userLoginParam);
		Cookie c = new Cookie("login_token", loginUser.getLastLoginToken());
		response.addCookie(c);
		return "index.html";
	}
	
	@AccessNotLogin
	@RequestMapping("/feishu")
	public String feishu(@RequestParam("code") String code, @RequestParam("state") String state,
			HttpServletResponse response) {
		UserLoginParam userLoginParam = new UserLoginParam();
		userLoginParam.setLoginSource(RegisteredSourceEnum.FEISHU.getCode());
		userLoginParam.setCode(code);
		LoginUser loginUser = sysUserApplicationService.login(userLoginParam);
		Cookie c = new Cookie("login_token", loginUser.getLastLoginToken());
		response.addCookie(c);
		return "index.html";
	}

	@AccessNotLogin
	@GetMapping("/cas")
	public String cas(HttpSession session, HttpServletResponse response) {
		Assertion assertion = (Assertion) session.getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);
		if(assertion == null) {
			return null;
		}
		AttributePrincipal principal = assertion.getPrincipal();
		String loginName = principal.getName();
		Object userName = principal.getAttributes().get("cn");
		UserLoginParam userLoginParam = new UserLoginParam();
		userLoginParam.setLoginSource(RegisteredSourceEnum.CAS.getCode());
		userLoginParam.setLoginName(loginName);
		userLoginParam.setUserName(userName == null ? loginName : userName.toString());
		LoginUser loginUser = sysUserApplicationService.login(userLoginParam);
		Cookie c = new Cookie("login_token", loginUser.getLastLoginToken());
		response.addCookie(c);
		return "index.html";
	}
}
