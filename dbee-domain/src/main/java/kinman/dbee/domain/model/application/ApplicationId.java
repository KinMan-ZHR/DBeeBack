package kinman.dbee.domain.model.application;

import kinman.dbee.domain.model.common.ValueObject;

import java.util.Objects;
import java.util.UUID;

/**
 * 应用ID值对象
 * <p>
 * 应用的唯一标识符，作为值对象实现。
 * </p>
 */
public class ApplicationId extends ValueObject {
    
    private final String id;
    
    /**
     * 创建一个随机的应用ID
     */
    public ApplicationId() {
        this.id = UUID.randomUUID().toString();
    }
    
    /**
     * 使用指定的ID创建应用ID
     *
     * @param id 应用ID字符串
     */
    public ApplicationId(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("应用ID不能为空");
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
        ApplicationId that = (ApplicationId) o;
        return Objects.equals(id, that.id);
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