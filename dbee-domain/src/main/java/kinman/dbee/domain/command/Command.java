package kinman.dbee.domain.command;

import java.util.UUID;

/**
 * 命令基类
 * <p>
 * 所有命令都应该继承此类。
 * 命令表示对系统状态的修改请求。
 * </p>
 */
public abstract class Command {
    
    private final String commandId;
    
    /**
     * 创建一个新的命令
     */
    protected Command() {
        this.commandId = UUID.randomUUID().toString();
    }
    
    /**
     * 获取命令ID
     *
     * @return 命令ID
     */
    public String getCommandId() {
        return commandId;
    }
} 