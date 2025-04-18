package kinman.dbee.domain.query;

import java.util.UUID;

/**
 * 查询基类
 * <p>
 * 所有查询都应该继承此类。
 * 查询表示对系统状态的读取请求。
 * </p>
 */
public abstract class Query {
    
    private final String queryId;
    
    /**
     * 创建一个新的查询
     */
    protected Query() {
        this.queryId = UUID.randomUUID().toString();
    }
    
    /**
     * 获取查询ID
     *
     * @return 查询ID
     */
    public String getQueryId() {
        return queryId;
    }
} 