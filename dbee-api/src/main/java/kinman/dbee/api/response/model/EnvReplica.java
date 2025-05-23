package kinman.dbee.api.response.model;

import java.io.Serializable;

public class EnvReplica implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Jvm指标开启状态，0：未开启，1：已开启
	 */
	private Integer jvmMetricsStatus;

	private String name;

	private String ip;

	private String versionName;

	private String branchName;

	private String envName;

	private Integer status;

	private String namespace;

	private String nodeName;

	private String clusterName;

	private String startTime;

	public Integer getJvmMetricsStatus() {
		return jvmMetricsStatus;
	}

	public void setJvmMetricsStatus(Integer jvmMetricsStatus) {
		this.jvmMetricsStatus = jvmMetricsStatus;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEnvName() {
		return envName;
	}

	public void setEnvName(String envName) {
		this.envName = envName;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

}
