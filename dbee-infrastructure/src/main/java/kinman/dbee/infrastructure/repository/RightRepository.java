package kinman.dbee.infrastructure.repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import kinman.dbee.api.enums.MessageCodeEnum;
import kinman.dbee.api.enums.RoleTypeEnum;
import kinman.dbee.api.response.PageData;
import kinman.dbee.api.response.model.BaseDto;
import kinman.dbee.infrastructure.param.PageParam;
import kinman.dbee.infrastructure.repository.po.AppMemberPO;
import kinman.dbee.infrastructure.repository.po.BaseAppPO;
import kinman.dbee.infrastructure.strategy.login.dto.LoginUser;
import kinman.dbee.infrastructure.utils.BeanUtils;
import kinman.dbee.infrastructure.utils.ClassUtils;
import kinman.dbee.infrastructure.utils.LogUtils;
import kinman.dbee.infrastructure.utils.QueryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

public abstract class RightRepository<P extends PageParam, E extends BaseAppPO, D extends BaseDto>
		extends BaseRepository<P, E> {

	private static final Logger logger = LoggerFactory.getLogger(RightRepository.class);
	
	@Autowired
	protected AppMemberRepository appMemberRepository;
	
	public PageData<D> page(LoginUser loginUser, P bizParam) {
		if(bizParam.getPageNum() == null){
			LogUtils.throwException(logger, MessageCodeEnum.PAGE_NUM_IS_EMPTY);
		}
		if(bizParam.getAppId() == null) {
			return pageData(bizParam);
		}
		if (RoleTypeEnum.ADMIN.getCode().equals(loginUser.getRoleType())) {
			return pageData(super.page(bizParam));
		}
		AppMemberPO appMember = appMemberRepository
				.queryByLoginNameAndAppId(loginUser.getLoginName(), bizParam.getAppId());
		if (appMember == null) {
			return pageData(bizParam);
		}
		return pageData(super.page(bizParam));
	}

	public List<D> list(LoginUser loginUser, P bizParam) {
		validateApp(bizParam.getAppId());
		if (RoleTypeEnum.ADMIN.getCode().equals(loginUser.getRoleType())) {
			return pos2Dtos(super.list(bizParam));
		}
		AppMemberPO appMember = appMemberRepository
				.queryByLoginNameAndAppId(loginUser.getLoginName(), bizParam.getAppId());
		if (appMember == null) {
			return Collections.emptyList();
		}
		return pos2Dtos(super.list(bizParam));
	}

	public D query(LoginUser loginUser, P bizParam) {
		validateApp(bizParam.getAppId());
		if (RoleTypeEnum.ADMIN.getCode().equals(loginUser.getRoleType())) {
			return po2Dto(super.query(bizParam));
		}
		AppMemberPO appMember = appMemberRepository
				.queryByLoginNameAndAppId(loginUser.getLoginName(), bizParam.getAppId());
		if (appMember == null) {
			return null;
		}
		return po2Dto(super.query(bizParam));
	}

	public boolean update(LoginUser loginUser, P bizParam) {
		validateApp(bizParam.getAppId());
		if (RoleTypeEnum.ADMIN.getCode().equals(loginUser.getRoleType())) {
			return super.updateById(bizParam);
		}
		AppMemberPO appMember = appMemberRepository
				.queryByLoginNameAndAppId(loginUser.getLoginName(), bizParam.getAppId());
		if (appMember == null) {
			LogUtils.throwException(logger, MessageCodeEnum.NO_ACCESS_RIGHT);
		}
		E e = super.queryById(bizParam.getId());
		if(e == null || !e.getAppId().equals(bizParam.getAppId())) {
			LogUtils.throwException(logger, MessageCodeEnum.RECORD_IS_NONEXISTENCE);
		}
		return super.updateById(bizParam);
	}
	
	public boolean delete(LoginUser loginUser, P bizParam) {
		validateApp(bizParam.getAppId());
		
		E e = super.queryById(bizParam.getId());
		if(e == null || !e.getAppId().equals(bizParam.getAppId())) {
			LogUtils.throwException(logger, MessageCodeEnum.RECORD_IS_NONEXISTENCE);
		}
		
		if (RoleTypeEnum.ADMIN.getCode().equals(loginUser.getRoleType())) {
			return super.delete(bizParam.getId());
		}
		AppMemberPO appMember = appMemberRepository
				.queryByLoginNameAndAppId(loginUser.getLoginName(), bizParam.getAppId());
		if (appMember == null) {
			LogUtils.throwException(logger, MessageCodeEnum.NO_ACCESS_RIGHT);
		}
		return super.delete(bizParam.getId());
	}

	public boolean deleteByAppId(String appId) {
		E po = ClassUtils.newParameterizedTypeInstance(getClass().getGenericSuperclass(), 1);
		po.setAppId(appId);
		UpdateWrapper<E> wrapper = QueryHelper.buildUpdateWrapper(po);
		po.setDeletionStatus(1);
		return getMapper().update(po, wrapper) > 0 ? true : false;
	}
	
	protected PageData<D> pageData(IPage<E> pageEntity) {
		PageData<D> pageData = new PageData<>();
		pageData.setPageNum((int)pageEntity.getCurrent());
		pageData.setPageCount((int)pageEntity.getPages());
		pageData.setPageSize((int)pageEntity.getSize());
		pageData.setItemCount((int)pageEntity.getTotal());
		pageData.setItems(pos2Dtos(pageEntity.getRecords()));
		return pageData;
	}
	
	protected List<D> pos2Dtos(List<E> pos) {
		return pos.stream().map(e -> po2Dto(e)).collect(Collectors.toList());
	}

	protected D po2Dto(E e) {
		if (e == null) {
			return null;
		}
		D dto = ClassUtils.newParameterizedTypeInstance(getClass().getGenericSuperclass(), 2);
		BeanUtils.copyProperties(e, dto);
		return dto;
	}
	
	protected PageData<D> pageData(P bizParam) {
		PageData<D> pageData = new PageData<>();
		pageData.setPageNum(1);
		pageData.setPageCount(0);
		pageData.setPageSize(bizParam.getPageSize());
		pageData.setItemCount(0);
		pageData.setItems(null);
		return pageData;
	}
}
