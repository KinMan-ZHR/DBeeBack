package kinman.dbee.rest.component;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import kinman.dbee.infrastructure.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 
 * 请求日志
 *
 */
@Aspect
@Component
public class WebLogAspect {

	private static final Logger logger = LoggerFactory.getLogger(WebLogAspect.class);

//	private static final String[] IGNORE_PARAMS = new String[] {
//			"serialVersionUID", "password", "code",
//			"authToken", "authName", "authPassword",
//			"CASE_INSENSITIVE_ORDER", "hash", "response"};

	private static final ThreadLocal<Long> startTime = new ThreadLocal<>();

	@Autowired
	private HttpServletRequest httpServletRequest;

	@Pointcut("execution(public * org.dbee.rest.resource.*.*(..))")
	public void include() {
	}
	
	@Before("include()")
	public void doBefore(JoinPoint joinPoint) {
		if(httpServletRequest.getRequestURL().toString().endsWith(Constants.COLLECT_METRICS_URI)) {
			return;
		}
		startTime.set(System.currentTimeMillis());
		logger.info("request url: {}, ip: {}", httpServletRequest.getRequestURL().toString(),
				httpServletRequest.getRemoteAddr());
	}

	@AfterReturning(returning = "ret", pointcut = "include()")
	public void doAfterReturning(Object ret) {
		if(httpServletRequest.getRequestURL().toString().endsWith(Constants.COLLECT_METRICS_URI)) {
			return;
		}
		logger.info("response in {} millisecond", (System.currentTimeMillis() - startTime.get()));
		startTime.remove();
	}

}