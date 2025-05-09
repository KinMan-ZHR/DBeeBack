package kinman.dbee.api.param.app.branch;

import kinman.dbee.api.param.PageParam;

/**
 * 分页查询应用分支
 */
public class AppBranchPageParam extends PageParam {

	private static final long serialVersionUID = 1L;

	/**
	 * 应用编号
	 */
	private String appId;

	/**
	 * 分支名称
	 */
	private String branchName;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

}