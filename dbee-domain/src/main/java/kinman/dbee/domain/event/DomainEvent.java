package kinman.dbee.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 领域事件基类
 * <p>
 * 代表领域中发生的事件，用于领域对象间的通信和领域状态变更的记录。
 * 所有领域事件都应该继承此类。
 * </p>
 */
public abstract class DomainEvent {
    
    private final String eventId;
    private final LocalDateTime occurredOn;
    
    /**
     * 创建一个新的领域事件
     */
    protected DomainEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
    }
    
    /**
     * 获取事件ID
     * 
     * @return 事件ID
     */
    public String getEventId() {
        return eventId;
    }
    
    /**
     * 获取事件发生时间
     * 
     * @return 事件发生时间
     */
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
} 