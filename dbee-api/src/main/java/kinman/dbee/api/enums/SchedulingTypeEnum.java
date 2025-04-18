package kinman.dbee.api.enums;

/**
 * 调度类型
 */
public enum SchedulingTypeEnum {

	NODE_AFFINITY(1, "节点容忍"),
	NODE_TOLERATION(2, "节点容忍"),
	REPLICA_AFFINITY(3, "副本亲和"),
	REPLICA_ANTI_AFFINITY(4, "副本反亲和"),
	;

	private final Integer code;

	private final String value;

	SchedulingTypeEnum(Integer code, String value) {
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
