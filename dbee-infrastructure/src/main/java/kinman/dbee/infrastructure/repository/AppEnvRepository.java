package kinman.dbee.infrastructure.repository;

import java.util.List;

import kinman.dbee.infrastructure.param.AppEnvParam;
import kinman.dbee.infrastructure.repository.mapper.AppEnvMapper;
import kinman.dbee.infrastructure.repository.mapper.CustomizedBaseMapper;
import kinman.dbee.infrastructure.repository.po.AppEnvPO;
import kinman.dbee.infrastructure.repository.po.AppMemberPO;
import kinman.dbee.infrastructure.repository.po.AppPO;
import kinman.dbee.infrastructure.strategy.login.dto.LoginUser;
import kinman.dbee.infrastructure.utils.JsonUtils;
import kinman.dbee.infrastructure.utils.StringUtils;
import kinman.dbee.api.enums.RoleTypeEnum;
import kinman.dbee.api.enums.TechTypeEnum;
import kinman.dbee.api.response.model.AppEnv;
import kinman.dbee.api.response.model.AppEnv.EnvExtendNode;
import kinman.dbee.api.response.model.AppEnv.EnvExtendSpringBoot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

@Repository
public class AppEnvRepository extends RightRepository<AppEnvParam, AppEnvPO, AppEnv> {

	@Autowired
	private AppEnvMapper mapper;
	
	@Autowired
	private AppRepository appRepository;

	@Override
	protected CustomizedBaseMapper<AppEnvPO> getMapper() {
		return mapper;
	}

	public List<AppEnvPO> list(List<String> appIds) {
		QueryWrapper<AppEnvPO> wrapper = new QueryWrapper<>();
		wrapper.in("app_id", appIds);
		wrapper.eq("deletion_status", 0);
		return mapper.selectList(wrapper);
	}
	
	public AppEnv query(LoginUser loginUser, AppEnvParam bizParam) {
		validateApp(bizParam.getAppId());
		if (!RoleTypeEnum.ADMIN.getCode().equals(loginUser.getRoleType())) {
			AppMemberPO appMember = appMemberRepository
					.queryByLoginNameAndAppId(loginUser.getLoginName(), bizParam.getAppId());
			if (appMember == null) {
				return null;
			}
		}
		
		AppEnvPO appEnvPO = super.query(bizParam);
		AppEnv dto = po2Dto(appEnvPO);
		if(!StringUtils.isBlank(appEnvPO.getExt())){
			AppPO appPO = appRepository.queryById(bizParam.getAppId());
			if(TechTypeEnum.SPRING_BOOT.getCode().equals(appPO.getTechType())) {
				dto.setEnvExtend(JsonUtils.parseToObject(appEnvPO.getExt(), EnvExtendSpringBoot.class));
			}else if(TechTypeEnum.VUE.getCode().equals(appPO.getTechType())
					|| TechTypeEnum.REACT.getCode().equals(appPO.getTechType())) {
				dto.setEnvExtend(JsonUtils.parseToObject(appEnvPO.getExt(), EnvExtendNode.class));
			}
		}
		return dto;
	}
	
	@Override
	protected AppEnvPO updateCondition(AppEnvParam bizParam) {
		AppEnvPO po = new AppEnvPO();
		po.setId(bizParam.getId());
		return po;
	}
}