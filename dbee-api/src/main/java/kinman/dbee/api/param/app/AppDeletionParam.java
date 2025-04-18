package kinman.dbee.api.param.app;

import java.io.Serializable;

/**
 * 删除应用参数模型。
 */
public class AppDeletionParam implements Serializable {

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