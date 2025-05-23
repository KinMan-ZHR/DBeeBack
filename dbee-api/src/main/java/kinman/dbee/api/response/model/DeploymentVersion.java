package kinman.dbee.api.response.model;

/**
 * 部署版本
 */
public class DeploymentVersion extends BaseDto {

	private static final long serialVersionUID = 1L;

	/**
	 * 分支名称
	 */
	private String branchName;

	/**
	 * 镜像名称（包含tag）
	 */
	private String versionName;

	/**
	 * 环境编号
	 */
	private String envId;

	/**
	 * 环境名称
	 */
	private String envName;

	/**
	 * 状态，0：构建中，1：构建成功，2：构建失败
	 */
	private Integer status;

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getEnvId() {
		return envId;
	}

	public void setEnvId(String envId) {
		this.envId = envId;
	}

	public String getEnvName() {
		return envName;
	}

	public void setEnvName(String envName) {
		this.envName = envName;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}