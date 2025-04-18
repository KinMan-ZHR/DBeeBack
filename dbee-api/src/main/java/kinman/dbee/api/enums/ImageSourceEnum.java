package kinman.dbee.api.enums;

public enum ImageSourceEnum {

	VERSION(1, "版本号"),
	CUSTOM(2, "自定义");

	private final Integer code;

	private final String value;

	ImageSourceEnum(Integer code, String value) {
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
