package kinman.dbee.api.enums;

/**
 * 构建版本状态
 * 
 * @author Dahai
 * @date 2022-8-13 17:12:50
 */
public enum BuildStatusEnum {

	BUILDING(0, "构建中"),
	BUILD_SUCCESS(1, "构建成功"),
	BUILD_FAILURE(2, "构建失败");

	private final Integer code;

	private final String value;

	BuildStatusEnum(Integer code, String value) {
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
