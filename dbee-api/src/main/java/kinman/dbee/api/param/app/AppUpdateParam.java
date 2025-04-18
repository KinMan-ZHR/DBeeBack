package kinman.dbee.api.param.app;

/**
 * 修改应用参数模型。
 */
public class AppUpdateParam extends AppCreationParam {

	private static final long serialVersionUID = 1L;

	/**
	 * 应用编号
	 */
	private String appId;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

}