package kinman.dbee.api.response;

import java.util.List;

/**
 * 分页数据封装类
 * <p>
 * 用于封装分页查询的结果数据，包含分页信息和数据项列表。
 * 支持泛型，可以封装任意类型的数据项列表。
 * </p>
 * 
 * @param <D> 数据项的类型
 * @author kinman
 */
public class PageData<D> implements Response {

	private static final long serialVersionUID = 1L;

	/**
	 * 当前页码，从1开始
	 */
	private Integer pageNum;

	/**
	 * 总页数
	 */
	private Integer pageCount;

	/**
	 * 每页数据条数
	 */
	private Integer pageSize;

	/**
	 * 总记录数
	 */
	private Integer itemCount;

	/**
	 * 当前页的数据项列表
	 */
	private List<D> items;

	/**
	 * 获取当前页码
	 * 
	 * @return 当前页码
	 */
	public Integer getPageNum() {
		return pageNum;
	}

	/**
	 * 设置当前页码
	 * 
	 * @param pageNum 当前页码
	 */
	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	/**
	 * 获取总页数
	 * 
	 * @return 总页数
	 */
	public Integer getPageCount() {
		return pageCount;
	}

	/**
	 * 设置总页数
	 * 
	 * @param pageCount 总页数
	 */
	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}

	/**
	 * 获取每页数据条数
	 * 
	 * @return 每页数据条数
	 */
	public Integer getPageSize() {
		return pageSize;
	}

	/**
	 * 设置每页数据条数
	 * 
	 * @param pageSize 每页数据条数
	 */
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * 获取总记录数
	 * 
	 * @return 总记录数
	 */
	public Integer getItemCount() {
		return itemCount;
	}

	/**
	 * 设置总记录数
	 * 
	 * @param itemCount 总记录数
	 */
	public void setItemCount(Integer itemCount) {
		this.itemCount = itemCount;
	}

	/**
	 * 获取当前页的数据项列表
	 * 
	 * @return 数据项列表
	 */
	public List<D> getItems() {
		return items;
	}

	/**
	 * 设置当前页的数据项列表
	 * 
	 * @param items 数据项列表
	 */
	public void setItems(List<D> items) {
		this.items = items;
	}

}
