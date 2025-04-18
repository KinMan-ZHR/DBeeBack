package kinman.dbee.domain.model.application;

import kinman.dbee.domain.exception.DomainException;
import kinman.dbee.domain.model.common.Entity;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 部署实体
 * <p>
 * 表示应用的一次部署操作，记录部署的详细信息。
 * </p>
 */
public class Deployment extends Entity<String> {
    
    private final String id;
    private final ApplicationId applicationId;
    private final String environmentId;
    private final Version version;
    private final DeploymentConfig config;
    private DeploymentStatus status;
    private final LocalDateTime startTime;
    private LocalDateTime endTime;
    private String logUrl;
    private String message;
    
    /**
     * 私有构造函数
     */
    private Deployment(String id, ApplicationId applicationId, String environmentId, Version version, DeploymentConfig config) {
        this.id = id;
        this.applicationId = applicationId;
        this.environmentId = environmentId;
        this.version = version;
        this.config = config;
        this.status = DeploymentStatus.PENDING;
        this.startTime = LocalDateTime.now();
    }
    
    /**
     * 创建新的部署
     *
     * @param applicationId 应用ID
     * @param environmentId 环境ID
     * @param version 版本
     * @param config 部署配置
     * @return 新创建的部署
     */
    public static Deployment create(ApplicationId applicationId, String environmentId, Version version, DeploymentConfig config) {
        String id = UUID.randomUUID().toString();
        return new Deployment(id, applicationId, environmentId, version, config);
    }
    
    /**
     * 开始执行部署
     */
    public void start() {
        if (status != DeploymentStatus.PENDING) {
            throw new DomainException("只有未开始的部署才能启动");
        }
        
        this.status = DeploymentStatus.IN_PROGRESS;
    }
    
    /**
     * 完成部署
     *
     * @param success 是否成功
     */
    public void complete(boolean success) {
        if (status == DeploymentStatus.COMPLETED || status == DeploymentStatus.FAILED) {
            throw new DomainException("部署已经完成，不能再次完成");
        }
        
        this.status = success ? DeploymentStatus.COMPLETED : DeploymentStatus.FAILED;
        this.endTime = LocalDateTime.now();
    }
    
    /**
     * 设置部署日志URL
     *
     * @param logUrl 日志URL
     */
    public void setLogUrl(String logUrl) {
        this.logUrl = logUrl;
    }
    
    /**
     * 设置部署消息
     *
     * @param message 部署消息
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    /**
     * 获取应用ID
     *
     * @return 应用ID
     */
    public ApplicationId getApplicationId() {
        return applicationId;
    }
    
    /**
     * 获取环境ID
     *
     * @return 环境ID
     */
    public String getEnvironmentId() {
        return environmentId;
    }
    
    /**
     * 获取版本
     *
     * @return 版本
     */
    public Version getVersion() {
        return version;
    }
    
    /**
     * 获取部署配置
     *
     * @return 部署配置
     */
    public DeploymentConfig getConfig() {
        return config;
    }
    
    /**
     * 获取部署状态
     *
     * @return 部署状态
     */
    public DeploymentStatus getStatus() {
        return status;
    }
    
    /**
     * 获取开始时间
     *
     * @return 开始时间
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    /**
     * 获取结束时间
     *
     * @return 结束时间
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    /**
     * 获取日志URL
     *
     * @return 日志URL
     */
    public String getLogUrl() {
        return logUrl;
    }
    
    /**
     * 获取部署消息
     *
     * @return 部署消息
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * 部署状态枚举
     */
    public enum DeploymentStatus {
        /**
         * 等待中
         */
        PENDING,
        
        /**
         * 进行中
         */
        IN_PROGRESS,
        
        /**
         * 已完成
         */
        COMPLETED,
        
        /**
         * 失败
         */
        FAILED
    }
} 