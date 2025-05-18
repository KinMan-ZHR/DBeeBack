package kinman.dbee.infrastructure.component;

import java.util.Objects;

import kinman.dbee.infrastructure.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ComponentConstants {

	@Value("${version}")
	private String version;

	@Value("${server.port}")
	private String serverPort;

	private String dataPath;

	private String logPath;
	
	@Value("${thread_pool.build.core:#{null}}")
	private Integer threadPoolBuildCore;
	
	@Value("${thread_pool.build.max:#{null}}")
	private Integer threadPoolBuildMax;

	@Value("${thread_pool.deployment.core:#{null}}")
	private Integer threadPoolDeploymentCore;

	@Value("${thread_pool.deployment.max:#{null}}")
	private Integer threadPoolDeploymentMax;
	
	@Autowired
	private MysqlConfig mysqlConfig;
	
	@Value("${kubernetes-client:fabric8}")
	private String kubernetesClient;

	@Value("${data.path:#{null}}")
	private void setDataPath(String dataPath) {
		if (Objects.isNull(dataPath)) {
			this.dataPath = Constants.DEFAULT_DATA_PATH;
		} else if (dataPath.endsWith("/")) {
			this.dataPath = dataPath + "dbee/data/";
		} else {
			this.dataPath = dataPath + "/dbee/data/";
		}
	}

	@Value("${log.path:#{null}}")
	private void setLogPath(String logPath) {
		if (Objects.isNull(logPath)) {
			this.logPath = Constants.DEFAULT_LOG_PATH;
		} else if (logPath.endsWith("/")) {
			this.logPath = logPath + "dbee/";
		} else {
			this.logPath = logPath + "/dbee/";
		}
	}


	public String getVersion() {
		return version;
	}

	public String getServerPort() {
		return serverPort;
	}

	public String getDataPath() {
		return dataPath;
	}

	public String getLogPath() {
		return logPath;
	}

	public MysqlConfig getMysql() {
		return mysqlConfig;
	}

	public Integer getThreadPoolBuildCore() {
		return threadPoolBuildCore;
	}

	public Integer getThreadPoolBuildMax() {
		return threadPoolBuildMax;
	}

	public Integer getThreadPoolDeploymentCore() {
		return threadPoolDeploymentCore;
	}

	public Integer getThreadPoolDeploymentMax() {
		return threadPoolDeploymentMax;
	}

	public String getKubernetesClient() {
		return kubernetesClient;
	}
}
