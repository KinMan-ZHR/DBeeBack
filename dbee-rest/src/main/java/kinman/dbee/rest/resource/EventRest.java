package kinman.dbee.rest.resource;

import kinman.dbee.api.event.DeploymentMessage;
import kinman.dbee.api.response.EventResponse;
import kinman.dbee.api.response.RestResponse;
import kinman.dbee.infrastructure.annotation.AccessNotLogin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AccessNotLogin
@RestController
@RequestMapping("/event")
public class EventRest extends AbstractRest {
	
	@PostMapping("/receive")
	public RestResponse<Void> receive(@RequestBody EventResponse<DeploymentMessage> reponse) {
		return success();
	}
}
