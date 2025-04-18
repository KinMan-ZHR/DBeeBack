package kinman.dbee.infrastructure.repository;

import kinman.dbee.api.response.model.AffinityToleration;
import kinman.dbee.infrastructure.param.AffinityTolerationParam;
import kinman.dbee.infrastructure.repository.mapper.AffinityTolerationMapper;
import kinman.dbee.infrastructure.repository.mapper.CustomizedBaseMapper;
import kinman.dbee.infrastructure.repository.po.AffinityTolerationPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AffinityTolerationRepository extends RightRepository<AffinityTolerationParam, AffinityTolerationPO, AffinityToleration> {

	@Autowired
	private AffinityTolerationMapper mapper;
	
	@Override
	protected CustomizedBaseMapper<AffinityTolerationPO> getMapper() {
		return mapper;
	}

	@Override
	protected AffinityTolerationPO updateCondition(AffinityTolerationParam bizParam) {
		AffinityTolerationPO po = new AffinityTolerationPO();
		po.setId(bizParam.getId());
		return po;
	}
}