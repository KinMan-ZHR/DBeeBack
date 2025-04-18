package kinman.dbee.rest.websocket;

import kinman.dbee.application.service.EnvReplicaApplicationService;
import kinman.dbee.application.service.SysUserApplicationService;
import kinman.dbee.infrastructure.component.SpringBeanContext;
import kinman.dbee.infrastructure.context.AppEnvClusterContext;
import kinman.dbee.infrastructure.strategy.login.dto.LoginUser;
import kinman.dbee.rest.websocket.ssh.SSHContext;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;

/**
 * 
 * WebSocket的基础功能
 */
public abstract class AbstracWebSocket {

	public SSHContext sshContext(String loginToken, String appId, String envId, String replicaName) {
		SysUserApplicationService sysUserApplicationService = SpringBeanContext
				.getBean(SysUserApplicationService.class);
		EnvReplicaApplicationService replicaApplicationService = SpringBeanContext
				.getBean(EnvReplicaApplicationService.class);
		LoginUser loginUser = sysUserApplicationService.queryLoginUserByToken(loginToken);
		AppEnvClusterContext appEnvClusterContext = replicaApplicationService
					.queryCluster(appId, envId, loginUser);
		 Config config = new ConfigBuilder()
				 .withTrustCerts(true)
				 .withMasterUrl(appEnvClusterContext.getClusterPO().getClusterUrl())
				 .withOauthToken(appEnvClusterContext.getClusterPO().getAuthToken())
				 .build();
		 
		 KubernetesClient client = new KubernetesClientBuilder()
				 .withConfig(config)
				 .build();
		 
		 SSHContext context = new SSHContext();
		 context.setClient(client);
		 context.setNamespace(appEnvClusterContext.getAppEnvPO().getNamespaceName());
		 context.setReplicaName(replicaName);
		 
		return context;
	}
	
}