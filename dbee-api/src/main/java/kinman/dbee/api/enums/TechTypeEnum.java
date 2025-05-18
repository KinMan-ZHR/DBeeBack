package kinman.dbee.api.enums;

public enum TechTypeEnum {

	SPRING_BOOT(1, "SpringBoot"),
	VUE(2, "Vue"),
	REACT(3, "React"),
	NODEJS(4, "Nodejs"),
	HTML(5, "Html");
	
	private final Integer code;

	private final String value;

	private TechTypeEnum(Integer code, String value) {
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
