package kinman.dbee.rest.component;

import kinman.dbee.api.enums.MessageCodeEnum;
import kinman.dbee.api.response.RestResponse;
import kinman.dbee.infrastructure.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * 统一异常处理
 *
 */
@ControllerAdvice
public class ExceptionHandle {

	private static final Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);

	@ResponseBody
	@ExceptionHandler(value = ApplicationException.class)
	public RestResponse<String> handleApplicationException(ApplicationException e) {
		RestResponse<String> response = new RestResponse<>();
		response.setCode(e.getCode());
		response.setMessage(e.getMessage());
		return response;
	}
	
	@ResponseBody
	@ExceptionHandler(value = Throwable.class)
	public RestResponse<String> handleThrowable(Throwable e) {
		logger.error("Server error", e);
		RestResponse<String> response = new RestResponse<>();
		response.setCode(MessageCodeEnum.SERVER_ERROR.getCode());
		response.setMessage(MessageCodeEnum.SERVER_ERROR.getMessage());
		return response;
	}
}
