package kinman.dbee.domain.model.common;

import kinman.dbee.domain.event.DomainEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 聚合根基类
 * <p>
 * 所有的聚合根对象都应继承此类。
 * 聚合根是一个一致性边界，维护内部实体和值对象的不变性。
 * 聚合根可以发布领域事件。
 * </p>
 * 
 * @param <ID> 标识类型
 */
public abstract class AggregateRoot<ID> extends Entity<ID> {
    
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    
    /**
     * 注册领域事件
     * 
     * @param event 领域事件
     */
    protected void registerEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }
    
    /**
     * 获取未处理的领域事件
     * 
     * @return 领域事件列表
     */
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }
    
    /**
     * 清除所有领域事件
     */
    public void clearEvents() {
        this.domainEvents.clear();
    }
} 