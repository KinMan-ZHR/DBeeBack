package kinman.dbee.api.param.global;

import kinman.dbee.api.param.PageParam;

/**
 * 全局配置分页参数模型
 */
public class GlobalConfigPageParam extends PageParam {

	private static final long serialVersionUID = 1L;

	/**
	 * 配置项类型，1：ldap，2：代码仓库，3：镜像仓库，4：maven，5：链路追踪模板，6：环境模板
	 */
	private Integer itemType;

	public Integer getItemType() {
		return itemType;
	}

	public void setItemType(Integer itemType) {
		this.itemType = itemType;
	}

}