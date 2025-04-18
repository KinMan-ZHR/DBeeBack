package kinman.dbee.domain.repository;

import kinman.dbee.domain.model.application.Application;
import kinman.dbee.domain.model.application.ApplicationId;

import java.util.List;

/**
 * 应用仓储接口
 * <p>
 * 定义了针对应用聚合根的持久化操作。
 * </p>
 */
public interface ApplicationRepository extends Repository<Application, ApplicationId> {
    
    /**
     * 根据名称查找应用
     *
     * @param name 应用名称
     * @return 应用列表
     */
    List<Application> findByName(String name);
    
    /**
     * 根据所有者ID查找应用
     *
     * @param ownerId 所有者ID
     * @return 应用列表
     */
    List<Application> findByOwnerId(String ownerId);
    
    /**
     * 查找所有应用
     *
     * @param page 页码
     * @param size 每页大小
     * @return 应用列表
     */
    List<Application> findAll(int page, int size);
    
    /**
     * 获取应用总数
     *
     * @return 应用总数
     */
    long count();
} 