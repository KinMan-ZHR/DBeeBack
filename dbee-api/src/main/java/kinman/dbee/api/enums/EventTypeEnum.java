package kinman.dbee.api.enums;

/**
 * 事件类型
 */
public enum EventTypeEnum {

	BUILD_VERSION("6001", "构建版本"),
	DEPLOY_ENV("6002", "部署环境"),
	;

	private final String code;

	private final String value;

	EventTypeEnum(String code, String value) {
		this.code = code;
		this.value = value;
	}

	public String getCode() {
		return code;
	}

	public String getValue() {
		return value;
	}
}
