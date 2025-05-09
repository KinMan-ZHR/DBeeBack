package kinman.dbee.infrastructure.repository;

import java.util.Date;

import kinman.dbee.api.response.model.LogRecord;
import kinman.dbee.infrastructure.component.ComponentConstants;
import kinman.dbee.infrastructure.param.LogRecordParam;
import kinman.dbee.infrastructure.repository.mapper.CustomizedBaseMapper;
import kinman.dbee.infrastructure.repository.mapper.LogRecordMapper;
import kinman.dbee.infrastructure.repository.po.LogRecordPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

@Repository
public class LogRecordRepository extends RightRepository<LogRecordParam, LogRecordPO, LogRecord> {

	@Autowired
	private ComponentConstants componentConstants;
	
	@Autowired
	private LogRecordMapper mapper;
	
	@Override
	protected CustomizedBaseMapper<LogRecordPO> getMapper() {
		return mapper;
	}

	public String add(LogRecordPO po) {
		getMapper().insert(po);
		return po.getId();
	}
	
	public Void delete(Date date) {
		UpdateWrapper<LogRecordPO> wrapper = new UpdateWrapper<>();
		wrapper.le("update_time", date);
		getMapper().delete(wrapper);
		//整理表空间
		if(componentConstants.getMysql().isEnable()) {
			this.executeSql("alter table log_record engine=InnoDB");
		}
		return null;
	}
	
	@Override
	protected LogRecordPO updateCondition(LogRecordParam bizParam) {
		LogRecordPO po = new LogRecordPO();
		po.setId(bizParam.getId());
		return po;
	}
}