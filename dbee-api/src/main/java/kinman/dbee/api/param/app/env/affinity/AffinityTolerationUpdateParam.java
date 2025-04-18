package kinman.dbee.api.param.app.env.affinity;

/**
 * 修改亲和容忍配置参数模型
 */
public class AffinityTolerationUpdateParam extends AffinityTolerationCreationParam {

	private static final long serialVersionUID = 1L;

	/**
	 * 亲和容忍配置编号
	 */
	private String affinityTolerationId;

	public String getAffinityTolerationId() {
		return affinityTolerationId;
	}

	public void setAffinityTolerationId(String affinityTolerationId) {
		this.affinityTolerationId = affinityTolerationId;
	}

}