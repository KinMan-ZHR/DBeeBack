package kinman.dbee.infrastructure.strategy.login;

import java.util.List;

import kinman.dbee.api.enums.RegisteredSourceEnum;
import kinman.dbee.api.enums.RoleTypeEnum;
import kinman.dbee.api.param.user.UserLoginParam;
import kinman.dbee.api.response.model.GlobalConfigAgg;
import kinman.dbee.api.response.model.SysUser;
import kinman.dbee.infrastructure.component.SpringBeanContext;
import kinman.dbee.infrastructure.param.SysUserParam;
import kinman.dbee.infrastructure.repository.SysUserRepository;
import kinman.dbee.infrastructure.repository.po.SysUserPO;
import kinman.dbee.infrastructure.strategy.login.dto.LoginUser;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CasUserStrategy extends UserStrategy {

	@Override
	public LoginUser login(UserLoginParam userLoginParam, GlobalConfigAgg globalConfig, PasswordEncoder passwordEncoder) {
		
		LoginUser loginUser = new LoginUser();
		loginUser.setLoginName(userLoginParam.getLoginName());
		loginUser.setUserName(userLoginParam.getUserName());
		
		//保存用户到Dbee
		SysUserRepository sysUserRepository = SpringBeanContext.getBean(SysUserRepository.class);
		SysUserPO sysUserPO = sysUserRepository.queryByLoginName(userLoginParam.getLoginName());
		if(sysUserPO == null) {
			//如果用户第一次登录，登记到内部系统
			SysUserParam bizParam = new SysUserParam();
			bizParam.setLoginName(loginUser.getLoginName());
			bizParam.setUserName(loginUser.getUserName());
			bizParam.setEmail(loginUser.getEmail());
			bizParam.setRegisteredSource(RegisteredSourceEnum.CAS.getCode());
			bizParam.setRoleType(RoleTypeEnum.NORMAL.getCode());
			loginUser.setId(sysUserRepository.add(bizParam));
		}else{
			loginUser.setRoleType(sysUserPO.getRoleType());
			loginUser.setId(sysUserPO.getId());
		}
		
		return loginUser;
	}
	
	@Override
	public List<SysUser> search(String userName, GlobalConfigAgg globalConfig) {
		return null;
	}
}
