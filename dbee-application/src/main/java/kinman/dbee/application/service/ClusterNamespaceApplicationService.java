package kinman.dbee.application.service;

import java.util.List;

import kinman.dbee.infrastructure.utils.StringUtils;
import kinman.dbee.api.enums.GlobalConfigItemTypeEnum;
import kinman.dbee.api.enums.MessageCodeEnum;
import kinman.dbee.api.param.cluster.namespace.ClusterNamespaceCreationParam;
import kinman.dbee.api.param.cluster.namespace.ClusterNamespaceDeletionParam;
import kinman.dbee.api.param.cluster.namespace.ClusterNamespacePageParam;
import kinman.dbee.api.response.PageData;
import kinman.dbee.api.response.model.ClusterNamespace;
import kinman.dbee.api.response.model.GlobalConfigAgg;
import kinman.dbee.infrastructure.param.GlobalConfigParam;
import kinman.dbee.infrastructure.repository.po.BasePO;
import kinman.dbee.infrastructure.repository.po.ClusterPO;
import kinman.dbee.infrastructure.strategy.cluster.ClusterStrategy;
import kinman.dbee.infrastructure.utils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 
 * 集群命名空间应用服务
 */
@Service
public class ClusterNamespaceApplicationService extends BaseApplicationService<ClusterNamespace, BasePO> {

	private static final Logger logger = LoggerFactory.getLogger(ClusterNamespaceApplicationService.class);
	
	public PageData<ClusterNamespace> page(ClusterNamespacePageParam clusterNamespacePageParam) {
		if(StringUtils.isBlank(clusterNamespacePageParam.getClusterId())) {
			return zeroPageData();
		}
		ClusterPO clusterPO = clusterRepository.queryById(clusterNamespacePageParam.getClusterId());
		if (clusterPO == null) {
			LogUtils.throwException(logger, MessageCodeEnum.CLUSTER_EXISTENCE);
		}
		List<ClusterNamespace> namespaces = clusterStrategy(clusterPO.getClusterType())
				.namespaceList(clusterPO, clusterNamespacePageParam);
		PageData<ClusterNamespace> pageData = new PageData<>();
		pageData.setPageNum(clusterNamespacePageParam.getPageNum());
		pageData.setPageCount(1);
		pageData.setPageSize(clusterNamespacePageParam.getPageSize());
		pageData.setItemCount(namespaces.size());
		pageData.setItems(namespaces);
		return pageData;
	}

	public Void add(ClusterNamespaceCreationParam clusterNamespaceCreationParam) {
		validateAddParam(clusterNamespaceCreationParam);
		ClusterPO clusterPO = clusterRepository.queryById(clusterNamespaceCreationParam.getClusterId());
		if (clusterPO == null) {
			LogUtils.throwException(logger, MessageCodeEnum.CLUSTER_EXISTENCE);
		}

		ClusterStrategy cluster = clusterStrategy(clusterPO.getClusterType());

		//1.添加命名空间
		cluster.addNamespace(clusterPO,
				clusterNamespaceCreationParam.getNamespaceName());
		//2.往集群推送dbee的服务地址
		cluster.createDbeeConfig(clusterPO);
		//3.为命名空间增加镜像仓库认证key
		GlobalConfigParam globalConfigParam = new GlobalConfigParam();
		globalConfigParam.setItemType(GlobalConfigItemTypeEnum.IMAGE_REPO.getCode());
		GlobalConfigAgg globalConfigAgg = globalConfigRepository.queryAgg(globalConfigParam);
		if(globalConfigAgg != null && globalConfigAgg.getImageRepo() != null) {
			cluster.createSecret(clusterPO, globalConfigAgg.getImageRepo());
		}
		
		return null;
	}
	
	public Void delete(ClusterNamespaceDeletionParam deleteParam) {
		validateAddParam(deleteParam);
		if("default".equals(deleteParam.getNamespaceName())){
			LogUtils.throwException(logger, MessageCodeEnum.NAMESPACE_NOT_ALLOWED_DELETION);
		}
		ClusterPO clusterPO = clusterRepository.queryById(deleteParam.getClusterId());
		if (clusterPO == null) {
			LogUtils.throwException(logger, MessageCodeEnum.CLUSTER_EXISTENCE);
		}
		clusterStrategy(clusterPO.getClusterType()).deleteNamespace(clusterPO,
				deleteParam.getNamespaceName());
		return null;
	}
	
	private void validateAddParam(ClusterNamespaceCreationParam clusterNamespaceCreationParam) {
		if(StringUtils.isBlank(clusterNamespaceCreationParam.getClusterId())){
			LogUtils.throwException(logger, MessageCodeEnum.CLUSTER_ID_IS_EMPTY);
		}
		if(StringUtils.isBlank(clusterNamespaceCreationParam.getNamespaceName())){
			LogUtils.throwException(logger, MessageCodeEnum.NAMESPACE_NAME_EMPTY);
		}
	}
}
