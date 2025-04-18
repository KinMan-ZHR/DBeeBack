package kinman.dbee.domain.exception;

/**
 * 领域异常基类
 * <p>
 * 所有领域层的异常都应该继承此类。
 * 代表领域规则验证失败或领域逻辑执行错误。
 * </p>
 */
public class DomainException extends RuntimeException {
    
    /**
     * 创建一个领域异常
     *
     * @param message 异常消息
     */
    public DomainException(String message) {
        super(message);
    }
    
    /**
     * 创建一个领域异常
     *
     * @param message 异常消息
     * @param cause 异常原因
     */
    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
} 