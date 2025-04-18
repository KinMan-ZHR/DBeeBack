package kinman.dbee.infrastructure.repository;

import kinman.dbee.infrastructure.param.ClusterParam;
import kinman.dbee.infrastructure.repository.mapper.ClusterMapper;
import kinman.dbee.infrastructure.repository.mapper.CustomizedBaseMapper;
import kinman.dbee.infrastructure.repository.po.ClusterPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ClusterRepository
		extends BaseRepository<ClusterParam, ClusterPO> {

	@Autowired
	private ClusterMapper mapper;

	@Override
	protected CustomizedBaseMapper<ClusterPO> getMapper() {
		return mapper;
	}

	@Override
	protected ClusterPO updateCondition(ClusterParam bizParam) {
		ClusterPO po = new ClusterPO();
		po.setId(bizParam.getId());
		return po;
	}

}