package kinman.dbee.rest.log;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kinman.dbee.api.enums.LogTypeEnum;
import kinman.dbee.infrastructure.component.SpringBeanContext;
import kinman.dbee.infrastructure.repository.LogRecordRepository;
import kinman.dbee.infrastructure.repository.po.LogRecordPO;
import kinman.dbee.infrastructure.utils.Constants;
import kinman.dbee.infrastructure.utils.DeploymentContext;
import kinman.dbee.infrastructure.utils.ThreadLocalUtils;
import kinman.dbee.infrastructure.utils.ThreadPoolUtils;
import kinman.dbee.rest.websocket.WebSocketCache;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import ch.qos.logback.core.spi.LifeCycle;

/**
 * 部署策略
 */
public class DeploymentPolicy implements LifeCycle {

	private boolean start;

	@Override
	public void start() {
		this.start = true;
	}

	@Override
	public void stop() {
		this.start = false;
	}

	@Override
	public boolean isStarted() {
		return start;
	}

	/**
	 * 自定义处理日志逻辑
	 */
	public void handler(String message) {
		DeploymentContext deployContext = ThreadLocalUtils.Deployment.get();
		if (deployContext == null) {
			return;
		}
		Callable<Void> writeLog = new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				Integer logType = LogTypeEnum.valueOf(deployContext.getEventType().name()).getCode();
				LogRecordPO logRecord = new LogRecordPO();
				logRecord.setAppId(deployContext.getApp().getId());
				logRecord.setBizId(deployContext.getId());
				logRecord.setLogType(logType);
				logRecord.setContent(message);
				logRecord.setCreationTime(new Date());
				
				WebSocketSession session = WebSocketCache.get(deployContext.getId() + logType);
				if(session != null) {
					Matcher m = Pattern.compile(Constants.CRLF).matcher(message);
					session.sendMessage(new TextMessage(m.replaceAll("<br/>")));
					WebSocketCache.put(session, logRecord);
				}
				
				LogRecordRepository repository = SpringBeanContext.getBean(LogRecordRepository.class);
				repository.add(logRecord);
				return null;
			}
		};
		ThreadPoolUtils.writeLog(writeLog);
	}
}