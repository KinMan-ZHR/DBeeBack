package kinman.dbee.infrastructure.repository;

import java.util.Date;
import java.util.List;

import kinman.dbee.api.enums.GlobalConfigItemTypeEnum;
import kinman.dbee.api.response.model.GlobalConfigAgg;
import kinman.dbee.api.response.model.GlobalConfigAgg.CAS;
import kinman.dbee.api.response.model.GlobalConfigAgg.CodeRepo;
import kinman.dbee.api.response.model.GlobalConfigAgg.DingDing;
import kinman.dbee.api.response.model.GlobalConfigAgg.EnvTemplate;
import kinman.dbee.api.response.model.GlobalConfigAgg.FeiShu;
import kinman.dbee.api.response.model.GlobalConfigAgg.ImageRepo;
import kinman.dbee.api.response.model.GlobalConfigAgg.Ldap;
import kinman.dbee.api.response.model.GlobalConfigAgg.Maven;
import kinman.dbee.api.response.model.GlobalConfigAgg.More;
import kinman.dbee.api.response.model.GlobalConfigAgg.TraceTemplate;
import kinman.dbee.api.response.model.GlobalConfigAgg.WeChat;
import kinman.dbee.infrastructure.param.GlobalConfigParam;
import kinman.dbee.infrastructure.repository.mapper.CustomizedBaseMapper;
import kinman.dbee.infrastructure.repository.mapper.GlobalConfigMapper;
import kinman.dbee.infrastructure.repository.po.GlobalConfigPO;
import kinman.dbee.infrastructure.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

@Repository
public class GlobalConfigRepository extends BaseRepository<GlobalConfigParam, GlobalConfigPO> {

	@Autowired
	private GlobalConfigMapper mapper;

	public GlobalConfigPO queryByItemType(Integer itemType) {
		GlobalConfigParam bizParam = new GlobalConfigParam();
		bizParam.setItemType(itemType);
		return query(bizParam);
	}
	
	public GlobalConfigAgg queryAgg(GlobalConfigParam bizParam) {
		GlobalConfigAgg globalConfig = new GlobalConfigAgg();
		QueryWrapper<GlobalConfigPO> queryWrapper = buildQueryWrapper(bizParam, null);
		List<GlobalConfigPO> pos = mapper.selectList(queryWrapper);
		if(CollectionUtils.isEmpty(pos)) {
			return globalConfig;
		}
		for(GlobalConfigPO po : pos) {
			if(StringUtils.isBlank(po.getItemValue())) {
				continue;
			}
			if(GlobalConfigItemTypeEnum.CODE_REPO.getCode().equals(po.getItemType())) {
				globalConfig.setCodeRepo(JsonUtils.parseToObject(po.getItemValue(), CodeRepo.class));
				continue;
			}
			if(GlobalConfigItemTypeEnum.IMAGE_REPO.getCode().equals(po.getItemType())) {
				globalConfig.setImageRepo(JsonUtils.parseToObject(po.getItemValue(), ImageRepo.class));
				continue;
			}
			if(GlobalConfigItemTypeEnum.MAVEN.getCode().equals(po.getItemType())) {
				globalConfig.setMaven(JsonUtils.parseToObject(po.getItemValue(), Maven.class));
				continue;
			}
			if(GlobalConfigItemTypeEnum.TRACE_TEMPLATE.getCode().equals(po.getItemType())) {
				TraceTemplate traceTemplate = JsonUtils.parseToObject(po.getItemValue(), TraceTemplate.class);
				globalConfig.setTraceTemplate(po.getId(), traceTemplate);
				continue;
			}
			if(GlobalConfigItemTypeEnum.ENV_TEMPLATE.getCode().equals(po.getItemType())) {
				EnvTemplate template = JsonUtils.parseToObject(po.getItemValue(), EnvTemplate.class);
				template.setId(po.getId());
				template.setCreationTime(po.getCreationTime());
				template.setUpdateTime(po.getUpdateTime());
				globalConfig.setEnvTemplate(template);
				continue;
			}
			if(GlobalConfigItemTypeEnum.SERVER_IP.getCode().equals(po.getItemType())) {
				globalConfig.setServerIps(JsonUtils.parseToList(po.getItemValue(), String.class));
				continue;
			}
			if(GlobalConfigItemTypeEnum.MORE.getCode().equals(po.getItemType())) {
				More more = JsonUtils.parseToObject(po.getItemValue(), More.class);
				globalConfig.setMore(more);
				continue;
			}
		}
		return globalConfig;
	}

	@Override
	protected CustomizedBaseMapper<GlobalConfigPO> getMapper() {
		return mapper;
	}

	public boolean updateByMoreCondition(GlobalConfigParam bizParam) {
		UpdateWrapper<GlobalConfigPO> wrapper = new UpdateWrapper<>();
		wrapper.eq("item_type", bizParam.getItemType());
		wrapper.lt("item_value", bizParam.getItemValue());
		GlobalConfigPO e = param2Entity(bizParam);
		e.setUpdateTime(new Date());
		return getMapper().update(e, wrapper) > 0 ? true : false;
	}
	
	@Override
	protected GlobalConfigPO updateCondition(GlobalConfigParam bizParam) {
		GlobalConfigPO po = new GlobalConfigPO();
		po.setItemType(bizParam.getItemType());
		po.setId(bizParam.getId());
		return po;
	}
}
