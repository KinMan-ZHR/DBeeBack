package kinman.dbee.infrastructure.strategy.cluster.k8s;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentStrategy;
import io.fabric8.kubernetes.api.model.apps.RollingUpdateDeployment;
import kinman.dbee.api.enums.*;
import kinman.dbee.api.response.model.*;
import kinman.dbee.api.response.model.AppEnv.EnvExtendSpringBoot;
import kinman.dbee.api.response.model.EnvHealth.Item;
import kinman.dbee.api.response.model.GlobalConfigAgg.TraceTemplate;
import kinman.dbee.infrastructure.repository.po.AffinityTolerationPO;
import kinman.dbee.infrastructure.repository.po.AppEnvPO;
import kinman.dbee.infrastructure.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class K8sDeploymentHelper {

	private static final Logger logger = LoggerFactory.getLogger(K8sDeploymentHelper.class);
	
	public static Deployment build(DeploymentContext context) {
		Deployment deployment = new DeploymentBuilder()
	            .withNewMetadata()
	            .withName(context.getDeploymentName())
	            .withLabels(K8sUtils.dbeeLabel(context.getDeploymentName()))
	            .endMetadata()
	            .withNewSpec()
	            .withNewSelector()
	            .addToMatchLabels(K8sUtils.DBEE_LABEL_KEY, context.getDeploymentName())
	            .endSelector()
	            .withReplicas(context.getAppEnv().getMinReplicas())
	            .withNewTemplate()
	            .withNewMetadata()
	            .addToLabels(deploymentLabel(context))
	            .withAnnotations(PrometheusHelper.addPrometheus("Pod", context))
	            .endMetadata()
	            .withNewSpec()
	            .withImagePullSecrets(imagePullSecrets())
	            .withInitContainers(initContainer(context))
	            .withContainers(containers(context))
	            .withAffinity(affinity(context))
	    		.withTolerations(toleration(context))
	    		.withVolumes(volumes(context))
	            .endSpec()
	            .endTemplate()
	            .withStrategy(deploymentStrategy())
	            .endSpec()
	            .build();
		
		return deployment;
	}
	
	private static DeploymentStrategy deploymentStrategy() {
		IntOrString size = new IntOrString("25%");
		RollingUpdateDeployment ru = new RollingUpdateDeployment();
		ru.setMaxSurge(size);
		ru.setMaxUnavailable(size);
		
		DeploymentStrategy s = new DeploymentStrategy();
		s.setRollingUpdate(ru);
		return s;
	}
	
	private static List<LocalObjectReference> imagePullSecrets() {
		LocalObjectReference r = new LocalObjectReference();
		r.setName(K8sUtils.DOCKER_REGISTRY_KEY);
		return Arrays.asList(r);
	}
	
	private static Affinity affinity(DeploymentContext context) {
		Affinity affinity = new Affinity();
		affinity.setNodeAffinity(nodeAffinity(context));
		affinity.setPodAffinity(podAffinity(context));
		affinityApp(affinity, context);
		affinity.setPodAntiAffinity(podAntiAffinity(context));
		return affinity;
	}
	
	private static NodeAffinity nodeAffinity(DeploymentContext context) {
		List<AffinityTolerationPO> nodeAffinitys = context.getAffinitys().stream()
				.filter(e -> SchedulingTypeEnum.NODE_AFFINITY.getCode().equals(e.getSchedulingType()))
				.collect(Collectors.toList());
		if(CollectionUtils.isEmpty(nodeAffinitys)) {
			return null;
		}
		
		NodeAffinity nodeAffinity = new NodeAffinity();
		
		//1.硬亲和
		List<AffinityTolerationPO> affinityForce = nodeAffinitys.stream()
				.filter(e -> AffinityLevelEnum.FORCE_AFFINITY.getCode().equals(e.getAffinityLevel()))
				.collect(Collectors.toList());
		if(!CollectionUtils.isEmpty(affinityForce)) {
			List<NodeSelectorRequirement> requirements = new ArrayList<>();
			for(AffinityTolerationPO affintiy : affinityForce) {
				NodeSelectorRequirement requirement = new NodeSelectorRequirement();
				requirement.setKey(affintiy.getKeyName());
				requirement.setOperator(affintiy.getOperator());
				if(!StringUtils.isBlank(affintiy.getValueList())) {
					requirement.setValues(Arrays.asList(affintiy.getValueList().split(",")));
				}
				requirements.add(requirement);
			}
			NodeSelectorTerm nodeSelectorTerm = new NodeSelectorTerm();
			nodeSelectorTerm.setMatchExpressions(requirements);
			NodeSelector nodeSelector = new NodeSelector();
			nodeSelector.setNodeSelectorTerms(Arrays.asList(nodeSelectorTerm));
			
			nodeAffinity.setRequiredDuringSchedulingIgnoredDuringExecution(nodeSelector);
		}
		
		//2.软亲和
		List<AffinityTolerationPO> affinitySoft = nodeAffinitys.stream()
				.filter(e -> AffinityLevelEnum.SOFT_AFFINITY.getCode().equals(e.getAffinityLevel()))
				.collect(Collectors.toList());
		if(!CollectionUtils.isEmpty(affinitySoft)) {
			List<PreferredSchedulingTerm> schedulingTerms = new ArrayList<>();
			for(AffinityTolerationPO affintiy : affinitySoft) {
				NodeSelectorRequirement requirement = new NodeSelectorRequirement();
				requirement.setKey(affintiy.getKeyName());
				requirement.setOperator(affintiy.getOperator());
				if(!StringUtils.isBlank(affintiy.getValueList())) {
					requirement.setValues(Arrays.asList(affintiy.getValueList().split(",")));
				}
				
				NodeSelectorTerm nodeSelectorTerm = new NodeSelectorTerm();
				nodeSelectorTerm.setMatchExpressions(Arrays.asList(requirement));
				
				NodeSelector nodeSelector = new NodeSelector();
				nodeSelector.setNodeSelectorTerms(Arrays.asList(nodeSelectorTerm));
				
				PreferredSchedulingTerm schedulingTerm = new PreferredSchedulingTerm();
				schedulingTerm.setPreference(nodeSelectorTerm);
				schedulingTerm.setWeight(Integer.valueOf(affintiy.getWeight()));
				schedulingTerms.add(schedulingTerm);
			}
			
			nodeAffinity.setPreferredDuringSchedulingIgnoredDuringExecution(schedulingTerms);
		}
		
		return nodeAffinity;
	}
	
	private static PodAffinity podAffinity(DeploymentContext context) {

		List<AffinityTolerationPO> affinitys = context.getAffinitys().stream()
				.filter(e -> SchedulingTypeEnum.REPLICA_AFFINITY.getCode().equals(e.getSchedulingType()))
				.collect(Collectors.toList());
		if(CollectionUtils.isEmpty(affinitys)) {
			return null;
		}
		
		PodAffinity affinity = new PodAffinity();
		
		//1.硬亲和
		List<AffinityTolerationPO> affinityForce = affinitys.stream()
				.filter(e -> AffinityLevelEnum.FORCE_AFFINITY.getCode().equals(e.getAffinityLevel()))
				.collect(Collectors.toList());
		if(!CollectionUtils.isEmpty(affinityForce)) {
			List<PodAffinityTerm> affinityTerms = new ArrayList<>();
			for(AffinityTolerationPO affintiy : affinityForce) {
				LabelSelectorRequirement requirement = new LabelSelectorRequirement();
				requirement.setKey(affintiy.getKeyName());
				requirement.setOperator(affintiy.getOperator());
				if(!StringUtils.isBlank(affintiy.getValueList())) {
					requirement.setValues(Arrays.asList(affintiy.getValueList().split(",")));
				}
				
				LabelSelector labelSelector = new LabelSelector();
				labelSelector.setMatchExpressions(Arrays.asList(requirement));
				
				PodAffinityTerm nodeSelectorTerm = new PodAffinityTerm();
				nodeSelectorTerm.setLabelSelector(labelSelector);
				nodeSelectorTerm.setTopologyKey(topologyKey(affintiy.getTopologyKey()));
				affinityTerms.add(nodeSelectorTerm);
			}
			
			affinity.setRequiredDuringSchedulingIgnoredDuringExecution(affinityTerms);
		}
		
		//2.软亲和
		List<AffinityTolerationPO> affinitySoft = affinitys.stream()
				.filter(e -> AffinityLevelEnum.SOFT_AFFINITY.getCode().equals(e.getAffinityLevel()))
				.collect(Collectors.toList());
		if(!CollectionUtils.isEmpty(affinitySoft)) {
			List<WeightedPodAffinityTerm> weightedTerms = new ArrayList<>();
			for(AffinityTolerationPO affintiy : affinitySoft) {
				LabelSelectorRequirement sr = new LabelSelectorRequirement();
				sr.setKey(affintiy.getKeyName());
				sr.setOperator(affintiy.getOperator());
				if(!StringUtils.isBlank(affintiy.getValueList())) {
					sr.setValues(Arrays.asList(affintiy.getValueList().split(",")));
				}
				
				LabelSelector labelSelector = new LabelSelector();
				labelSelector.setMatchExpressions(Arrays.asList(sr));
				
				PodAffinityTerm podAffinityTerm = new PodAffinityTerm();
				podAffinityTerm.setLabelSelector(labelSelector);
				podAffinityTerm.setTopologyKey(topologyKey(affintiy.getTopologyKey()));
				
				WeightedPodAffinityTerm weightedTerm = new WeightedPodAffinityTerm();
				weightedTerm.setPodAffinityTerm(podAffinityTerm);
				weightedTerm.setWeight(Integer.valueOf(affintiy.getWeight()));
				weightedTerms.add(weightedTerm);
			}
			
			affinity.setPreferredDuringSchedulingIgnoredDuringExecution(weightedTerms);
		}
		
		return affinity;
	
	}
	
	private static String topologyKey(String topologyKey) {
		return StringUtils.isBlank(topologyKey) ? K8sUtils.DEFAULT_TOPOLOGY_KEY : topologyKey;
	}
	
	private static PodAntiAffinity podAntiAffinity(DeploymentContext context) {

		List<AffinityTolerationPO> affinitys = context.getAffinitys().stream()
				.filter(e -> SchedulingTypeEnum.REPLICA_ANTI_AFFINITY.getCode().equals(e.getSchedulingType()))
				.collect(Collectors.toList());
		if(CollectionUtils.isEmpty(affinitys)) {
			return null;
		}
		
		PodAntiAffinity affinity = new PodAntiAffinity();
		
		//1.硬亲和
		List<AffinityTolerationPO> affinityForce = affinitys.stream()
				.filter(e -> AffinityLevelEnum.FORCE_AFFINITY.getCode().equals(e.getAffinityLevel()))
				.collect(Collectors.toList());
		if(!CollectionUtils.isEmpty(affinityForce)) {
			List<PodAffinityTerm> affinityTerms = new ArrayList<>();
			for(AffinityTolerationPO affintiy : affinityForce) {
				LabelSelectorRequirement requirement = new LabelSelectorRequirement();
				requirement.setKey(affintiy.getKeyName());
				requirement.setOperator(affintiy.getOperator());
				if(!StringUtils.isBlank(affintiy.getValueList())) {
					requirement.setValues(Arrays.asList(affintiy.getValueList().split(",")));
				}
				
				LabelSelector labelSelector = new LabelSelector();
				labelSelector.setMatchExpressions(Arrays.asList(requirement));
				
				PodAffinityTerm nodeSelectorTerm = new PodAffinityTerm();
				nodeSelectorTerm.setLabelSelector(labelSelector);
				nodeSelectorTerm.setTopologyKey(topologyKey(affintiy.getTopologyKey()));
				affinityTerms.add(nodeSelectorTerm);
			}
			
			affinity.setRequiredDuringSchedulingIgnoredDuringExecution(affinityTerms);
		}
		
		//2.软亲和
		List<AffinityTolerationPO> affinitySoft = affinitys.stream()
				.filter(e -> AffinityLevelEnum.SOFT_AFFINITY.getCode().equals(e.getAffinityLevel()))
				.collect(Collectors.toList());
		if(!CollectionUtils.isEmpty(affinitySoft)) {
			List<WeightedPodAffinityTerm> weightedTerms = new ArrayList<>();
			for(AffinityTolerationPO affintiy : affinitySoft) {
				LabelSelectorRequirement sr = new LabelSelectorRequirement();
				sr.setKey(affintiy.getKeyName());
				sr.setOperator(affintiy.getOperator());
				if(!StringUtils.isBlank(affintiy.getValueList())) {
					sr.setValues(Arrays.asList(affintiy.getValueList().split(",")));
				}
				
				LabelSelector labelSelector = new LabelSelector();
				labelSelector.setMatchExpressions(Arrays.asList(sr));
				
				PodAffinityTerm podAffinityTerm = new PodAffinityTerm();
				podAffinityTerm.setLabelSelector(labelSelector);
				podAffinityTerm.setTopologyKey(topologyKey(affintiy.getTopologyKey()));
				
				WeightedPodAffinityTerm weightedTerm = new WeightedPodAffinityTerm();
				weightedTerm.setPodAffinityTerm(podAffinityTerm);
				weightedTerm.setWeight(Integer.valueOf(affintiy.getWeight()));
				weightedTerms.add(weightedTerm);
			}
			
			affinity.setPreferredDuringSchedulingIgnoredDuringExecution(weightedTerms);
		}
		
		return affinity;
	
	}
	
	private static void affinityApp(Affinity affinity, DeploymentContext context) {
		List<String> affinityNames = context.getApp().getAffinityAppNames();
		if(CollectionUtils.isEmpty(affinityNames)) {
			return;
		}
		
		List<String> affinityValues = new ArrayList<>();
		for(String appName : affinityNames) {
			affinityValues.add(K8sUtils.getDeploymentName(appName, context.getAppEnv().getTag()));
		}
		
		LabelSelectorRequirement sr = new LabelSelectorRequirement();
		sr.setKey(K8sUtils.DBEE_LABEL_KEY);
		sr.setOperator("In");
		sr.setValues(affinityValues);
		
		LabelSelector labelSelector = new LabelSelector();
		labelSelector.setMatchExpressions(Arrays.asList(sr));
		
		PodAffinityTerm podAffinityTerm = new PodAffinityTerm();
		podAffinityTerm.setLabelSelector(labelSelector);
		podAffinityTerm.setTopologyKey(K8sUtils.DEFAULT_TOPOLOGY_KEY);
		
		WeightedPodAffinityTerm weightedTerm = new WeightedPodAffinityTerm();
		weightedTerm.setWeight(100);
		weightedTerm.setPodAffinityTerm(podAffinityTerm);
		
		PodAffinity podAffinity = affinity.getPodAffinity();
		if(podAffinity == null) {
			podAffinity = new PodAffinity();
			affinity.setPodAffinity(podAffinity);
		}
		List<WeightedPodAffinityTerm> terms = podAffinity.getPreferredDuringSchedulingIgnoredDuringExecution();
		if(CollectionUtils.isEmpty(terms)) {
			podAffinity.setPreferredDuringSchedulingIgnoredDuringExecution(Arrays.asList(weightedTerm));
		}else {
			terms.add(weightedTerm);
		}
	}
	
	private static List<Toleration> toleration(DeploymentContext context) {
		List<AffinityTolerationPO> configs = context.getAffinitys().stream()
				.filter(e -> SchedulingTypeEnum.NODE_TOLERATION.getCode().equals(e.getSchedulingType()))
				.collect(Collectors.toList());
		if(CollectionUtils.isEmpty(configs)) {
			return null;
		}
		
		List<Toleration> tolerations = new ArrayList<>();
		for(AffinityTolerationPO c : configs) {
			Toleration t = new Toleration();
			t.setKey(c.getKeyName());
			t.setOperator(c.getOperator());
			t.setValue(c.getValueList());
			t.setEffect(c.getEffectType());
			t.setTolerationSeconds(StringUtils.isBlank(c.getDuration()) ? null : Long.valueOf(c.getDuration()));
			tolerations.add(t);
		}
		
		return tolerations;
	}
	
	private static List<Container> containers(DeploymentContext context) {
		AppEnvPO appEnvPO = context.getAppEnv();
		Container container = new Container();
		container.setName(context.getDeploymentName());
		containerOfJar(context, container);
		//Vue、React、Html应用会通过Nginx来启动服务
		containerOfNginx(context, container);
		containerOfNodejs(context, container);
		envVars(context, container);
		container.setImagePullPolicy("Always");
		
		//主端口
		ContainerPort servicePort = new ContainerPort();
		servicePort.setName("major");
		servicePort.setContainerPort(appEnvPO.getServicePort());
		List<ContainerPort> ports = new ArrayList<>();
		ports.add(servicePort);
		
		//辅助端口
		if(!StringUtils.isBlank(appEnvPO.getMinorPorts())) {
			String[] portStr = appEnvPO.getMinorPorts().split(",");
			for(int i = 0; i < portStr.length; i++) {
				ContainerPort containerPort = new ContainerPort();
				containerPort.setName("minor" + (i + 1));
				containerPort.setContainerPort(Integer.valueOf(portStr[i]));
				ports.add(containerPort);
			}
		}
		
		container.setPorts(ports);
		
		// 设置资源
		Quantity cpu = new Quantity(new BigDecimal(appEnvPO.getReplicaCpu()).movePointLeft(3).toPlainString());
		Quantity memory = new Quantity(appEnvPO.getReplicaMemory() + "Mi");
		Map<String, Quantity> requests = new HashMap<>();
		requests.put("cpu", cpu);
		requests.put("memory", memory);
		Map<String, Quantity> limits = new HashMap<>();
		limits.put("cpu", cpu);
		limits.put("memory", memory);
		ResourceRequirements resources = new ResourceRequirements();
		resources.setRequests(requests);
		resources.setLimits(limits);
		container.setResources(resources);
		container.setVolumeMounts(volumeMounts(context));
		probe(container, context);
		lifecycle(container, context);
		
		return Arrays.asList(container);
	}
	
	private static void containerOfJar(DeploymentContext context, Container container) {
		if(!jarFileType(context.getApp())) {
			return;
		}
		container.setImage(context.getFullNameOfImage());
		commandsOfJar(container, context);
		argsOfJar(container, context);
	}
	
	private static void containerOfNginx(DeploymentContext context, Container container) {
		if(!nginxApp(context.getApp())) {
			return;
		}
		container.setImage(nginxImage(context));
	}
	
	private static void containerOfNodejs(DeploymentContext context, Container container) {
		if(!nodejsApp(context.getApp())) {
			return;
		}
		String startFile = ((AppExtendNodejs)context.getApp().getAppExtend()).getStartFile();
		if(StringUtils.isBlank(startFile)) {
			startFile = "index.js";
		}
		String appName = context.getApp().getAppName();
		String commands = "export NODE_ENV=" + context.getAppEnv().getTag() +
				" && export HOST=0.0.0.0" + " && export PORT=" + context.getAppEnv().getServicePort() +
				" && cd " + Constants.USR_LOCAL_HOME +
				" && tar zxf " + appName + ".tar.gz" +
				" && cd " + appName + " && exec node " + startFile;
		container.setCommand(Arrays.asList("sh", "-c", commands));
		container.setImage(context.getFullNameOfImage());
	}

	private static void envVars(DeploymentContext context, Container container) {
		List<EnvVar> envVars = new ArrayList<>();
		EnvVar envVar = new EnvVar();
		envVar.setName("TZ");
		envVar.setValue("Asia/Shanghai");
		envVars.add(envVar);
		container.setEnv(envVars);
		containerOfWar(context, container);
	}
	
	private static void containerOfWar(DeploymentContext context, Container container) {
		if(!warFileType(context.getApp())) {
			return;
		}
		container.setImage(context.getApp().getBaseImage());
		
		//Dbee定义的Jvm参数
		StringBuilder argsStr = new StringBuilder();
		List<String> jvmArgsOfDbee = jvmArgsOfDbee(context);
		for(String arg : jvmArgsOfDbee) {
			argsStr.append(" ").append(arg);
		}
		//用户定义的Jvm参数
		EnvExtendSpringBoot envExtend = context.getEnvExtend();
		if(envExtend != null && !StringUtils.isBlank(envExtend.getJvmArgs())) {
			argsStr.append(" ").append(envExtend.getJvmArgs());
		}
		EnvVar envVar = new EnvVar();
		envVar.setName("JAVA_OPTS");
		envVar.setValue(argsStr.toString());
		container.getEnv().add(envVar);
	}

	private static void lifecycle(Container container, DeploymentContext context) {
		if(context.getEnvLifecycle() == null) {
			return;
		}
		Lifecycle lifecycle = new Lifecycle();
		lifecycle.setPostStart(lifecycleHandler(context.getEnvLifecycle().getPostStart(), context));
		lifecycle.setPreStop(lifecycleHandler(context.getEnvLifecycle().getPreStop(), context));
		container.setLifecycle(lifecycle);
	}

	private static void probe(Container container, DeploymentContext context) {
		if(context.getAppEnv().getServicePort() == null) {
			return;
		}
		startupProbe(container, context);
		readinessProbe(container, context);
		livenessProbe(container, context);
	}
	
	private static void startupProbe(Container container, DeploymentContext context) {
		Item item = null;
		if(context.getEnvHealth() != null) {
			item = context.getEnvHealth().getStartup();
		}
		Probe probe = new Probe();
		probeAction(probe, item, context);
		probe.setInitialDelaySeconds(item != null && item.getInitialDelay() != null ? item.getInitialDelay() : 6);
		probe.setPeriodSeconds(item != null && item.getPeriod() != null ? item.getPeriod() : 1);
		probe.setTimeoutSeconds(item != null && item.getTimeout() != null ? item.getTimeout() : 1);
		probe.setSuccessThreshold(item != null && item.getSuccessThreshold() != null ? item.getSuccessThreshold() : 1);
		//默认检查300次（300秒）以后仍然没有启动，则重启服务
		probe.setFailureThreshold(item != null && item.getFailureThreshold() != null ? item.getFailureThreshold() : 300);
		container.setStartupProbe(probe);
	}
	
	private static void readinessProbe(Container container, DeploymentContext context) {
		Item item = null;
		if(context.getEnvHealth() != null) {
			item = context.getEnvHealth().getReadiness();
		}
		Probe probe = new Probe();
		probeAction(probe, item, context);
		probe.setInitialDelaySeconds(item != null && item.getInitialDelay() != null ? item.getInitialDelay() : 6);
		probe.setPeriodSeconds(item != null && item.getPeriod() != null ? item.getPeriod() : 5);
		probe.setTimeoutSeconds(item != null && item.getTimeout() != null ? item.getTimeout() : 1);
		probe.setSuccessThreshold(item != null && item.getSuccessThreshold() != null ? item.getSuccessThreshold() : 1);
		probe.setFailureThreshold(item != null && item.getFailureThreshold() != null ? item.getFailureThreshold() : 300);
		container.setReadinessProbe(probe);
	}
	
	private static void livenessProbe(Container container, DeploymentContext context) {
		Item item = null;
		if(context.getEnvHealth() != null) {
			item = context.getEnvHealth().getLiveness();
		}
		Probe probe = new Probe();
		probeAction(probe, item, context);
		probe.setInitialDelaySeconds(item != null && item.getInitialDelay() != null ? item.getInitialDelay() : 30);
		probe.setPeriodSeconds(item != null && item.getPeriod() != null ? item.getPeriod() : 5);
		probe.setTimeoutSeconds(item != null && item.getTimeout() != null ? item.getTimeout() : 1);
		probe.setSuccessThreshold(item != null && item.getSuccessThreshold() != null ? item.getSuccessThreshold() : 1);
		probe.setFailureThreshold(item != null && item.getFailureThreshold() != null ? item.getFailureThreshold() : 3);
		container.setLivenessProbe(probe);
	}
	
	private static void probeAction(Probe probe, EnvHealth.Item item, DeploymentContext context) {
		if(item == null || item.getActionType() == null) {
			TCPSocketAction action = new TCPSocketAction();
			action.setPort(new IntOrString(context.getAppEnv().getServicePort()));
			probe.setTcpSocket(action);
			return;
		}
		if(ActionTypeEnum.HTTP_GET.getCode().equals(item.getActionType())){
			HTTPGetAction action = new HTTPGetAction();
			action.setPath(item.getAction());
			action.setPort(new IntOrString(context.getAppEnv().getServicePort()));
			probe.setHttpGet(action);
			return;
		}
		if(ActionTypeEnum.TCP.getCode().equals(item.getActionType())) {
			TCPSocketAction action = new TCPSocketAction();
			action.setPort(new IntOrString(Integer.parseInt(item.getAction())));
			probe.setTcpSocket(action);
			return;
		}
		if(ActionTypeEnum.EXEC.getCode().equals(item.getActionType())){
			ExecAction action = new ExecAction();
			action.setCommand(Arrays.asList("/bin/sh", "-c", item.getAction()));
			probe.setExec(action);
		}
	}
	
	private static LifecycleHandler lifecycleHandler(EnvLifecycle.Item item, DeploymentContext context) {
		if(item == null || item.getActionType() == null) {
			return null;
		}
		LifecycleHandler handler = new LifecycleHandler();
		if(ActionTypeEnum.HTTP_GET.getCode().equals(item.getActionType())){
			HTTPGetAction action = new HTTPGetAction();
			action.setPath(item.getAction());
			action.setPort(new IntOrString(context.getAppEnv().getServicePort()));
			handler.setHttpGet(action);
		}else if(ActionTypeEnum.TCP.getCode().equals(item.getActionType())) {
			TCPSocketAction action = new TCPSocketAction();
			action.setPort(new IntOrString(Integer.parseInt(item.getAction())));
			handler.setTcpSocket(action);
		}else if(ActionTypeEnum.EXEC.getCode().equals(item.getActionType())){
			ExecAction action = new ExecAction();
			action.setCommand(Arrays.asList("/bin/sh", "-c", item.getAction()));
			handler.setExec(action);
		}
		return handler;
	}
	
	/**
	 *   使用Jib通过Jdk的安装目录构建的Jdk镜像，缺少java命令的执行权限，故首先进行赋权。
	 *   需要执行多条shell指令，因此只能使用sh -c模式。
	 */
	private static void commandsOfJar(Container container, DeploymentContext context){
		StringBuilder commands = new StringBuilder();
		commands.append("chmod +x $JAVA_HOME/bin/java");
		commands.append(" && exec $JAVA_HOME/bin/java");
		//Dbee定义的Jvm参数
		List<String> jvmArgsOfDbee = jvmArgsOfDbee(context);
		for(String arg : jvmArgsOfDbee) {
			commands.append(" ").append(arg);
		}
		//用户自定义Jvm参数
		EnvExtendSpringBoot envExtend = context.getEnvExtend();
		if(envExtend != null && !StringUtils.isBlank(envExtend.getJvmArgs())) {
			String[] jvmArgs = envExtend.getJvmArgs().split("\\s+");
			for (String arg : jvmArgs) {
				commands.append(" ").append(arg);
			}
		}
		commands.append(" ").append("-jar");
		String packageFileType = PackageFileTypeEnum.getByCode(((AppExtendJava)context.getApp()
				.getAppExtend()).getPackageFileType()).getValue();
		commands.append(" ").append(Constants.USR_LOCAL_HOME + context.getApp().getAppName() + "." + packageFileType);
		
		container.setCommand(Arrays.asList("sh", "-c", commands.toString()));
	}
	
	private static List<String> jvmArgsOfDbee(DeploymentContext context) {
		List<String> args = new ArrayList<>();
		args.add("-Duser.timezone=Asia/Shanghai");
		args.add("-Denv=" + context.getAppEnv().getTag());
		
		//dbee-agent参数
		if(TechTypeEnum.SPRING_BOOT.getCode().equals(context.getApp().getTechType())
				&& !StringUtils.isBlank(context.getAppEnv().getExt())
				&& YesOrNoEnum.YES.getCode().equals(JsonUtils.parseToObject(context.getAppEnv().getExt(),
						EnvExtendSpringBoot.class).getJvmMetricsStatus())) {
			args.add("-javaagent:"+ K8sUtils.AGENT_VOLUME_PATH +"dbee-agent.jar");
		}
		
		//skywalking-agent参数
		if(!YesOrNoEnum.YES.getCode().equals(context.getAppEnv().getTraceStatus())) {
			return args;
		}
		TraceTemplate traceTemplate = context.getGlobalConfigAgg().getTraceTemplate(context.getAppEnv().getTraceTemplateId());
		if(traceTemplate == null) {
			LogUtils.throwException(logger, MessageCodeEnum.TRACE_TEMPLATE_IS_EMPTY);
		}
		args.add("-javaagent:"+ K8sUtils.AGENT_VOLUME_PATH +"skywalking-agent/skywalking-agent.jar");
		args.add("-Dskywalking.collector.backend_service=" + traceTemplate.getServiceUrl());
		args.add("-Dskywalking.agent.service_name=" + context.getApp().getAppName());
		return args;
	}
	
	private static void argsOfJar(Container container, DeploymentContext context){
		List<String> args = new ArrayList<>();
		args.add("--server.port=" + context.getAppEnv().getServicePort());
		container.setArgs(args);
	}
	
	private static List<Container> initContainer(DeploymentContext context) {
		List<Container> containers = new ArrayList<>();
		initContainerOfWar(context, containers);
		initContainerOfTraceAgent(context, containers);
		initContainerOfDbeeAgent(context, containers);
		//Vue、React、Html应用会通过Nginx来启动服务
		initContainerOfNginx(context, containers);
		return containers;
	}
	
	private static String nginxImage(DeploymentContext context) {
		if(ImageSourceEnum.VERSION.getCode().equals(context.getApp().getBaseImageSource())) {
			String v = NginxVersionEnum.getByCode(context.getApp().getBaseImageVersion()).getValue();
			return String.format(Constants.NGINX_IMAGE_URL, v);
		}
		return context.getApp().getBaseImage();
	}
	
	private static void initContainerOfTraceAgent(DeploymentContext context, List<Container> containers) {
		if(!TechTypeEnum.SPRING_BOOT.getCode().equals(context.getApp().getTechType())) {
			return;
		}
		if(YesOrNoEnum.NO.getCode().equals(context.getAppEnv().getTraceStatus())) {
			return;
		}
		
		TraceTemplate traceTemplate = context.getGlobalConfigAgg().getTraceTemplate(context.getAppEnv().getTraceTemplateId());
		if(traceTemplate == null) {
			LogUtils.throwException(logger, MessageCodeEnum.TRACE_TEMPLATE_IS_EMPTY);
		}
		
		Container initContainer = new Container();
		initContainer.setName("skywalking-agent");
		initContainer.setImage(context.getFullNameOfTraceAgentImage());
		initContainer.setImagePullPolicy("Always");
		initContainer.setCommand(Arrays.asList("/bin/sh", "-c"));
		initContainer.setArgs(Arrays.asList("cp -rf /skywalking-agent " + K8sUtils.AGENT_VOLUME_PATH));
		
		VolumeMount volumeMount = new VolumeMount();
		volumeMount.setMountPath(K8sUtils.AGENT_VOLUME_PATH);
		volumeMount.setName(K8sUtils.AGENT_VOLUME);
		initContainer.setVolumeMounts(Arrays.asList(volumeMount));
		containers.add(initContainer);
	}
	
	private static void initContainerOfDbeeAgent(DeploymentContext context, List<Container> containers) {
		if(!TechTypeEnum.SPRING_BOOT.getCode().equals(context.getApp().getTechType())) {
			return;
		}
		
		if(context.getEnvExtend() == null) {
			return;
		}
		
		if(YesOrNoEnum.NO.getCode().equals(((EnvExtendSpringBoot)context.getEnvExtend()).getJvmMetricsStatus())) {
			return;
		}
		
		Container initContainer = new Container();
		initContainer.setName("dbee-agent");
		initContainer.setImage(context.getFullNameOfDbeeAgentImage());
		initContainer.setImagePullPolicy("Always");
		initContainer.setCommand(Arrays.asList("/bin/sh", "-c"));
		initContainer.setArgs(Arrays.asList("cp -rf " + Constants.USR_LOCAL_HOME
				+ "dbee-agent-*.jar " + K8sUtils.AGENT_VOLUME_PATH + "dbee-agent.jar"));
		
		VolumeMount volumeMount = new VolumeMount();
		volumeMount.setMountPath(K8sUtils.AGENT_VOLUME_PATH);
		volumeMount.setName(K8sUtils.AGENT_VOLUME);
		initContainer.setVolumeMounts(Arrays.asList(volumeMount));
		containers.add(initContainer);
	}
	
	private static void initContainerOfWar(DeploymentContext context, List<Container> containers) {
		if(!warFileType(context.getApp())) {
			return;
		}
		
		Container container = new Container();
		container.setName("war");
		container.setImage(context.getFullNameOfImage());
		container.setImagePullPolicy("Always");
		container.setCommand(Arrays.asList("/bin/sh", "-c"));
		String warFile = Constants.USR_LOCAL_HOME + context.getApp().getAppName() + "." + PackageFileTypeEnum.WAR.getValue();
		container.setArgs(Arrays.asList("cp -rf " + warFile + " " + K8sUtils.TOMCAT_APP_PATH));
		
		VolumeMount volumeMount = new VolumeMount();
		volumeMount.setMountPath(K8sUtils.TOMCAT_APP_PATH);
		volumeMount.setName(K8sUtils.WAR_VOLUME);
		container.setVolumeMounts(Arrays.asList(volumeMount));
		containers.add(container);
	}
	
	private static void initContainerOfNginx(DeploymentContext context, List<Container> containers) {
		if(!nginxApp(context.getApp())) {
			return;
		}
		
		Container container = new Container();
		container.setName(Constants.NGINX);
		container.setImage(context.getFullNameOfImage());
		container.setImagePullPolicy("Always");
		container.setCommand(Arrays.asList("/bin/sh", "-c"));
		String file = Constants.USR_LOCAL_HOME + context.getApp().getAppName();
		container.setArgs(Arrays.asList("cp -rf " + file + "/* " + K8sUtils.NGINX_VOLUME_PATH));
		
		VolumeMount volumeMount = new VolumeMount();
		volumeMount.setMountPath(K8sUtils.NGINX_VOLUME_PATH);
		volumeMount.setName(K8sUtils.NGINX_VOLUME);
		container.setVolumeMounts(Arrays.asList(volumeMount));
		containers.add(container);
	}
	
	private static boolean warFileType(App app) {
		return springBootApp(app) && PackageFileTypeEnum.WAR.getCode()
					.equals(((AppExtendJava)app.getAppExtend()).getPackageFileType());
	}
	
	private static boolean jarFileType(App app) {
		return springBootApp(app) && PackageFileTypeEnum.JAR.getCode()
					.equals(((AppExtendJava)app.getAppExtend()).getPackageFileType());
	}
	
	private static boolean springBootApp(App app) {
		return TechTypeEnum.SPRING_BOOT.getCode().equals(app.getTechType());
	}
	

	/**
	 * Nginx服务，如：Vue、React、Html
	 */
	private static boolean nginxApp(App app) {
		return TechTypeEnum.VUE.getCode().equals(app.getTechType())
				|| TechTypeEnum.REACT.getCode().equals(app.getTechType())
				|| TechTypeEnum.HTML.getCode().equals(app.getTechType());
	}

	private static boolean nodejsApp(App app) {
		return TechTypeEnum.NODEJS.getCode().equals(app.getTechType());
	}

	private static List<VolumeMount> volumeMounts(DeploymentContext context) {
		List<VolumeMount> volumeMounts = new ArrayList<>();
		
		//指定时区
		VolumeMount volumeMountTime = new VolumeMount();
		volumeMountTime.setName("timezone");
		volumeMountTime.setMountPath("/etc/localtime");
		volumeMounts.add(volumeMountTime);
		
		//data目录
		VolumeMount config = new VolumeMount();
		config.setName(K8sUtils.DATA_VOLUME);
		config.setMountPath(K8sUtils.DATA_PATH);
		volumeMounts.add(config);
		
		//临时data目录，用于下载文件等
		VolumeMount volumeMountData = new VolumeMount();
		volumeMountData.setMountPath(K8sUtils.TMP_DATA_PATH);
		volumeMountData.setName(K8sUtils.TMP_DATA_VOLUME);
		volumeMounts.add(volumeMountData);
		
		VolumeMount dbeeAgent = new VolumeMount();
		dbeeAgent.setMountPath(K8sUtils.AGENT_VOLUME_PATH);
		dbeeAgent.setName(K8sUtils.AGENT_VOLUME);
		volumeMounts.add(dbeeAgent);
		
		//war
		if(warFileType(context.getApp())) {
			VolumeMount volumeMountWar = new VolumeMount();
			volumeMountWar.setMountPath(K8sUtils.TOMCAT_APP_PATH);
			volumeMountWar.setName(K8sUtils.WAR_VOLUME);
			volumeMounts.add(volumeMountWar);
		}
		
		//Node
		if(nginxApp(context.getApp())) {
			VolumeMount volumeMountWar = new VolumeMount();
			volumeMountWar.setMountPath(K8sUtils.NGINX_VOLUME_PATH);
			volumeMountWar.setName(K8sUtils.NGINX_VOLUME);
			volumeMounts.add(volumeMountWar);
		}
		
		return volumeMounts;
	}

	private static List<Volume> volumes(DeploymentContext context) {
		List<Volume> volumes = new ArrayList<>();
		
		Volume volumeTime = new Volume();
		volumeTime.setName("timezone");
		HostPathVolumeSource hostPathVolumeSource = new HostPathVolumeSource();
		hostPathVolumeSource.setPath("/etc/localtime");
		volumeTime.setHostPath(hostPathVolumeSource);
		volumes.add(volumeTime);
		
		Volume config = new Volume();
		config.setName(K8sUtils.DATA_VOLUME);
		ConfigMapVolumeSource s = new ConfigMapVolumeSource();
		s.setName(K8sUtils.DBEE_CONFIGMAP_NAME);
		config.setConfigMap(s);
		volumes.add(config);
		
		//临时data目录，用于下载文件等
		Volume volumeData = new Volume();
		volumeData.setName(K8sUtils.TMP_DATA_VOLUME);
		EmptyDirVolumeSource emptyDirData = new EmptyDirVolumeSource();
		volumeData.setEmptyDir(emptyDirData);
		volumes.add(volumeData);
		
		Volume dbeeAgent = new Volume();
		dbeeAgent.setName(K8sUtils.AGENT_VOLUME);
		EmptyDirVolumeSource emptyDir = new EmptyDirVolumeSource();
		dbeeAgent.setEmptyDir(emptyDir);
		volumes.add(dbeeAgent);
		
		//War
		if(warFileType(context.getApp())) {
			Volume volumeWar = new Volume();
			volumeWar.setName(K8sUtils.WAR_VOLUME);
			EmptyDirVolumeSource emptyDirWar = new EmptyDirVolumeSource();
			volumeWar.setEmptyDir(emptyDirWar);
			volumes.add(volumeWar);
		}
		
		//Nginx服务，如：Vue、React、Html
		if(nginxApp(context.getApp())) {
			Volume volumeWar = new Volume();
			volumeWar.setName(K8sUtils.NGINX_VOLUME);
			EmptyDirVolumeSource emptyDirWar = new EmptyDirVolumeSource();
			volumeWar.setEmptyDir(emptyDirWar);
			volumes.add(volumeWar);
		}
		
		return volumes;
	}
	
	public static Map<String, String> deploymentLabel(DeploymentContext context) {
		Map<String, String> labels = K8sUtils.dbeeLabel(context.getDeploymentName());
		labels.put("version", String.valueOf(System.currentTimeMillis()));
		return labels;
	}
}