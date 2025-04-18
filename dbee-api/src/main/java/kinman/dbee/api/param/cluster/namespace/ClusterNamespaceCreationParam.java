package kinman.dbee.api.param.cluster.namespace;

import java.io.Serializable;

/**
 * 添加服务器集群命名空间。
 */
public class ClusterNamespaceCreationParam implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 服务器集群编号
	 */
	private String clusterId;

	/**
	 * 命名空间名称，如：default、qa、pl、ol等
	 */
	private String namespaceName;

	public String getClusterId() {
		return clusterId;
	}

	public void setClusterId(String clusterId) {
		this.clusterId = clusterId;
	}

	public String getNamespaceName() {
		return namespaceName;
	}

	public void setNamespaceName(String namespaceName) {
		this.namespaceName = namespaceName;
	}

}