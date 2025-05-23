package kinman.dbee.infrastructure.strategy.login.dto;

import java.util.Date;

import kinman.dbee.api.response.model.SysUser;

public class LoginUser extends SysUser {

	private static final long serialVersionUID = 1L;

	/**
	 * 最后一次登录时间
	 */
	private Date lastLoginTime;

	/**
	 * 最后一次登录token
	 */
	private String lastLoginToken;

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getLastLoginToken() {
		return lastLoginToken;
	}

	public void setLastLoginToken(String lastLoginToken) {
		this.lastLoginToken = lastLoginToken;
	}
}
