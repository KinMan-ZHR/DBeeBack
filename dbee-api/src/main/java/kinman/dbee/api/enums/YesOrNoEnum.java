package kinman.dbee.api.enums;

/**
 * 是否选项枚举类
 * <p>
 * 用于表示二元选择（是/否）的枚举，常用于各种需要布尔选择的场景。
 * </p>
 * 
 * @author kinman
 */
public enum YesOrNoEnum {

	NO(0, "否"), YES(1, "是");

	private final Integer code;

	private final String value;

	private YesOrNoEnum(Integer code, String value) {
		this.code = code;
		this.value = value;
	}

	/**
	 * 获取枚举对应的代码值
	 * 
	 * @return 代码值
	 */
	public Integer getCode() {
		return code;
	}

	/**
	 * 获取枚举对应的显示值
	 * 
	 * @return 显示值
	 */
	public String getValue() {
		return value;
	}
}
