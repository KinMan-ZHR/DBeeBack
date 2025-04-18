package kinman.dbee.api.param.user;

import java.io.Serializable;

/**
 * 搜索用户的参数模型
 */
public class UserSearchParam implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 用户名
	 */
	private String loginName;

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

}