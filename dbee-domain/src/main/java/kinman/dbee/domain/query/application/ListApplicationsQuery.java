package kinman.dbee.domain.query.application;

import kinman.dbee.domain.query.Query;

/**
 * 列出应用查询
 * <p>
 * 用于请求获取应用列表。
 * </p>
 */
public class ListApplicationsQuery extends Query {
    
    private final int page;
    private final int size;
    private final String ownerId;
    
    /**
     * 创建列出应用查询
     *
     * @param page 页码
     * @param size 每页大小
     */
    public ListApplicationsQuery(int page, int size) {
        this(page, size, null);
    }
    
    /**
     * 创建列出应用查询
     *
     * @param page 页码
     * @param size 每页大小
     * @param ownerId 所有者ID
     */
    public ListApplicationsQuery(int page, int size, String ownerId) {
        this.page = page;
        this.size = size;
        this.ownerId = ownerId;
    }
    
    /**
     * 获取页码
     *
     * @return 页码
     */
    public int getPage() {
        return page;
    }
    
    /**
     * 获取每页大小
     *
     * @return 每页大小
     */
    public int getSize() {
        return size;
    }
    
    /**
     * 获取所有者ID
     *
     * @return 所有者ID
     */
    public String getOwnerId() {
        return ownerId;
    }
} 