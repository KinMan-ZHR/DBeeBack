package kinman.dbee.domain.model.user;

import kinman.dbee.domain.model.common.ValueObject;

import java.util.Objects;
import java.util.UUID;

/**
 * 用户ID值对象
 * <p>
 * 用户的唯一标识符，作为值对象实现。
 * </p>
 */
public class UserId extends ValueObject {
    
    private final String id;
    
    /**
     * 创建一个随机的用户ID
     */
    public UserId() {
        this.id = UUID.randomUUID().toString();
    }
    
    /**
     * 使用指定的ID创建用户ID
     *
     * @param id 用户ID字符串
     */
    public UserId(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        this.id = id;
    }
    
    /**
     * 获取ID字符串
     *
     * @return ID字符串
     */
    public String getValue() {
        return id;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserId userId = (UserId) o;
        return Objects.equals(id, userId.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return id;
    }
} 