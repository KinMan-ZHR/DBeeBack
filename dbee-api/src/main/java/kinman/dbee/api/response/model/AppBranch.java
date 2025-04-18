package kinman.dbee.api.response.model;

/**
 * 分支信息
 */
public class AppBranch extends BaseDto {

	private static final long serialVersionUID = 1L;

	/**
	 * 分支名
	 */
	private String branchName;

	/**
	 * 合并状态，0：未合并，1：已合并
	 */
	private Integer mergedStatus;

	/**
	 * 最后提交信息
	 */
	private String commitMessage;

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public Integer getMergedStatus() {
		return mergedStatus;
	}

	public void setMergedStatus(Integer mergedStatus) {
		this.mergedStatus = mergedStatus;
	}

	public String getCommitMessage() {
		return commitMessage;
	}

	public void setCommitMessage(String commitMessage) {
		this.commitMessage = commitMessage;
	}

}