package kinman.dbee.agent;

import java.io.Serializable;

/**
 * 指标模型
 * 
 * @author kinman 2025-03-05
 */
public class Metrics implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 副本名称
	 */
	private String replicaName;

	/**
	 * 指标类型，见：MetricsTypeEnum
	 */
	private Integer metricsType;

	/**
	 * 指标值
	 */
	private Long metricsValue;

	public String getReplicaName() {
		return replicaName;
	}

	public void setReplicaName(String replicaName) {
		this.replicaName = replicaName;
	}

	public Integer getMetricsType() {
		return metricsType;
	}

	public void setMetricsType(Integer metricsType) {
		this.metricsType = metricsType;
	}

	public Long getMetricsValue() {
		return metricsValue;
	}

	public void setMetricsValue(Long metricsValue) {
		this.metricsValue = metricsValue;
	}

	@Override
	public String toString() {
		return "{\"replicaName\":\"" + replicaName + "\", \"metricsType\":" + metricsType
				+ ", \"metricsValue\":" + metricsValue + "}";
	}
}