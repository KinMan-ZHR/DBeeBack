package kinman.dbee.infrastructure.strategy.login;

import java.util.List;

import kinman.dbee.api.param.user.UserLoginParam;
import kinman.dbee.api.response.model.GlobalConfigAgg;
import kinman.dbee.api.response.model.SysUser;
import kinman.dbee.infrastructure.strategy.login.dto.LoginUser;
import org.springframework.security.crypto.password.PasswordEncoder;

public abstract class UserStrategy {

	public abstract LoginUser login(UserLoginParam userLoginParam,
                                    GlobalConfigAgg globalConfig, PasswordEncoder passwordEncoder);
	
	public abstract List<SysUser> search(String userName, GlobalConfigAgg globalConfig);
}
