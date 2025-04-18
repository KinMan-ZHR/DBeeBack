package kinman.dbee.api.enums;

public enum EnvTypeEnum {

	DEV(1, "开发环境"),
	QA(2, "测试环境"),
	PL(3, "预发环境"),
	OL(4, "生产环境"),
	;

	private final Integer code;

	private final String value;

	EnvTypeEnum(Integer code, String value) {
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
