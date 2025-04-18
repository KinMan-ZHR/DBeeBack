package kinman.dbee.api.param.global;

import java.io.Serializable;

/**
 * 全局配置分页参数模型
 */
public class GlobalConfigDeletionParam implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 配置编号
	 */
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}