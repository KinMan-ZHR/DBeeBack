package kinman.dbee.api.response;

import java.io.Serializable;

import kinman.dbee.api.enums.MessageCodeEnum;

/**
 * RESTful API通用响应包装类
 * <p>
 * 用于统一封装API响应数据的格式，包括状态码、消息和业务数据。
 * 支持泛型，可以包装任意类型的业务数据。
 * 默认使用成功状态码和消息。
 * </p>
 * 
 * @param <D> 业务数据的类型
 * @author kinman
 */
public class RestResponse<D> implements Response, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 响应码，默认为成功状态码
	 */
	private String code = MessageCodeEnum.SUCCESS.getCode();

	/**
	 * 响应信息，默认为成功状态消息
	 */
	private String message = MessageCodeEnum.SUCCESS.getMessage();

	/**
	 * 业务数据
	 */
	private D data;

	/**
	 * 默认构造函数，创建一个成功状态的响应
	 */
	public RestResponse() {

	}

	/**
	 * 创建一个包含业务数据的成功响应
	 * 
	 * @param data 业务数据
	 */
	public RestResponse(D data) {
		this.data = data;
	}
	
	/**
	 * 创建一个指定状态码和消息的响应
	 * 
	 * @param messageCode 消息码枚举
	 */
	public RestResponse(MessageCodeEnum messageCode) {
		this.code = messageCode.getCode();
		this.message = messageCode.getMessage();
	}
	
	/**
	 * 创建一个自定义状态码和消息的响应
	 * 
	 * @param code 状态码
	 * @param message 消息内容
	 */
	public RestResponse(String code, String message) {
		this.code = code;
		this.message = message;
	}

	/**
	 * 获取响应码
	 * 
	 * @return 响应码
	 */
	public String getCode() {
		return code;
	}

	/**
	 * 设置响应码
	 * 
	 * @param code 响应码
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * 获取响应消息
	 * 
	 * @return 响应消息
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * 设置响应消息
	 * 
	 * @param message 响应消息
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * 获取业务数据
	 * 
	 * @return 业务数据
	 */
	public D getData() {
		return data;
	}

	/**
	 * 设置业务数据
	 * 
	 * @param data 业务数据
	 */
	public void setData(D data) {
		this.data = data;
	}
}
