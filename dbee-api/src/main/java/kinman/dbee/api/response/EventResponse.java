package kinman.dbee.api.response;

import java.io.Serializable;

/**
 * 事件数据模型
 */
public class EventResponse<D> implements Response, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 事件码，见org.dbee.api.enums.EventCodeEnum
	 */
	private String eventCode;

	/**
	 * 事件数据，json格式
	 */
	private D data;

	public EventResponse() {

	}

	public String getEventCode() {
		return eventCode;
	}

	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
	}

	public D getData() {
		return data;
	}

	public void setData(D data) {
		this.data = data;
	}

}