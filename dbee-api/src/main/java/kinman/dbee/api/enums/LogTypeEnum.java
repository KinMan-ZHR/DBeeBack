package kinman.dbee.api.enums;

/**
 * 日志类型
 * 
 * @author Dahi
 */
public enum LogTypeEnum {

	BUILD_VERSION(1, "构建日志"),
	DEPLOY_ENV(2, "部署日志"),
	;

	private final Integer code;

	private final String value;

	LogTypeEnum(Integer code, String value) {
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
