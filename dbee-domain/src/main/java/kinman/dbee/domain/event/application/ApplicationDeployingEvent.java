package kinman.dbee.domain.event.application;

import kinman.dbee.domain.event.DomainEvent;

/**
 * 应用部署事件
 * <p>
 * 当应用开始部署时发布的领域事件。
 * </p>
 */
public class ApplicationDeployingEvent extends DomainEvent {
    
    private final String applicationId;
    private final String environmentId;
    private final String version;
    private final String deploymentId;
    
    /**
     * 创建应用部署事件
     *
     * @param applicationId 应用ID
     * @param environmentId 环境ID
     * @param version 版本号
     * @param deploymentId 部署ID
     */
    public ApplicationDeployingEvent(String applicationId, String environmentId, String version, String deploymentId) {
        this.applicationId = applicationId;
        this.environmentId = environmentId;
        this.version = version;
        this.deploymentId = deploymentId;
    }
    
    /**
     * 获取应用ID
     *
     * @return 应用ID
     */
    public String getApplicationId() {
        return applicationId;
    }
    
    /**
     * 获取环境ID
     *
     * @return 环境ID
     */
    public String getEnvironmentId() {
        return environmentId;
    }
    
    /**
     * 获取版本号
     *
     * @return 版本号
     */
    public String getVersion() {
        return version;
    }
    
    /**
     * 获取部署ID
     *
     * @return 部署ID
     */
    public String getDeploymentId() {
        return deploymentId;
    }
} 