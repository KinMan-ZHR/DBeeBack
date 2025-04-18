package kinman.dbee.api.param.cluster.namespace;

import kinman.dbee.api.param.PageParam;

/**
 * 分页查询服务器集群命名空间。
 */
public class ClusterNamespacePageParam extends PageParam {

	private static final long serialVersionUID = 1L;

	/**
	 * 集群编号
	 */
	private String clusterId;

	/**
	 * 名称，如：default、qa、pl、ol等
	 */
	private String namespaceName;

	public String getNamespaceName() {
		return namespaceName;
	}

	public void setNamespaceName(String namespaceName) {
		this.namespaceName = namespaceName;
	}

	public String getClusterId() {
		return clusterId;
	}

	public void setClusterId(String clusterId) {
		this.clusterId = clusterId;
	}

}