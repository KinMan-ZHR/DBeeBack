package kinman.dbee.infrastructure.strategy.login;

import java.util.List;

import kinman.dbee.api.param.user.UserLoginParam;
import kinman.dbee.api.response.model.GlobalConfigAgg;
import kinman.dbee.api.response.model.SysUser;
import kinman.dbee.infrastructure.component.SpringBeanContext;
import kinman.dbee.infrastructure.strategy.login.dto.LoginUser;
import kinman.dbee.infrastructure.param.SysUserParam;
import kinman.dbee.infrastructure.repository.SysUserRepository;
import kinman.dbee.infrastructure.repository.po.SysUserPO;
import kinman.dbee.infrastructure.utils.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

public class NormalUserStrategy extends UserStrategy {

	@Override
	public LoginUser login(UserLoginParam userLoginParam, GlobalConfigAgg globalConfig,
                           PasswordEncoder passwordEncoder) {
		SysUserRepository sysUserRepository = SpringBeanContext.getBean(SysUserRepository.class);
		SysUserPO sysUserPO = sysUserRepository.queryByLoginName(userLoginParam.getLoginName());
		if(sysUserPO == null) {
			return null;
		}
		if(!passwordEncoder.matches(userLoginParam.getPassword(), sysUserPO.getPassword())) {
			return null;
		}
		LoginUser loginUser = new LoginUser();
		BeanUtils.copyProperties(sysUserPO, loginUser);
		return loginUser;
	}

	@Override
	public List<SysUser> search(String userName, GlobalConfigAgg globalConfig) {
		SysUserRepository sysUserRepository = SpringBeanContext.getBean(SysUserRepository.class);
		SysUserParam userInfoParam = new SysUserParam();
		userInfoParam.setLoginName(userName);
		return sysUserRepository.likeRightList(userInfoParam);
	}
}
