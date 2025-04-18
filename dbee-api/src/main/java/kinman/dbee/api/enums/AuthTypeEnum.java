package kinman.dbee.api.enums;

public enum AuthTypeEnum {

	TOKEN(1, "令牌认证"),
	ACCOUNT(2, "账号认证"),
	;

	private final Integer code;

	private final String value;

	AuthTypeEnum(Integer code, String value) {
		this.code = code;
		this.value = value;
	}

	public Integer getCode() {
		return code;
	}

	public String getValue() {
		return value;
	}
}
