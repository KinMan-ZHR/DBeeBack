package kinman.dbee.api.param.user;

import java.io.Serializable;

/**
 * 查询用户的参数模型
 */
public class UserQueryParam implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 登录名
	 */
	private String loginName;

	/**
	 * 用户类型，见：org.dbee.api.enums.UserSourceTypeEnum
	 */
	private Integer userSourceType;

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public Integer getUserSourceType() {
		return userSourceType;
	}

	public void setUserSourceType(Integer userSourceType) {
		this.userSourceType = userSourceType;
	}

}