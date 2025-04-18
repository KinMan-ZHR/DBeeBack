package kinman.dbee.domain.event.application;

import kinman.dbee.domain.event.DomainEvent;

/**
 * 应用创建事件
 * <p>
 * 当新应用被创建时发布的领域事件。
 * </p>
 */
public class ApplicationCreatedEvent extends DomainEvent {
    
    private final String applicationId;
    private final String name;
    private final String applicationType;
    private final String ownerId;
    
    /**
     * 创建应用创建事件
     *
     * @param applicationId 应用ID
     * @param name 应用名称
     * @param applicationType 应用类型
     * @param ownerId 所有者ID
     */
    public ApplicationCreatedEvent(String applicationId, String name, String applicationType, String ownerId) {
        this.applicationId = applicationId;
        this.name = name;
        this.applicationType = applicationType;
        this.ownerId = ownerId;
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
     * 获取应用名称
     *
     * @return 应用名称
     */
    public String getName() {
        return name;
    }
    
    /**
     * 获取应用类型
     *
     * @return 应用类型
     */
    public String getApplicationType() {
        return applicationType;
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