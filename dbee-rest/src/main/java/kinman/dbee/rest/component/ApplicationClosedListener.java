package kinman.dbee.rest.component;

import kinman.dbee.application.service.ClusterApplicationService;
import kinman.dbee.rest.websocket.WebSocketCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component
public class ApplicationClosedListener implements ApplicationListener<ContextClosedEvent>{
	
	@Autowired
	private ClusterApplicationService clusterApplicationService;
	
	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		clusterApplicationService.deleteDbeeConfig();
		WebSocketCache.removeAllReplicaLog();
	}
}
