package kinman.dbee.domain.model.common;

/**
 * 值对象基类
 * <p>
 * 值对象是通过其所有属性而非标识符定义的对象。
 * 值对象是不可变的，相等性比较基于其所有属性。
 * </p>
 */
public abstract class ValueObject {
    
    /**
     * 值对象相等性比较需要比较所有属性
     * 子类必须实现此方法以提供正确的相等性比较
     */
    @Override
    public abstract boolean equals(Object o);
    
    /**
     * 值对象的哈希码应基于所有属性计算
     * 子类必须实现此方法以提供正确的哈希码
     */
    @Override
    public abstract int hashCode();
} 