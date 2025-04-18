package kinman.dbee.api.param;

import java.io.Serializable;

/**
 * 分页参数基类
 * <p>
 * 所有需要分页功能的参数类的基类，提供了分页相关的基本属性和操作。
 * 具体业务参数类应该继承此类来获取分页功能。
 * </p>
 * 
 * @author kinman
 */
public abstract class PageParam implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 每页条数，默认为10条
	 */
	private Integer pageSize = 10;

	/**
	 * 页码，从1开始
	 */
	private Integer pageNum;

	/**
	 * 获取每页条数
	 * 
	 * @return 每页条数
	 */
	public Integer getPageSize() {
		return pageSize;
	}

	/**
	 * 设置每页条数
	 * 
	 * @param pageSize 每页条数
	 */
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * 获取页码
	 * 
	 * @return 页码
	 */
	public Integer getPageNum() {
		return pageNum;
	}

	/**
	 * 设置页码
	 * 
	 * @param pageNum 页码
	 */
	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

}
