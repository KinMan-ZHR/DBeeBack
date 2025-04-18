package kinman.dbee.rest.component;

import kinman.dbee.rest.websocket.DeploymentDetailLogWebSocket;
import kinman.dbee.rest.websocket.ssh.SSHWebSocketHandler;
import kinman.dbee.rest.websocket.BuildVersionLogWebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * websocket配置
 */
@Configuration
@EnableWebSocket
public class WebSSHWebSocketConfig implements WebSocketConfigurer {
	
	@Autowired
	private SSHWebSocketHandler terminalWebSocketHandler;
	
	@Autowired
	private DeploymentDetailLogWebSocket deploymentDetailLogWebSocket;
	
	@Autowired
	private BuildVersionLogWebSocket buildVersionLogWebSocket;
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
		webSocketHandlerRegistry.addHandler(terminalWebSocketHandler, "/terminal")
			.addHandler(deploymentDetailLogWebSocket, "/deployment/log")
			.addHandler(buildVersionLogWebSocket, "/build/log")
			.setAllowedOrigins("*");
	}
}
