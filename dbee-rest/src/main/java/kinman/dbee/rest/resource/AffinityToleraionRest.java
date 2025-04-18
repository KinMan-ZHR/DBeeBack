package kinman.dbee.rest.resource;

import kinman.dbee.api.param.app.env.affinity.AffinityTolerationCreationParam;
import kinman.dbee.api.param.app.env.affinity.AffinityTolerationDeletionParam;
import kinman.dbee.api.param.app.env.affinity.AffinityTolerationPageParam;
import kinman.dbee.api.param.app.env.affinity.AffinityTolerationQueryParam;
import kinman.dbee.api.param.app.env.affinity.AffinityTolerationUpdateParam;
import kinman.dbee.api.response.PageData;
import kinman.dbee.api.response.RestResponse;
import kinman.dbee.api.response.model.AffinityToleration;
import kinman.dbee.application.service.AffinityTolerationApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * 亲和容忍配置服务
 */
@RestController
@RequestMapping("/app/env/affinity")
public class AffinityToleraionRest extends AbstractRest {

	@Autowired
	private AffinityTolerationApplicationService service;

	/**
	 * 分页查询
	 * 
	 * @param 分页参数
	 * @return 符合条件的分页数据
	 */
	@PostMapping("/page")
	public RestResponse<PageData<AffinityToleration>> page(@CookieValue(name = "login_token",
			required = false) String loginToken,
			@RequestBody AffinityTolerationPageParam pageParam) {
		return success(service.page(queryLoginUserByToken(loginToken), pageParam));
	}
	
	/**
	 * 查询
	 * 
	 * @param 查询参数
	 * @return 符合条件的数据
	 */
	@PostMapping("/query")
	public RestResponse<AffinityToleration> query(@CookieValue(name = "login_token",
			required = false) String loginToken,
			@RequestBody AffinityTolerationQueryParam queryParam) {
		return success(service.query(queryLoginUserByToken(loginToken), queryParam));
	}

	/**
	 * 添加
	 * 
	 * @param addParam 添加参数
	 * @return 无
	 */
	@PostMapping("/add")
	public RestResponse<Void> add(@RequestBody AffinityTolerationCreationParam addParam) {
		return success(service.add(addParam));
	}

	/**
	 * 修改
	 * 
	 * @param updateParam 修改参数
	 * @return 无
	 */
	@PostMapping("/update")
	public RestResponse<Void> update(@CookieValue(name = "login_token", required = false) String loginToken,
			@RequestBody AffinityTolerationUpdateParam updateParam) {
		return success(service.update(queryLoginUserByToken(loginToken), updateParam));
	}

	/**
	 * 修改
	 * 
	 * @param updateParam 修改参数
	 * @return 无
	 */
	@PostMapping("/openStatus")
	public RestResponse<Void> openStatus(@CookieValue(name = "login_token", required = false) String loginToken,
			@RequestBody AffinityTolerationUpdateParam updateParam) {
		return success(service.openStatus(queryLoginUserByToken(loginToken), updateParam));
	}
	
	/**
	 * 删除
	 * 
	 * @param deletionParam 删除参数
	 * @return 无
	 */
	@PostMapping("/delete")
	public RestResponse<Void> delete(@CookieValue(name = "login_token", required = false) String loginToken,
			@RequestBody AffinityTolerationDeletionParam deletionParam) {
		return success(service.delete(queryLoginUserByToken(loginToken), deletionParam));
	}

}