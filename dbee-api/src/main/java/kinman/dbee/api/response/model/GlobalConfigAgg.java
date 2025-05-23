package kinman.dbee.api.response.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalConfigAgg implements Serializable {

	private static final long serialVersionUID = 1L;

	private CodeRepo codeRepo;

	private ImageRepo imageRepo;

	private CAS cas;

	private Ldap ldap;

	private WeChat wechat;

	private DingDing dingding;

	private FeiShu feishu;

	private Maven maven;

	private Map<String, TraceTemplate> traceTemplates = new HashMap<>();

	private EnvTemplate envTemplate;

	private CustomizedMenu customizedMenu;

	private List<String> serverIps;

	private More more;

	public Maven getMaven() {
		return maven;
	}

	public void setMaven(Maven maven) {
		this.maven = maven;
	}

	public CodeRepo getCodeRepo() {
		return codeRepo;
	}

	public void setCodeRepo(CodeRepo codeRepo) {
		this.codeRepo = codeRepo;
	}

	public ImageRepo getImageRepo() {
		return imageRepo;
	}

	public void setImageRepo(ImageRepo imageRepo) {
		this.imageRepo = imageRepo;
	}

	public CustomizedMenu getCustomizedMenu() {
		return customizedMenu;
	}

	public void setCustomizedMenu(CustomizedMenu customizedMenu) {
		this.customizedMenu = customizedMenu;
	}

	public CAS getCas() {
		return cas;
	}

	public void setCas(CAS cas) {
		this.cas = cas;
	}

	public Ldap getLdap() {
		return ldap;
	}

	public void setLdap(Ldap ldap) {
		this.ldap = ldap;
	}

	public TraceTemplate getTraceTemplate(String id) {
		return traceTemplates.get(id);
	}

	public void setTraceTemplate(String id, TraceTemplate traceTemplate) {
		this.traceTemplates.put(id, traceTemplate);
	}

	public EnvTemplate getEnvTemplate() {
		return envTemplate;
	}

	public void setEnvTemplate(EnvTemplate envTemplate) {
		this.envTemplate = envTemplate;
	}

	public List<String> getServerIps() {
		return serverIps;
	}

	public void setServerIps(List<String> serverIps) {
		this.serverIps = serverIps;
	}

	public More getMore() {
		return more;
	}

	public void setMore(More more) {
		this.more = more;
	}

	public WeChat getWechat() {
		return wechat;
	}

	public void setWechat(WeChat wechat) {
		this.wechat = wechat;
	}

	public DingDing getDingding() {
		return dingding;
	}

	public void setDingding(DingDing dingding) {
		this.dingding = dingding;
	}

	public FeiShu getFeishu() {
		return feishu;
	}

	public void setFeishu(FeiShu feishu) {
		this.feishu = feishu;
	}

	public static abstract class BaseGlobalConfig implements Serializable {

		private static final long serialVersionUID = 1L;

		private String id;

		private Integer itemType;

		/**
		 * 创建时间
		 */
		private Date creationTime;

		/**
		 * 修改时间
		 */
		private Date updateTime;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public Integer getItemType() {
			return itemType;
		}

		public void setItemType(Integer itemType) {
			this.itemType = itemType;
		}

		public Date getCreationTime() {
			return creationTime;
		}

		public void setCreationTime(Date creationTime) {
			this.creationTime = creationTime;
		}

		public Date getUpdateTime() {
			return updateTime;
		}

		public void setUpdateTime(Date updateTime) {
			this.updateTime = updateTime;
		}

	}

	public static class ImageRepo extends BaseGlobalConfig {

		private static final long serialVersionUID = 1L;

		private String type;

		private String url;

		private String authName;

		private String authPassword;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getAuthName() {
			return authName;
		}

		public void setAuthName(String authName) {
			this.authName = authName;
		}

		public String getAuthPassword() {
			return authPassword;
		}

		public void setAuthPassword(String authPassword) {
			this.authPassword = authPassword;
		}

	}

	public static class CodeRepo extends BaseGlobalConfig {

		private static final long serialVersionUID = 1L;

		private GitHub gitHub;

		private String type;

		private GitLab gitLab;

		private Codeup codeup;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public GitHub getGitHub() {
			return gitHub;
		}

		public void setGitHub(GitHub gitHub) {
			this.gitHub = gitHub;
		}

		public GitLab getGitLab() {
			return gitLab;
		}

		public void setGitLab(GitLab gitLab) {
			this.gitLab = gitLab;
		}

		public Codeup getCodeup() {
			return codeup;
		}

		public void setCodeup(Codeup codeup) {
			this.codeup = codeup;
		}

		public static class GitLab {
			
			private String url;

			private Integer authType;

			private String authToken;

			private String authName;

			private String authPassword;

			public String getUrl() {
				return url;
			}

			public void setUrl(String url) {
				this.url = url;
			}

			public Integer getAuthType() {
				return authType;
			}

			public void setAuthType(Integer authType) {
				this.authType = authType;
			}

			public String getAuthToken() {
				return authToken;
			}

			public void setAuthToken(String authToken) {
				this.authToken = authToken;
			}

			public String getAuthName() {
				return authName;
			}

			public void setAuthName(String authName) {
				this.authName = authName;
			}

			public String getAuthPassword() {
				return authPassword;
			}

			public void setAuthPassword(String authPassword) {
				this.authPassword = authPassword;
			}
		}

		public static class GitHub {

			private String url;

			private String authType;

			private String authToken;

			private String authName;

			private String authPassword;

			public String getUrl() {
				return url;
			}

			public void setUrl(String url) {
				this.url = url;
			}

			public String getAuthType() {
				return authType;
			}

			public void setAuthType(String authType) {
				this.authType = authType;
			}

			public String getAuthToken() {
				return authToken;
			}

			public void setAuthToken(String authToken) {
				this.authToken = authToken;
			}

			public String getAuthName() {
				return authName;
			}

			public void setAuthName(String authName) {
				this.authName = authName;
			}

			public String getAuthPassword() {
				return authPassword;
			}

			public void setAuthPassword(String authPassword) {
				this.authPassword = authPassword;
			}
		}

		public static class Codeup {

			private String organizationId;

			private String accessKey;

			private String accessKeySecret;

			public String getOrganizationId() {
				return organizationId;
			}

			public void setOrganizationId(String organizationId) {
				this.organizationId = organizationId;
			}

			public String getAccessKey() {
				return accessKey;
			}

			public void setAccessKey(String accessKey) {
				this.accessKey = accessKey;
			}

			public String getAccessKeySecret() {
				return accessKeySecret;
			}

			public void setAccessKeySecret(String accessKeySecret) {
				this.accessKeySecret = accessKeySecret;
			}

		}
	}

	public static class Ldap extends BaseGlobalConfig {

		private static final long serialVersionUID = 1L;

		private Integer enable;

		private String url;

		private String adminDn;

		private String adminPassword;

		private String searchBaseDn;

		public Integer getEnable() {
			return enable;
		}

		public void setEnable(Integer enable) {
			this.enable = enable;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getAdminDn() {
			return adminDn;
		}

		public void setAdminDn(String adminDn) {
			this.adminDn = adminDn;
		}

		public String getAdminPassword() {
			return adminPassword;
		}

		public void setAdminPassword(String adminPassword) {
			this.adminPassword = adminPassword;
		}

		public String getSearchBaseDn() {
			return searchBaseDn;
		}

		public void setSearchBaseDn(String searchBaseDn) {
			this.searchBaseDn = searchBaseDn;
		}

	}

	public static class Maven extends BaseGlobalConfig {

		private static final long serialVersionUID = 1L;

		/**
		 * maven仓库地址
		 */
		private List<String> mavenRepoUrl;

		public List<String> getMavenRepoUrl() {
			return mavenRepoUrl;
		}

		public void setMavenRepoUrl(List<String> mavenRepoUrl) {
			this.mavenRepoUrl = mavenRepoUrl;
		}

	}

	/**
	 * 环境模板模型
	 */
	public static class EnvTemplate extends BaseGlobalConfig {

		private static final long serialVersionUID = 1L;

		/**
		 * 集群编号
		 */
		private String clusterId;

		/**
		 * 部署命名空间名称
		 */
		private String namespaceName;

		/**
		 * 模板名称，如：开发环境模板、测试环境模板、生产环境模板等
		 */
		private String templateName;

		/**
		 * 环境名称，如：开发、测试、预发、生产等
		 */
		private String envName;

		/**
		 * 环境标识，如：dev、qa、pl、ol等
		 */
		private String tag;

		/**
		 * 部署序号（值越小越往前）
		 */
		private Integer deploymentOrder;

		/**
		 * 最小副本数
		 */
		private Integer minReplicas;

		/**
		 * 最大副本数
		 */
		private Integer maxReplicas;

		/**
		 * 每副本CPU，单位m
		 */
		private Integer replicaCpu;

		/**
		 * 每副本内存，单位MB
		 */
		private Integer replicaMemory;

		/**
		 * 自动扩容，cpu使用率
		 */
		private Integer autoScalingCpu;

		/**
		 * 自动扩容，内存使用率
		 */
		private Integer autoScalingMemory;

		/**
		 * 是否需要部署审批，0：否，1：是
		 */
		private Integer requiredDeployApproval;

		/**
		 * 是否需要合并代码，0：否，1：是
		 */
		private Integer requiredMerge;

		/**
		 * 链路追踪状态
		 */
		private Integer traceStatus;

		/**
		 * 链路追踪模板编号
		 */
		private String traceTemplateId;

		/**
		 * 链路追踪模板名称
		 */
		private String traceTemplateName;

		/**
		 * 服务端口
		 */
		private Integer servicePort;

		/**
		 * 辅助端口，如：8081,8082
		 */
		private String minorPorts;

		/**
		 * jvm参数
		 */
		private String jvmArgs;

		/**
		 * 模板描述
		 */
		private String description;

		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}

		public String getClusterId() {
			return clusterId;
		}

		public void setClusterId(String clusterId) {
			this.clusterId = clusterId;
		}

		public String getTemplateName() {
			return templateName;
		}

		public void setTemplateName(String templateName) {
			this.templateName = templateName;
		}

		public String getEnvName() {
			return envName;
		}

		public void setEnvName(String envName) {
			this.envName = envName;
		}

		public void setDeploymentOrder(Integer deploymentOrder) {
			this.deploymentOrder = deploymentOrder;
		}

		public Integer getDeploymentOrder() {
			return deploymentOrder;
		}

		public Integer getMinReplicas() {
			return minReplicas;
		}

		public void setMinReplicas(Integer minReplicas) {
			this.minReplicas = minReplicas;
		}

		public Integer getMaxReplicas() {
			return maxReplicas;
		}

		public void setMaxReplicas(Integer maxReplicas) {
			this.maxReplicas = maxReplicas;
		}

		public String getMinorPorts() {
			return minorPorts;
		}

		public void setMinorPorts(String minorPorts) {
			this.minorPorts = minorPorts;
		}

		public Integer getAutoScalingCpu() {
			return autoScalingCpu;
		}

		public void setAutoScalingCpu(Integer autoScalingCpu) {
			this.autoScalingCpu = autoScalingCpu;
		}

		public Integer getAutoScalingMemory() {
			return autoScalingMemory;
		}

		public void setAutoScalingMemory(Integer autoScalingMemory) {
			this.autoScalingMemory = autoScalingMemory;
		}

		public Integer getReplicaCpu() {
			return replicaCpu;
		}

		public void setReplicaCpu(Integer replicaCpu) {
			this.replicaCpu = replicaCpu;
		}

		public Integer getReplicaMemory() {
			return replicaMemory;
		}

		public void setReplicaMemory(Integer replicaMemory) {
			this.replicaMemory = replicaMemory;
		}

		public Integer getRequiredDeployApproval() {
			return requiredDeployApproval;
		}

		public void setRequiredDeployApproval(Integer requiredDeployApproval) {
			this.requiredDeployApproval = requiredDeployApproval;
		}

		public Integer getRequiredMerge() {
			return requiredMerge;
		}

		public void setRequiredMerge(Integer requiredMerge) {
			this.requiredMerge = requiredMerge;
		}

		public Integer getTraceStatus() {
			return traceStatus;
		}

		public void setTraceStatus(Integer traceStatus) {
			this.traceStatus = traceStatus;
		}

		public String getTraceTemplateId() {
			return traceTemplateId;
		}

		public void setTraceTemplateId(String traceTemplateId) {
			this.traceTemplateId = traceTemplateId;
		}

		public String getTraceTemplateName() {
			return traceTemplateName;
		}

		public void setTraceTemplateName(String traceTemplateName) {
			this.traceTemplateName = traceTemplateName;
		}

		public String getNamespaceName() {
			return namespaceName;
		}

		public void setNamespaceName(String namespaceName) {
			this.namespaceName = namespaceName;
		}

		public Integer getServicePort() {
			return servicePort;
		}

		public void setServicePort(Integer servicePort) {
			this.servicePort = servicePort;
		}

		public String getJvmArgs() {
			return jvmArgs;
		}

		public void setJvmArgs(String jvmArgs) {
			this.jvmArgs = jvmArgs;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

	}

	public static class TraceTemplate extends BaseGlobalConfig {

		private static final long serialVersionUID = 1L;

		/**
		 * 模板名称
		 */
		private String name;

		/**
		 * 服务地址，如：127.0.0.1:1800
		 */
		private String serviceUrl;

		/**
		 * 技术类型
		 */
		private Integer techType;

		/**
		 * agent镜像来源，1：版本号，2：自定义
		 */
		private Integer agentImageSource;

		/**
		 * Agent版本
		 */
		private String agentVersion;

		/**
		 * Agent镜像
		 */
		private String agentImage;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getServiceUrl() {
			return serviceUrl;
		}

		public void setServiceUrl(String serviceUrl) {
			this.serviceUrl = serviceUrl;
		}

		public Integer getAgentImageSource() {
			return agentImageSource;
		}

		public void setAgentImageSource(Integer agentImageSource) {
			this.agentImageSource = agentImageSource;
		}

		public String getAgentImage() {
			return agentImage;
		}

		public void setAgentImage(String agentImage) {
			this.agentImage = agentImage;
		}

		public Integer getTechType() {
			return techType;
		}

		public void setTechType(Integer techType) {
			this.techType = techType;
		}

		public String getAgentVersion() {
			return agentVersion;
		}

		public void setAgentVersion(String agentVersion) {
			this.agentVersion = agentVersion;
		}

	}

	/**
	 * 自定义菜单模型
	 */
	public static class CustomizedMenu extends BaseGlobalConfig {

		private static final long serialVersionUID = 1L;

		/**
		 * 父级菜单名称
		 */
		private String parentName;

		/**
		 * 菜单名称
		 */
		private String name;

		/**
		 * 链接地址，如：http://127.0.0.1:80/menu
		 */
		private String url;

		public String getParentName() {
			return parentName;
		}

		public void setParentName(String parentName) {
			this.parentName = parentName;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

	}

	/**
	 * 更多
	 */
	public static class More extends BaseGlobalConfig {

		private static final long serialVersionUID = 1L;

		/**
		 * 事件通知地址
		 */
		private String eventNotifyUrl;

		public String getEventNotifyUrl() {
			return eventNotifyUrl;
		}

		public void setEventNotifyUrl(String eventNotifyUrl) {
			this.eventNotifyUrl = eventNotifyUrl;
		}

	}

	/**
	 * 企业微信配置
	 */
	public static class WeChat extends BaseGlobalConfig {

		private static final long serialVersionUID = 1L;

		private String corpId;

		private String agentId;

		private String secret;

		private Integer enable;

		public String getCorpId() {
			return corpId;
		}

		public void setCorpId(String corpId) {
			this.corpId = corpId;
		}

		public String getAgentId() {
			return agentId;
		}

		public void setAgentId(String agentId) {
			this.agentId = agentId;
		}

		public String getSecret() {
			return secret;
		}

		public void setSecret(String secret) {
			this.secret = secret;
		}

		public Integer getEnable() {
			return enable;
		}

		public void setEnable(Integer enable) {
			this.enable = enable;
		}

	}

	/**
	 * 钉钉
	 */
	public static class DingDing extends BaseGlobalConfig {

		private static final long serialVersionUID = 1L;

		private String appKey;

		private String appSecret;

		private Integer enable;

		public String getAppKey() {
			return appKey;
		}

		public void setAppKey(String appKey) {
			this.appKey = appKey;
		}

		public String getAppSecret() {
			return appSecret;
		}

		public void setAppSecret(String appSecret) {
			this.appSecret = appSecret;
		}

		public Integer getEnable() {
			return enable;
		}

		public void setEnable(Integer enable) {
			this.enable = enable;
		}

	}

	/**
	 * 飞书
	 */
	public static class FeiShu extends BaseGlobalConfig {

		private static final long serialVersionUID = 1L;

		private String appID;

		private String appSecret;

		private Integer enable;

		public String getAppID() {
			return appID;
		}

		public void setAppID(String appID) {
			this.appID = appID;
		}

		public String getAppSecret() {
			return appSecret;
		}

		public void setAppSecret(String appSecret) {
			this.appSecret = appSecret;
		}

		public Integer getEnable() {
			return enable;
		}

		public void setEnable(Integer enable) {
			this.enable = enable;
		}

	}

	/**
	 * CAS登录
	 */
	public static class CAS extends BaseGlobalConfig {

		private static final long serialVersionUID = 1L;

		private String serverUrlPrefix;

		private String serverLoginUrl;

		private String serverLogoutUrl;

		private String clientHostUrl;

		private Integer enable;

		public String getServerUrlPrefix() {
			return serverUrlPrefix;
		}

		public void setServerUrlPrefix(String serverUrlPrefix) {
			this.serverUrlPrefix = serverUrlPrefix;
		}

		public String getServerLoginUrl() {
			return serverLoginUrl;
		}

		public void setServerLoginUrl(String serverLoginUrl) {
			this.serverLoginUrl = serverLoginUrl;
		}

		public String getServerLogoutUrl() {
			return serverLogoutUrl;
		}

		public void setServerLogoutUrl(String serverLogoutUrl) {
			this.serverLogoutUrl = serverLogoutUrl;
		}

		public String getClientHostUrl() {
			return clientHostUrl;
		}

		public void setClientHostUrl(String clientHostUrl) {
			this.clientHostUrl = clientHostUrl;
		}

		public Integer getEnable() {
			return enable;
		}

		public void setEnable(Integer enable) {
			this.enable = enable;
		}

	}
}
