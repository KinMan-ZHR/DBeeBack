package kinman.dbee.api.param.app.branch.deploy;

/**
 * 终止部署线程参数模型
 */
public class AbortDeploymentThreadParam extends AbortDeploymentParam {
	private static final long serialVersionUID = 1L;

	/**
	 * 线程名称
	 */
	private String threadName;

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

}