package kinman.dbee.api.response.model;

import java.io.Serializable;

/**
 * 环境健康检查配置模型
 */
public class EnvHealth extends BaseDto {

	private static final long serialVersionUID = 1L;

	private Item startup;

	private Item readiness;

	private Item liveness;

	public Item getStartup() {
		return startup;
	}

	public void setStartup(Item startup) {
		this.startup = startup;
	}

	public Item getReadiness() {
		return readiness;
	}

	public void setReadiness(Item readiness) {
		this.readiness = readiness;
	}

	public Item getLiveness() {
		return liveness;
	}

	public void setLiveness(Item liveness) {
		this.liveness = liveness;
	}

	public static enum HealthTypeEnum {

		STARTUP(1, "启动检查"), READINESS(2, "就绪检查"), LIVENESS(3, "存活检查"),;

		private Integer code;

		private String value;

		private HealthTypeEnum(Integer code, String value) {
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

	public static class Item implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * 编号
		 */
		private String id;

		/**
		 * 应用编号
		 */
		private String appId;

		/**
		 * 环境编号
		 */
		private String envId;

		/**
		 * 健康类型，1：启动，2：就绪，3：存活
		 */
		private int healthType;

		/**
		 * 动作类型
		 */
		private Integer actionType;

		/**
		 * 动作内容
		 */
		private String action;

		/**
		 * 初始延迟检查时间，单位：秒
		 */
		private Integer initialDelay;

		/**
		 * 周期检查时间，单位：秒
		 */
		private Integer period;

		/**
		 * 检查超时时间，单位：秒
		 */
		private Integer timeout;

		/**
		 * 成功次数阈值
		 */
		private Integer successThreshold;

		/**
		 * 失败次数阈值
		 */
		private Integer failureThreshold;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getAppId() {
			return appId;
		}

		public void setAppId(String appId) {
			this.appId = appId;
		}

		public String getEnvId() {
			return envId;
		}

		public void setEnvId(String envId) {
			this.envId = envId;
		}

		public int getHealthType() {
			return healthType;
		}

		public void setHealthType(int healthType) {
			this.healthType = healthType;
		}

		public Integer getActionType() {
			return actionType;
		}

		public void setActionType(Integer actionType) {
			this.actionType = actionType;
		}

		public String getAction() {
			return action;
		}

		public void setAction(String action) {
			this.action = action;
		}

		public Integer getInitialDelay() {
			return initialDelay;
		}

		public void setInitialDelay(Integer initialDelay) {
			this.initialDelay = initialDelay;
		}

		public Integer getPeriod() {
			return period;
		}

		public void setPeriod(Integer period) {
			this.period = period;
		}

		public Integer getTimeout() {
			return timeout;
		}

		public void setTimeout(Integer timeout) {
			this.timeout = timeout;
		}

		public Integer getSuccessThreshold() {
			return successThreshold;
		}

		public void setSuccessThreshold(Integer successThreshold) {
			this.successThreshold = successThreshold;
		}

		public Integer getFailureThreshold() {
			return failureThreshold;
		}

		public void setFailureThreshold(Integer failureThreshold) {
			this.failureThreshold = failureThreshold;
		}
	}
}