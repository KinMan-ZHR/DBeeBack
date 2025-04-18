package kinman.dbee.infrastructure.repository.mapper;

import kinman.dbee.infrastructure.repository.po.BasePO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface CustomizedBaseMapper<T extends BasePO> extends BaseMapper<T> {

}