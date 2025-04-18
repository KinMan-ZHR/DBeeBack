package kinman.dbee.infrastructure.repository;

import kinman.dbee.api.response.model.DeploymentDetail;
import kinman.dbee.infrastructure.param.DeploymentDetailParam;
import kinman.dbee.infrastructure.repository.mapper.CustomizedBaseMapper;
import kinman.dbee.infrastructure.repository.mapper.DeploymentDetailMapper;
import kinman.dbee.infrastructure.repository.po.DeploymentDetailPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DeploymentDetailRepository
		extends RightRepository<DeploymentDetailParam, DeploymentDetailPO, DeploymentDetail> {

	@Autowired
	private DeploymentDetailMapper mapper;

	@Override
	protected CustomizedBaseMapper<DeploymentDetailPO> getMapper() {
		return mapper;
	}

	@Override
	protected DeploymentDetailPO updateCondition(DeploymentDetailParam bizParam) {
		DeploymentDetailPO po = new DeploymentDetailPO();
		po.setId(bizParam.getId());
		return po;
	}

}
