package kinman.dbee.rest.resource.system;

import kinman.dbee.api.response.RestResponse;
import kinman.dbee.infrastructure.annotation.AccessNotLogin;
import kinman.dbee.infrastructure.component.SpringBeanContext;
import kinman.dbee.infrastructure.utils.ThreadPoolUtils;
import kinman.dbee.rest.resource.AbstractRest;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system")
public class SystemRest extends AbstractRest {
	
	@AccessNotLogin
	@GetMapping("/ping")
	public RestResponse<String> ping() {
		return this.success();
	}
	
	@AccessNotLogin
	@GetMapping("/shutdown")
	public RestResponse<String> shutdown() {
		ThreadPoolUtils.async(()->{
			int exitCode = SpringApplication.exit((ConfigurableApplicationContext)
					SpringBeanContext.getContext(), () -> 0);
		    System.exit(exitCode);
		});
		return this.success();
	}
}
