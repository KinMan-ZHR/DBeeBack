package kinman.dbee.api.response.model;

import kinman.dbee.api.response.model.App.AppExtend;

public class AppExtendGo extends AppExtend {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Go版本
	 */
	private String goVersion;

	public String getGoVersion() {
		return goVersion;
	}

	public void setGoVersion(String goVersion) {
		this.goVersion = goVersion;
	}

}