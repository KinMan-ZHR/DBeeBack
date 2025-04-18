package kinman.dbee.api.enums;

public enum CodeRepoTypeEnum {

	GITLAB(1, "GitLab"),
	GITHUB(2, "GitHub"),
	GITEE(3, "Gitee"),
	CODEUP(4, "Codeup");

	private final Integer code;

	private final String value;

	CodeRepoTypeEnum(Integer code, String value) {
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
