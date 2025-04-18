package kinman.dbee.domain.command;

/**
 * 命令处理器接口
 * <p>
 * 所有命令处理器都应该实现此接口。
 * 命令处理器负责处理特定类型的命令。
 * </p>
 *
 * @param <T> 命令类型
 */
public interface CommandHandler<T extends Command> {
    
    /**
     * 处理命令
     *
     * @param command 命令实例
     */
    void handle(T command);
} 