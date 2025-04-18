package kinman.dbee.api.enums;

public enum ReplicaStatusEnum {

	PENDING(1, "启动中"),
	RUNNING(2, "成功"),
	FAILED(3, "失败"),
	DESTROYING(4, "销毁中"),
	;

	private final Integer code;

	private final String value;

	private ReplicaStatusEnum(Integer code, String value) {
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
