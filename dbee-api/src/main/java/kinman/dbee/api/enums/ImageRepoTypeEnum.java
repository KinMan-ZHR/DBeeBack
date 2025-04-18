package kinman.dbee.api.enums;

public enum ImageRepoTypeEnum {

	HARBOR(1, "Harbor"),
	DOCKERHUB(2, "DockerHub"),
	OTHER(3, "Other"),
	;

	private final Integer code;

	private final String value;

	ImageRepoTypeEnum(Integer code, String value) {
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
