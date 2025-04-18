package kinman.dbee.api.enums;

public enum PackageFileTypeEnum {

	JAR(1, "jar"),
	WAR(2, "war"),
	ZIP(3, "zip"),
	;

	private final Integer code;

	private final String value;

	PackageFileTypeEnum(Integer code, String value) {
		this.code = code;
		this.value = value;
	}
	
	public static PackageFileTypeEnum getByCode(Integer code) {
		for(PackageFileTypeEnum item : PackageFileTypeEnum.values()) {
			if(item.getCode().equals(code)) {
				return item;
			}
		}
		return null;
	}

	public Integer getCode() {
		return code;
	}

	public String getValue() {
		return value;
	}
}
