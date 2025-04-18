package kinman.dbee.domain.event;

/**
 * 领域事件发布器接口
 * <p>
 * 用于发布领域事件的接口，具体实现由基础设施层提供。
 * </p>
 */
public interface DomainEventPublisher {
    
    /**
     * 发布领域事件
     * 
     * @param event 领域事件
     */
    void publish(DomainEvent event);
    
    /**
     * 发布多个领域事件
     * 
     * @param events 领域事件数组
     */
    void publishAll(DomainEvent... events);
} 