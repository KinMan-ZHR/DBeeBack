package kinman.dbee.rest.resource;

import kinman.dbee.api.enums.MessageCodeEnum;
import kinman.dbee.api.response.RestResponse;
import kinman.dbee.application.service.SysUserApplicationService;
import kinman.dbee.infrastructure.exception.ApplicationException;
import kinman.dbee.infrastructure.strategy.login.dto.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractRest {

	@Autowired
	protected SysUserApplicationService sysUserApplicationService;
	
	protected LoginUser queryLoginUserByToken(String loginToken) {
		return sysUserApplicationService.queryLoginUserByToken(loginToken);
	}

	protected <D> RestResponse<D> success() {
		return new RestResponse<D>();
	}
	
	protected <D> RestResponse<D> success(D data) {
		return new RestResponse<D>(data);
	}

	protected <D> RestResponse<D> error(ApplicationException e) {
		return new RestResponse<D>(e.getCode(), e.getMessage());
	}
	
	protected <D> RestResponse<D> error(MessageCodeEnum e) {
		return new RestResponse<D>(e.getCode(), e.getMessage());
	}
}
