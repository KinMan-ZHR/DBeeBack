package kinman.dbee.domain.command.application;

import kinman.dbee.domain.command.Command;

/**
 * 部署应用命令
 * <p>
 * 用于请求部署应用。
 * </p>
 */
public class DeployApplicationCommand extends Command {
    
    private final String applicationId;
    private final String environmentId;
    private final String versionNumber;
    
    /**
     * 创建部署应用命令
     *
     * @param applicationId 应用ID
     * @param environmentId 环境ID
     * @param versionNumber 版本号
     */
    public DeployApplicationCommand(String applicationId, String environmentId, String versionNumber) {
        this.applicationId = applicationId;
        this.environmentId = environmentId;
        this.versionNumber = versionNumber;
    }
    
    /**
     * 获取应用ID
     *
     * @return 应用ID
     */
    public String getApplicationId() {
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
     * 获取版本号
     *
     * @return 版本号
     */
    public String getVersionNumber() {
        return versionNumber;
    }
} 