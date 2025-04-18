package kinman.dbee.api.enums;

public enum CodeTypeEnum {

	BRANCH(1, "分支"),
	TAG(2, "标签");

	private final Integer code;

	private final String value;

	CodeTypeEnum(Integer code, String value) {
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
