package kinman.dbee.api.enums;

/**
 *  用户来源
 * @author Dahai
 *  2021-12-4 20:20:25
 */
public enum RegisteredSourceEnum {

	DBEE(1, "Dbee"),
	LDAP(2, "LDAP"),
	WECHAT(3, "WeChat"),
	DINGDING(4, "DingDing"),
	CAS(5, "CAS"),
	FEISHU(6, "FeiShu");

	private final Integer code;

	private final String value;

	private RegisteredSourceEnum(Integer code, String value) {
		this.code = code;
		this.value = value;
	}

	public static RegisteredSourceEnum getByCode(Integer code) {
		if(code == null) {
			return null;
		}
		for(RegisteredSourceEnum item : RegisteredSourceEnum.values()) {
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
