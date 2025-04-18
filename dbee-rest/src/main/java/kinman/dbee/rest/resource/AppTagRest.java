package kinman.dbee.rest.resource;

import java.util.List;

import kinman.dbee.api.param.app.branch.AppTagListParam;
import kinman.dbee.api.param.app.tag.AppTagCreationParam;
import kinman.dbee.api.param.app.tag.AppTagDeletionParam;
import kinman.dbee.api.param.app.tag.AppTagPageParam;
import kinman.dbee.api.response.PageData;
import kinman.dbee.api.response.RestResponse;
import kinman.dbee.api.response.model.AppTag;
import kinman.dbee.application.service.AppTagApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * 应用标签
 */
@RestController
@RequestMapping("/app/tag")
public class AppTagRest extends AbstractRest {

	@Autowired
	private AppTagApplicationService appTagApplicationService;

	/**
	 * 分页查询
	 * 
	 * @param appBranchPageParam 分页参数
	 * @return 符合条件的分页数据
	 */
	@PostMapping("/page")
	public RestResponse<PageData<AppTag>> page(@CookieValue(name = "login_token", required = false) String loginToken,
			@RequestBody AppTagPageParam appTagPageParam) {
		return success(appTagApplicationService.page(queryLoginUserByToken(loginToken),
				appTagPageParam));
	}
	
	/**
	 * 搜索分支
	 * 
	 * @param appTagListParam 分页参数
	 * @return 符合条件的数据
	 */
	@PostMapping("/search")
	public RestResponse<List<AppTag>> search(@CookieValue(name = "login_token", required = false) String loginToken,
			@RequestBody AppTagListParam appTagListParam) {
		return success(appTagApplicationService.list(queryLoginUserByToken(loginToken),
				appTagListParam));
	}

	/**
	 * 添加
	 * 
	 * @param appTagCreationParam 添加参数
	 * @return 无
	 */
	@PostMapping("/add")
	public RestResponse<Void> add(@CookieValue(name = "login_token", required = false) String loginToken,
			@RequestBody AppTagCreationParam appTagCreationParam) {
		return success(appTagApplicationService.add(queryLoginUserByToken(loginToken),
				appTagCreationParam));
	}

	/**
	 * 删除
	 * 
	 * @param appTagDeletionParam 删除参数
	 * @return 无
	 */
	@PostMapping("/delete")
	public RestResponse<Void> delete(@CookieValue(name = "login_token", required = false) String loginToken,
			@RequestBody AppTagDeletionParam appTagDeletionParam) {
		return success(appTagApplicationService.delete(queryLoginUserByToken(loginToken),
				appTagDeletionParam));
	}
}