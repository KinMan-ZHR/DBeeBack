package kinman.dbee.api.enums;

public enum PackageBuildTypeEnum {

	MAVEN(1, "Maven"),
	GRADLE(2, "Gradle"),
	;

	private final Integer code;

	private final String value;

	PackageBuildTypeEnum(Integer code, String value) {
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
