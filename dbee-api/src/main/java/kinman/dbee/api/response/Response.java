package kinman.dbee.api.response;

import java.io.Serializable;

/**
 * 响应对象标记接口
 * <p>
 * 所有API响应对象都应实现此接口，表示可以作为API响应返回给客户端。
 * 此接口继承了Serializable接口，确保所有响应对象都可序列化。
 * </p>
 * 
 * @author kinman
 */
public interface Response extends Serializable {

}
