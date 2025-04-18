package kinman.dbee.infrastructure.utils;

import java.util.Date;
import java.util.List;

import kinman.dbee.api.enums.EventTypeEnum;
import kinman.dbee.api.response.model.App;
import kinman.dbee.api.response.model.AppEnv.EnvExtend;
import kinman.dbee.api.response.model.EnvHealth;
import kinman.dbee.api.response.model.EnvLifecycle;
import kinman.dbee.api.response.model.EnvPrometheus;
import kinman.dbee.api.response.model.GlobalConfigAgg;
import kinman.dbee.infrastructure.component.ComponentConstants;
import kinman.dbee.infrastructure.strategy.cluster.ClusterStrategy;
import kinman.dbee.infrastructure.strategy.repo.CodeRepoStrategy;
import kinman.dbee.infrastructure.repository.po.AffinityTolerationPO;
import kinman.dbee.infrastructure.repository.po.AppEnvPO;
import kinman.dbee.infrastructure.repository.po.ClusterPO;

/**
 * 
 * 部署分支模型
 *
 */
public class DeploymentContext {

	// 提交人
	private String submitter;

	// 审批人
	private String approver;

	private GlobalConfigAgg globalConfigAgg;

	private ClusterPO cluster;

	private ComponentConstants componentConstants;

	private App app;

	private String branchName;

	private AppEnvPO appEnv;

	private EnvExtend envExtend;

	private EnvHealth envHealth;

	private EnvLifecycle envLifecycle;

	private EnvPrometheus envPrometheus;

	private List<AffinityTolerationPO> affinitys;

	private CodeRepoStrategy codeRepoStrategy;

	private ClusterStrategy clusterStrategy;

	private String localPathOfBranch;

	private String versionName;

	private String fullNameOfImage;

	private String id;

	private Date startTime;

	private String deploymentName;

	/**
	 * 事件类型
	 */
	private EventTypeEnum eventType;

	/**
	 * 日志文件路径
	 */
	private String logFilePath;

	/**
	 * 链路追踪镜像名称
	 */
	private String fullNameOfTraceAgentImage;

	/**
	 * Dbee代理镜像名称
	 */
	private String fullNameOfDbeeAgentImage;

	public String getSubmitter() {
		return submitter;
	}

	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}

	public String getApprover() {
		return approver;
	}

	public void setApprover(String approver) {
		this.approver = approver;
	}

	public EnvPrometheus getEnvPrometheus() {
		return envPrometheus;
	}

	public void setEnvPrometheus(EnvPrometheus envPrometheus) {
		this.envPrometheus = envPrometheus;
	}

	public ClusterPO getCluster() {
		return cluster;
	}

	public void setCluster(ClusterPO cluster) {
		this.cluster = cluster;
	}

	@SuppressWarnings("unchecked")
	public <T extends EnvExtend> T getEnvExtend() {
		return (T) envExtend;
	}

	public void setEnvExtend(EnvExtend envExtend) {
		this.envExtend = envExtend;
	}

	public String getDeploymentName() {
		return deploymentName;
	}

	public void setDeploymentName(String deploymentName) {
		this.deploymentName = deploymentName;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public EnvHealth getEnvHealth() {
		return envHealth;
	}

	public void setEnvHealth(EnvHealth envHealth) {
		this.envHealth = envHealth;
	}

	public EnvLifecycle getEnvLifecycle() {
		return envLifecycle;
	}

	public void setEnvLifecycle(EnvLifecycle envLifecycle) {
		this.envLifecycle = envLifecycle;
	}

	public List<AffinityTolerationPO> getAffinitys() {
		return affinitys;
	}

	public void setAffinitys(List<AffinityTolerationPO> affinitys) {
		this.affinitys = affinitys;
	}

	public EventTypeEnum getEventType() {
		return eventType;
	}

	public void setEventType(EventTypeEnum eventType) {
		this.eventType = eventType;
	}

	public GlobalConfigAgg getGlobalConfigAgg() {
		return globalConfigAgg;
	}

	public void setGlobalConfigAgg(GlobalConfigAgg globalConfigAgg) {
		this.globalConfigAgg = globalConfigAgg;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getFullNameOfImage() {
		return fullNameOfImage;
	}

	public void setFullNameOfImage(String fullNameOfImage) {
		this.fullNameOfImage = fullNameOfImage;
	}

	public String getLocalPathOfBranch() {
		return localPathOfBranch;
	}

	public void setLocalPathOfBranch(String localPathOfBranch) {
		this.localPathOfBranch = localPathOfBranch;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public App getApp() {
		return app;
	}

	public void setApp(App app) {
		this.app = app;
	}

	public AppEnvPO getAppEnv() {
		return appEnv;
	}

	public void setAppEnv(AppEnvPO appEnv) {
		this.appEnv = appEnv;
	}

	public ComponentConstants getComponentConstants() {
		return componentConstants;
	}

	public void setComponentConstants(ComponentConstants componentConstants) {
		this.componentConstants = componentConstants;
	}

	public CodeRepoStrategy getCodeRepoStrategy() {
		return codeRepoStrategy;
	}

	public void setCodeRepoStrategy(CodeRepoStrategy codeRepoStrategy) {
		this.codeRepoStrategy = codeRepoStrategy;
	}

	public ClusterStrategy getClusterStrategy() {
		return clusterStrategy;
	}

	public void setClusterStrategy(ClusterStrategy clusterStrategy) {
		this.clusterStrategy = clusterStrategy;
	}

	public String getLogFilePath() {
		return logFilePath;
	}

	public void setLogFilePath(String logFilePath) {
		this.logFilePath = logFilePath;
	}

	public String getFullNameOfTraceAgentImage() {
		return fullNameOfTraceAgentImage;
	}

	public void setFullNameOfTraceAgentImage(String fullNameOfTraceAgentImage) {
		this.fullNameOfTraceAgentImage = fullNameOfTraceAgentImage;
	}

	public String getFullNameOfDbeeAgentImage() {
		return fullNameOfDbeeAgentImage;
	}

	public void setFullNameOfDbeeAgentImage(String fullNameOfDbeeAgentImage) {
		this.fullNameOfDbeeAgentImage = fullNameOfDbeeAgentImage;
	}

}
