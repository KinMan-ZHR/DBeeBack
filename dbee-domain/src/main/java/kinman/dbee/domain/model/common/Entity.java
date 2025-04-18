package kinman.dbee.domain.model.common;

import java.util.Objects;

/**
 * 领域实体基类
 * <p>
 * 所有具有唯一标识的领域对象都应继承此类。
 * 实体通过ID而非属性进行相等性比较。
 * </p>
 *
 * @param <ID> 标识类型
 */
public abstract class Entity<ID> {
    
    /**
     * 获取实体标识
     *
     * @return 实体标识
     */
    public abstract ID getId();
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Entity<?> entity = (Entity<?>) o;
        return Objects.equals(getId(), entity.getId());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
} 