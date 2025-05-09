package kinman.dbee.api.event;

/**
 * 构建版本通知模型
 *
 */
public class BuildVersionMessage {

	/**
	 * 提交人
	 */
	private String submitter;

	/**
	 * 分支（Tag）
	 */
	private String branchName;

	/**
	 * 应用名称
	 */
	private String appName;

	/**
	 * 版本名称
	 */
	private String versionName;

	/**
	 * 状态
	 * <p>
	 * 构建事件，参见：org.dbee.api.enums.BuildStatusEnum
	 * <P>
	 * 部署事件，参见：org.dbee.api.enums.DeploymentStatusEnum
	 */
	private Integer status;

	public String getSubmitter() {
		return submitter;
	}

	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
