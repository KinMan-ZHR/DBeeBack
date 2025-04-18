package kinman.dbee.domain.model.application;

import kinman.dbee.domain.event.application.ApplicationCreatedEvent;
import kinman.dbee.domain.event.application.ApplicationDeployingEvent;
import kinman.dbee.domain.event.application.ApplicationStatusChangedEvent;
import kinman.dbee.domain.exception.DomainException;
import kinman.dbee.domain.model.common.AggregateRoot;
import kinman.dbee.domain.model.user.UserId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 应用聚合根
 * <p>
 * 应用是整个系统的核心聚合根之一，代表一个可部署的应用程序。
 * 包含应用的基本信息、部署配置等。
 * </p>
 */
public class Application extends AggregateRoot<ApplicationId> {
    
    private ApplicationId id;
    private String name;
    private String description;
    private ApplicationStatus status;
    private ApplicationType type;
    private DeploymentConfig deploymentConfig;
    private UserId ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Version currentVersion;
    private final List<Deployment> deployments = new ArrayList<>();
    
    /**
     * 私有构造函数，禁止直接创建实例
     */
    private Application() {
    }
    
    /**
     * 创建新应用
     *
     * @param name 应用名称
     * @param description 应用描述
     * @param type 应用类型
     * @param ownerId 所有者ID
     * @return 新创建的应用实例
     */
    public static Application create(String name, String description, ApplicationType type, UserId ownerId) {
        if (name == null || name.trim().isEmpty()) {
            throw new DomainException("应用名称不能为空");
        }
        
        Application app = new Application();
        app.id = new ApplicationId();
        app.name = name;
        app.description = description;
        app.status = ApplicationStatus.CREATED;
        app.type = type;
        app.ownerId = ownerId;
        app.createdAt = LocalDateTime.now();
        app.updatedAt = app.createdAt;
        
        // 注册领域事件
        app.registerEvent(new ApplicationCreatedEvent(app.id.getValue(), name, type.name(), ownerId.getValue()));
        
        return app;
    }
    
    /**
     * 配置应用部署参数
     *
     * @param config 部署配置
     */
    public void configureDeployment(DeploymentConfig config) {
        if (status != ApplicationStatus.CREATED && status != ApplicationStatus.CONFIGURED) {
            throw new DomainException("应用当前状态不允许配置部署参数：" + status.getDescription());
        }
        
        this.deploymentConfig = config;
        this.status = ApplicationStatus.CONFIGURED;
        this.updatedAt = LocalDateTime.now();
        
        // 注册状态变更事件
        registerEvent(new ApplicationStatusChangedEvent(
                id.getValue(), 
                ApplicationStatus.CREATED.name(), 
                ApplicationStatus.CONFIGURED.name()));
    }
    
    /**
     * 准备部署应用
     *
     * @param version 部署版本
     */
    public void prepareForDeployment(Version version) {
        if (status != ApplicationStatus.CONFIGURED) {
            throw new DomainException("应用未配置，无法部署");
        }
        
        if (deploymentConfig == null) {
            throw new DomainException("缺少部署配置");
        }
        
        this.status = ApplicationStatus.READY_FOR_DEPLOYMENT;
        this.currentVersion = version;
        this.updatedAt = LocalDateTime.now();
        
        // 注册状态变更事件
        registerEvent(new ApplicationStatusChangedEvent(
                id.getValue(), 
                ApplicationStatus.CONFIGURED.name(), 
                ApplicationStatus.READY_FOR_DEPLOYMENT.name()));
    }
    
    /**
     * 开始部署应用
     *
     * @param targetEnvironmentId 目标环境ID
     * @return 新创建的部署对象
     */
    public Deployment startDeployment(String targetEnvironmentId) {
        if (status != ApplicationStatus.READY_FOR_DEPLOYMENT) {
            throw new DomainException("应用未准备好部署: " + status.getDescription());
        }
        
        if (currentVersion == null) {
            throw new DomainException("缺少部署版本");
        }
        
        // 创建新的部署记录
        Deployment deployment = Deployment.create(
                this.id,
                targetEnvironmentId,
                this.currentVersion,
                this.deploymentConfig
        );
        
        this.deployments.add(deployment);
        this.status = ApplicationStatus.DEPLOYING;
        this.updatedAt = LocalDateTime.now();
        
        // 注册部署事件
        registerEvent(new ApplicationDeployingEvent(
                id.getValue(),
                targetEnvironmentId,
                currentVersion.getVersionNumber(),
                deployment.getId()
        ));
        
        return deployment;
    }
    
    /**
     * 部署完成
     *
     * @param deploymentId 部署ID
     * @param success 是否成功
     */
    public void completeDeployment(String deploymentId, boolean success) {
        Deployment deployment = findDeploymentById(deploymentId);
        if (deployment == null) {
            throw new DomainException("找不到指定的部署记录: " + deploymentId);
        }
        
        deployment.complete(success);
        
        if (success) {
            this.status = ApplicationStatus.RUNNING;
        } else {
            this.status = ApplicationStatus.ERROR;
        }
        
        this.updatedAt = LocalDateTime.now();
        
        // 注册状态变更事件
        registerEvent(new ApplicationStatusChangedEvent(
                id.getValue(), 
                ApplicationStatus.DEPLOYING.name(), 
                this.status.name()));
    }
    
    /**
     * 停止应用
     */
    public void stop() {
        if (status != ApplicationStatus.RUNNING) {
            throw new DomainException("只有运行中的应用才能停止");
        }
        
        this.status = ApplicationStatus.STOPPED;
        this.updatedAt = LocalDateTime.now();
        
        // 注册状态变更事件
        registerEvent(new ApplicationStatusChangedEvent(
                id.getValue(), 
                ApplicationStatus.RUNNING.name(), 
                ApplicationStatus.STOPPED.name()));
    }
    
    /**
     * 启动应用
     */
    public void start() {
        if (status != ApplicationStatus.STOPPED) {
            throw new DomainException("只有已停止的应用才能启动");
        }
        
        this.status = ApplicationStatus.RUNNING;
        this.updatedAt = LocalDateTime.now();
        
        // 注册状态变更事件
        registerEvent(new ApplicationStatusChangedEvent(
                id.getValue(), 
                ApplicationStatus.STOPPED.name(), 
                ApplicationStatus.RUNNING.name()));
    }
    
    /**
     * 根据ID查找部署记录
     *
     * @param deploymentId 部署ID
     * @return 部署记录，如果未找到返回null
     */
    private Deployment findDeploymentById(String deploymentId) {
        return deployments.stream()
                .filter(d -> d.getId().equals(deploymentId))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public ApplicationId getId() {
        return id;
    }
    
    /**
     * 获取应用名称
     *
     * @return 应用名称
     */
    public String getName() {
        return name;
    }
    
    /**
     * 获取应用描述
     *
     * @return 应用描述
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 获取应用状态
     *
     * @return 应用状态
     */
    public ApplicationStatus getStatus() {
        return status;
    }
    
    /**
     * 获取应用类型
     *
     * @return 应用类型
     */
    public ApplicationType getType() {
        return type;
    }
    
    /**
     * 获取部署配置
     *
     * @return 部署配置
     */
    public DeploymentConfig getDeploymentConfig() {
        return deploymentConfig;
    }
    
    /**
     * 获取所有者ID
     *
     * @return 所有者ID
     */
    public UserId getOwnerId() {
        return ownerId;
    }
    
    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * 获取更新时间
     *
     * @return 更新时间
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * 获取当前版本
     *
     * @return 当前版本
     */
    public Version getCurrentVersion() {
        return currentVersion;
    }
    
    /**
     * 获取部署历史记录
     *
     * @return 部署历史记录列表
     */
    public List<Deployment> getDeployments() {
        return Collections.unmodifiableList(deployments);
    }
} 