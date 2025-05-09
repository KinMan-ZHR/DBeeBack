package kinman.dbee.api.enums;

public enum TechTypeEnum {

	SPRING_BOOT(1, "SpringBoot"),
	VUE(2, "Vue"),
	REACT(3, "React"),
	NODEJS(4, "Nodejs"),
	HTML(5, "Html"),
	GO(6, "Go"),
	PYTHON(7, "Python"),
	FLASK(8, "Flask"),
	DJANGO(9, "Django"),
	NUXT(10, "Nuxt"),
	NEXT(11, "Next"),
	DOTNET(12, ".Net");
	
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
