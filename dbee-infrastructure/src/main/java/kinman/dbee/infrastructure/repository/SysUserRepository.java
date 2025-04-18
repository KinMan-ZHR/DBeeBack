package kinman.dbee.infrastructure.repository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import kinman.dbee.api.enums.MessageCodeEnum;
import kinman.dbee.api.response.PageData;
import kinman.dbee.api.response.model.SysUser;
import kinman.dbee.infrastructure.param.SysUserParam;
import kinman.dbee.infrastructure.repository.mapper.CustomizedBaseMapper;
import kinman.dbee.infrastructure.repository.mapper.SysUserMapper;
import kinman.dbee.infrastructure.repository.po.SysUserPO;
import kinman.dbee.infrastructure.strategy.login.dto.LoginUser;
import kinman.dbee.infrastructure.strategy.login.param.LoginUserParam;
import kinman.dbee.infrastructure.utils.BeanUtils;
import kinman.dbee.infrastructure.utils.LogUtils;
import kinman.dbee.infrastructure.utils.QueryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@Repository
public class SysUserRepository extends BaseRepository<SysUserParam, SysUserPO> {

	private static final Logger logger = LoggerFactory.getLogger(SysUserRepository.class);

	@Autowired
	private SysUserMapper mapper;
	
	@Autowired
	private AppMemberRepository appMemberRepository;
	
	public LoginUser queryLoginUser(String loginToken) {
		LoginUserParam bizParam = new LoginUserParam();
		bizParam.setLastLoginToken(loginToken);
		return queryLoginUser(bizParam);
	}
	
	public LoginUser queryLoginUser(SysUserParam bizParam) {
		QueryWrapper<SysUserPO> queryWrapper = buildQueryWrapper(bizParam, null);
		SysUserPO po = getMapper().selectOne(queryWrapper);
		if(Objects.isNull(po)) {
			return null;
		}
		LoginUser loginUser = new LoginUser();
		BeanUtils.copyProperties(po, loginUser);
		return loginUser;
	}
	
	public SysUserPO queryByLoginName(String loginName) {
		SysUserParam bizParam = new SysUserParam();
		bizParam.setLoginName(loginName);
		return query(bizParam);
	}
	
	public List<SysUser> likeRightList(SysUserParam bizParam) {
		QueryWrapper<SysUserPO> queryWrapper = QueryHelper.buildLikeRightWrapper(param2Entity(bizParam));
		List<SysUserPO> pos = getMapper().selectList(queryWrapper);
		if (CollectionUtils.isEmpty(pos)) {
			return null;
		}
		return pos2Dtos(pos);
	}
	
	public PageData<SysUser> likeRightPage(SysUserParam bizParam) {
		if(bizParam.getPageNum() == null) {
			LogUtils.throwException(logger, MessageCodeEnum.PAGE_NUM_IS_EMPTY);
		}
		IPage<SysUserPO> page = new Page<>(bizParam.getPageNum(), bizParam.getPageSize());
		QueryWrapper<SysUserPO> queryWrapper = QueryHelper.buildLikeRightWrapper(param2Entity(bizParam));
		getMapper().selectPage(page, queryWrapper);
		return pageData(page);
	}
	
	public boolean delete(String id) {
		if(super.delete(id)) {
			return appMemberRepository.deleteByUserId(id);
		}
		return false;
	}

	protected List<SysUser> pos2Dtos(List<SysUserPO> pos) {
		return pos.stream().map(e -> po2Dto(e)).collect(Collectors.toList());
	}
	
	protected SysUser po2Dto(SysUserPO po) {
		if (po == null) {
			return null;
		}
		SysUser sysUser = new SysUser();
		BeanUtils.copyProperties(po, sysUser);
		return sysUser;
	}
	
	protected PageData<SysUser> pageData(IPage<SysUserPO> pagePO) {
		PageData<SysUser> pageData = new PageData<>();
		pageData.setPageNum((int)pagePO.getCurrent());
		pageData.setPageCount((int)pagePO.getPages());
		pageData.setPageSize((int)pagePO.getSize());
		pageData.setItemCount((int)pagePO.getTotal());
		pageData.setItems(pos2Dtos(pagePO.getRecords()));
		return pageData;
	}
	
	@Override
	protected SysUserPO updateCondition(SysUserParam bizParam) {
		SysUserPO po = new SysUserPO();
		po.setId(bizParam.getId());
		po.setLoginName(bizParam.getLoginName());
		return po;
	}
	
	@Override
	protected CustomizedBaseMapper<SysUserPO> getMapper() {
		return mapper;
	}

}