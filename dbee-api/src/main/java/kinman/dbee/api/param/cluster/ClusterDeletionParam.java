package kinman.dbee.api.param.cluster;

import java.io.Serializable;

/**
 * 删除集群的参数模型。
 */
public class ClusterDeletionParam implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 集群编码
	 */
	private String clusterId;

	public String getClusterId() {
		return clusterId;
	}

	public void setClusterId(String clusterId) {
		this.clusterId = clusterId;
	}

}