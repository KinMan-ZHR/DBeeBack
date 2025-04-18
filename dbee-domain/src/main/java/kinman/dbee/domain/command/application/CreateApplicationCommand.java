package kinman.dbee.domain.command.application;

import kinman.dbee.domain.command.Command;
import kinman.dbee.domain.model.application.ApplicationType;

/**
 * 创建应用命令
 * <p>
 * 用于请求创建新应用。
 * </p>
 */
public class CreateApplicationCommand extends Command {
    
    private final String name;
    private final String description;
    private final ApplicationType type;
    private final String ownerId;
    
    /**
     * 创建创建应用命令
     *
     * @param name 应用名称
     * @param description 应用描述
     * @param type 应用类型
     * @param ownerId 所有者ID
     */
    public CreateApplicationCommand(String name, String description, ApplicationType type, String ownerId) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.ownerId = ownerId;
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
     * 获取应用类型
     *
     * @return 应用类型
     */
    public ApplicationType getType() {
        return type;
    }
    
    /**
     * 获取所有者ID
     *
     * @return 所有者ID
     */
    public String getOwnerId() {
        return ownerId;
    }
} 