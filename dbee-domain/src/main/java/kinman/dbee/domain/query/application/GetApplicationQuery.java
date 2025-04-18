package kinman.dbee.domain.query.application;

import kinman.dbee.domain.query.Query;

/**
 * 获取应用查询
 * <p>
 * 用于请求获取应用详情。
 * </p>
 */
public class GetApplicationQuery extends Query {
    
    private final String applicationId;
    
    /**
     * 创建获取应用查询
     *
     * @param applicationId 应用ID
     */
    public GetApplicationQuery(String applicationId) {
        this.applicationId = applicationId;
    }
    
    /**
     * 获取应用ID
     *
     * @return 应用ID
     */
    public String getApplicationId() {
        return applicationId;
    }
} 