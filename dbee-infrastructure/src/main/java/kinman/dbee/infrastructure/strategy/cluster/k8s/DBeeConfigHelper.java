package kinman.dbee.infrastructure.strategy.cluster.k8s;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import kinman.dbee.infrastructure.component.ComponentConstants;
import kinman.dbee.infrastructure.component.SpringBeanContext;
import kinman.dbee.infrastructure.utils.StringUtils;
import kinman.dbee.api.enums.MessageCodeEnum;
import kinman.dbee.infrastructure.repository.po.ClusterPO;
import kinman.dbee.infrastructure.utils.Constants;
import kinman.dbee.infrastructure.utils.HttpUtils;
import kinman.dbee.infrastructure.utils.K8sUtils;
import kinman.dbee.infrastructure.utils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.Resource;

public class DBeeConfigHelper {
	
	private static final Logger logger = LoggerFactory.getLogger(DBeeConfigHelper.class);
	
	/**
	 * 通过ConfigMap向k8s集群写入dbee服务器的地址，地址格式为：ip1:8100,ip2:8100
	 */
	public static void writeServerIp(ClusterPO clusterPO, KubernetesClient client) {
		NamespaceList namespaceList = client.namespaces().list();
		if(CollectionUtils.isEmpty(namespaceList.getItems())) {
			return;
		}
		
		ComponentConstants componentConstants = SpringBeanContext.getBean(ComponentConstants.class);
		for(Namespace n : namespaceList.getItems()) {
			String namespace = n.getMetadata().getName();
			if(!K8sUtils.DBEE_NAMESPACE.equals(namespace)
					&& K8sUtils.getSystemNamspaces().contains(namespace)) {
				continue;
			}
			if(!"Active".equals(n.getStatus().getPhase())){
				continue;
			}
			
			ConfigMap configMap = dbeeConfigMap();
			Resource<ConfigMap> resource = client.configMaps().inNamespace(namespace)
					.resource(configMap);
			ConfigMap existedCP = resource.get();
			if(existedCP == null) {
				String ipPortUri = Constants.hostIp() + ":" + componentConstants.getServerPort();
				if(ipPortUri.startsWith(Constants.LOCALHOST_IP)) {
					LogUtils.throwException(logger, "Your dbee server mast have a valid ip, not 127.0.0.1", MessageCodeEnum.DBEE_SERVER_URL_FAILURE);
				}
				configMap.setData(Collections.singletonMap(K8sUtils.DBEE_SERVER_URL_KEY, ipPortUri));
				resource.create();
			}else {
				Set<String> newIp = new HashSet<>();
				newIp.add(Constants.hostIp() + ":" + componentConstants.getServerPort());
				String ipStr = existedCP.getData().get(K8sUtils.DBEE_SERVER_URL_KEY);
				if(!StringUtils.isBlank(ipStr)) {
					String[] ips = ipStr.split(",");
					for(String ip : ips) {
						if(ip.startsWith(Constants.LOCALHOST_IP)) {
							continue;
						}
						if(!HttpUtils.pingDbeeServer(ip)) {
							continue;
						}
						newIp.add(ip);
					}
				}
				configMap.setData(Collections.singletonMap(K8sUtils.DBEE_SERVER_URL_KEY, String.join(",", newIp)));
				resource.update();
			}
		}
	}
	
	/**
	 * 通过ConfigMap向k8s集群删除dbee服务器的地址，地址格式为：ip1:8100,ip2:8100
	 */
	public static void deleteServerIp(ClusterPO clusterPO, KubernetesClient client) {
		NamespaceList namespaceList = client.namespaces().list();
		if(CollectionUtils.isEmpty(namespaceList.getItems())) {
			return;
		}
		for(Namespace n : namespaceList.getItems()) {
			String namespace = n.getMetadata().getName();
			if(!K8sUtils.DBEE_NAMESPACE.equals(namespace)
					&& K8sUtils.getSystemNamspaces().contains(namespace)) {
				continue;
			}
			if(!"Active".equals(n.getStatus().getPhase())){
				continue;
			}
			ConfigMap configMap = client.configMaps().inNamespace(namespace)
					.withName(K8sUtils.DBEE_CONFIGMAP_NAME).get();
			if(configMap == null) {
				continue;
			}
			Set<String> newIp = new HashSet<>();
			String ipStr = configMap.getData().get(K8sUtils.DBEE_SERVER_URL_KEY);
			if(!StringUtils.isBlank(ipStr)) {
				String[] ips = ipStr.split(",");
				//ip格式为：127.0.0.1:8100
				for(String ip : ips) {
					if(Constants.hostIp().equals(ip.split(":")[0])) {
						continue;
					}
					if(!HttpUtils.pingDbeeServer(ip)) {
						continue;
					}
					newIp.add(ip);
				}
			}
			configMap.setData(Collections.singletonMap(K8sUtils.DBEE_SERVER_URL_KEY, String.join(",", newIp)));
			Resource<ConfigMap> resource = client.configMaps().inNamespace(namespace).resource(configMap);
			if(newIp.size() == 0) {
				resource.delete();
			}else {
				resource.update();
			}
		}
	}
	
	private static ConfigMap dbeeConfigMap() {
		ConfigMap configMap = new ConfigMap();
		ObjectMeta meta = new ObjectMeta();
		meta.setName(K8sUtils.DBEE_CONFIGMAP_NAME);
		meta.setLabels(Collections.singletonMap(K8sUtils.DBEE_LABEL_KEY, K8sUtils.DBEE_CONFIGMAP_NAME));
		configMap.setMetadata(meta);
		return configMap;
	}
}