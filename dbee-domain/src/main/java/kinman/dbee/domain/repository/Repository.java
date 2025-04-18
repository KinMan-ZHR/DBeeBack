package kinman.dbee.domain.repository;

import kinman.dbee.domain.model.common.AggregateRoot;

import java.util.Optional;

/**
 * 通用仓储接口
 * <p>
 * 定义了针对聚合根的基本操作。
 * 具体实现由基础设施层提供。
 * </p>
 *
 * @param <T> 聚合根类型
 * @param <ID> 聚合根标识类型
 */
public interface Repository<T extends AggregateRoot<ID>, ID> {
    
    /**
     * 保存聚合根
     *
     * @param aggregateRoot 聚合根实例
     */
    void save(T aggregateRoot);
    
    /**
     * 根据ID查找聚合根
     *
     * @param id 聚合根ID
     * @return 聚合根的Optional包装
     */
    Optional<T> findById(ID id);
    
    /**
     * 根据ID删除聚合根
     *
     * @param id 聚合根ID
     */
    void deleteById(ID id);
    
    /**
     * 检查指定ID的聚合根是否存在
     *
     * @param id 聚合根ID
     * @return 如果存在返回true，否则返回false
     */
    boolean existsById(ID id);
} 