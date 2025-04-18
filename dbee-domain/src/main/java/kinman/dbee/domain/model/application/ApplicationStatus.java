package kinman.dbee.domain.model.application;

/**
 * 应用状态枚举
 * <p>
 * 定义应用在生命周期中的各种状态。
 * </p>
 */
public enum ApplicationStatus {
    
    /**
     * 初始创建状态
     */
    CREATED("已创建"),
    
    /**
     * 已配置状态
     */
    CONFIGURED("已配置"),
    
    /**
     * 准备部署状态
     */
    READY_FOR_DEPLOYMENT("准备部署"),
    
    /**
     * 部署中状态
     */
    DEPLOYING("部署中"),
    
    /**
     * 运行中状态
     */
    RUNNING("运行中"),
    
    /**
     * 停止状态
     */
    STOPPED("已停止"),
    
    /**
     * 发生错误状态
     */
    ERROR("错误");
    
    private final String description;
    
    ApplicationStatus(String description) {
        this.description = description;
    }
    
    /**
     * 获取状态描述
     *
     * @return 状态描述
     */
    public String getDescription() {
        return description;
    }
} 