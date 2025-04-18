package kinman.dbee.infrastructure.strategy.cluster;

import java.io.InputStream;
import java.util.List;

import kinman.dbee.api.param.app.env.replica.EnvReplicaPageParam;
import kinman.dbee.api.param.cluster.ClusterNodePageParam;
import kinman.dbee.api.param.cluster.namespace.ClusterNamespacePageParam;
import kinman.dbee.api.response.PageData;
import kinman.dbee.api.response.model.AppEnv;
import kinman.dbee.api.response.model.ClusterNamespace;
import kinman.dbee.api.response.model.ClusterNode;
import kinman.dbee.api.response.model.EnvReplica;
import kinman.dbee.api.response.model.GlobalConfigAgg.ImageRepo;
import kinman.dbee.infrastructure.strategy.cluster.model.Replica;
import kinman.dbee.infrastructure.model.ReplicaMetrics;
import kinman.dbee.infrastructure.repository.po.AppEnvPO;
import kinman.dbee.infrastructure.repository.po.AppPO;
import kinman.dbee.infrastructure.repository.po.ClusterPO;
import kinman.dbee.infrastructure.utils.DeploymentContext;

public interface ClusterStrategy {

	Void createSecret(ClusterPO clusterPO, ImageRepo imageRepo);
	
	Replica readDeployment(ClusterPO clusterPO, AppEnv appEnv, AppPO appPO);
	
	boolean createDeployment(DeploymentContext context);

	PageData<EnvReplica> replicaPage(EnvReplicaPageParam pageParam, ClusterPO clusterPO,
			AppPO appPO, AppEnvPO appEnvPO);

	boolean rebuildReplica(ClusterPO clusterPO, String replicaName, String namespace);

	InputStream streamPodLog(ClusterPO clusterPO, String replicaName, String namespace);
	
	String podLog(ClusterPO clusterPO, String replicaName, String namespace);

	String podYaml(ClusterPO clusterPO, String replicaName, String namespace);

	boolean autoScaling(AppPO appPO, AppEnvPO appEnvPO, ClusterPO clusterPO);

	void openLogCollector(ClusterPO clusterPO);

	void closeLogCollector(ClusterPO clusterPO);

	boolean logSwitchStatus(ClusterPO clusterPO);

	boolean deleteDeployment(ClusterPO clusterPO, AppPO appPO, AppEnvPO appEnvPO);

	List<String> queryFiles(ClusterPO clusterPO, String replicaName, String namespace);

	InputStream downloadFile(ClusterPO clusterPO, String namespace, String replicaName, String fileName);
	
	List<ClusterNamespace> namespaceList(ClusterPO clusterPO, ClusterNamespacePageParam clusterNamespacePageParam);
	
	boolean addNamespace(ClusterPO clusterPO, String namespaceName);
	
	boolean deleteNamespace(ClusterPO clusterPO, String namespaceName);
	
	List<ReplicaMetrics> replicaMetrics(ClusterPO clusterPO, String namespace);
	
	Void createDbeeConfig(ClusterPO clusterPO);
	
	Void deleteDbeeConfig(ClusterPO clusterPO);
	
	String version(ClusterPO clusterPO);
	
	PageData<ClusterNode> nodePage(ClusterNodePageParam pageParam, ClusterPO clusterPO);
	
	void addNode(ClusterPO clusterPO, String hostName);
	
	void deleteNode(ClusterPO clusterPO, String hostName);

}
