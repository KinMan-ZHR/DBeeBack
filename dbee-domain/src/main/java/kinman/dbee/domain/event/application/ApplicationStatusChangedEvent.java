package kinman.dbee.domain.event.application;

import kinman.dbee.domain.event.DomainEvent;

/**
 * 应用状态变更事件
 * <p>
 * 当应用状态发生变化时发布的领域事件。
 * </p>
 */
public class ApplicationStatusChangedEvent extends DomainEvent {
    
    private final String applicationId;
    private final String oldStatus;
    private final String newStatus;
    
    /**
     * 创建应用状态变更事件
     *
     * @param applicationId 应用ID
     * @param oldStatus 旧状态
     * @param newStatus 新状态
     */
    public ApplicationStatusChangedEvent(String applicationId, String oldStatus, String newStatus) {
        this.applicationId = applicationId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
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
     * 获取旧状态
     *
     * @return 旧状态
     */
    public String getOldStatus() {
        return oldStatus;
    }
    
    /**
     * 获取新状态
     *
     * @return 新状态
     */
    public String getNewStatus() {
        return newStatus;
    }
} 