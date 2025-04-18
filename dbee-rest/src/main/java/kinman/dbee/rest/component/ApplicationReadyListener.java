package kinman.dbee.rest.component;

import kinman.dbee.application.service.AutoDeploymentApplicationService;
import kinman.dbee.application.service.InitializingService;
import kinman.dbee.infrastructure.utils.ThreadPoolUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent>{

	@Autowired
	private InitializingService initializingService;
	
	@Autowired
	private AutoDeploymentApplicationService autoDeploymentApplicationService;
	
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		ThreadPoolUtils.initThreadPool();
		initializingService.asynInitConfig();
		autoDeploymentApplicationService.startAllJob();
	}
}
