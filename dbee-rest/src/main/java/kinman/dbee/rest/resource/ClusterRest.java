package kinman.dbee.rest.resource;

import java.util.List;

import kinman.dbee.api.param.cluster.ClusterCreationParam;
import kinman.dbee.api.param.cluster.ClusterDeletionParam;
import kinman.dbee.api.param.cluster.ClusterNodePageParam;
import kinman.dbee.api.param.cluster.ClusterPageParam;
import kinman.dbee.api.param.cluster.ClusterQueryParam;
import kinman.dbee.api.param.cluster.ClusterSearchParam;
import kinman.dbee.api.param.cluster.ClusterUpdateParam;
import kinman.dbee.api.param.cluster.LogSwitchParam;
import kinman.dbee.api.param.cluster.NodeCreationParam;
import kinman.dbee.api.param.cluster.NodeDeletionParam;
import kinman.dbee.api.response.PageData;
import kinman.dbee.api.response.RestResponse;
import kinman.dbee.api.response.model.Cluster;
import kinman.dbee.api.response.model.ClusterNode;
import kinman.dbee.api.response.model.LogCollectorStatus;
import kinman.dbee.application.service.ClusterApplicationService;
import kinman.dbee.infrastructure.annotation.AccessOnlyAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * 集群
 */
@RestController
@RequestMapping("/cluster")
public class ClusterRest extends AbstractRest {

	@Autowired
	private ClusterApplicationService clusterApplicationService;
	
	/**
	 * 分页查询
	 * 
	 * @param clusterPageParam 查询参数
	 * @return 符合条件的分页数据
	 */
	@AccessOnlyAdmin
	@PostMapping("/page")
	public RestResponse<PageData<Cluster>> page(@RequestBody ClusterPageParam clusterPageParam) {
		return this.success(clusterApplicationService.page(clusterPageParam));
	}
	
	/**
	 * 分页查询节点
	 * 
	 * @param clusterNodePageParam 查询参数
	 * @return 符合条件的分页数据
	 */
	@AccessOnlyAdmin
	@PostMapping("/node/page")
	public RestResponse<PageData<ClusterNode>> nodePage(@RequestBody ClusterNodePageParam clusterNodePageParam) {
		return this.success(clusterApplicationService.nodePage(clusterNodePageParam));
	}
	
	/**
	 * 添加节点
	 * 
	 * @param creationParam 添加节点参数
	 * @return 无
	 */
	@AccessOnlyAdmin
	@PostMapping("/node/add")
	public RestResponse<Void> addNode(@RequestBody NodeCreationParam creationParam) {
		return this.success(clusterApplicationService.addNode(creationParam));
	}
	
	/**
	 * 删除节点
	 *
	 * @param deletionParam 删除节点参数
	 * @return 无
	 */
	@AccessOnlyAdmin
	@PostMapping("/node/delete")
	public RestResponse<Void> deleteNode(@RequestBody NodeDeletionParam deletionParam) {
		return this.success(clusterApplicationService.deleteNode(deletionParam));
	}
	
	/**
	 * 查询
	 *
	 * @param clusterQueryParam 查询参数
	 * @return 符合条件的数据
	 */
	@AccessOnlyAdmin
	@PostMapping("/query")
	public RestResponse<Cluster> query(@RequestBody ClusterQueryParam clusterQueryParam) {
		return this.success(clusterApplicationService.query(clusterQueryParam));
	}
	
	/**
	 * 搜索集群
	 * 
	 * @param clusterSearchParam 搜索参数
	 * @return 符合条件的数据
	 */
	@PostMapping("/search")
	public RestResponse<List<Cluster>> search(@RequestBody ClusterSearchParam clusterSearchParam) {
		return this.success(clusterApplicationService.search(clusterSearchParam));
	}
	
	/**
	 * 添加
	 * 
	 * @param clusterCreationParam 添加集群参数
	 * @return 无
	 */
	@AccessOnlyAdmin
	@PostMapping("/add")
	public RestResponse<Void> add(@RequestBody ClusterCreationParam clusterCreationParam) {
		return this.success(clusterApplicationService.add(clusterCreationParam));
	}
	
	/**
	 * 修改
	 * 
	 * @param clusterUpdateParam 修改服务器集群参数
	 * @return 无
	 */
	@AccessOnlyAdmin
	@PostMapping("/update")
	public RestResponse<Void> update(@RequestBody ClusterUpdateParam clusterUpdateParam) {
		return this.success(clusterApplicationService.update(clusterUpdateParam));
	}
	
	/**
	 * 删除
	 * 
	 * @param clusterDeletionParam 删除服务器集群参数
	 * @return 无
	 */
	@AccessOnlyAdmin
	@PostMapping("/delete")
	public RestResponse<Void> delete(@RequestBody ClusterDeletionParam clusterDeletionParam) {
		return this.success(clusterApplicationService.delete(clusterDeletionParam.getClusterId()));
	}
	
	/**
	 * 开启日志收集
	 * 
	 * @param logSwitchParam 日志收集器参数
	 * @return 无
	 */
	@AccessOnlyAdmin
	@PostMapping("/logSwitch/open")
	public RestResponse<Void> openLogSwitch(@RequestBody LogSwitchParam logSwitchParam) {
		return this.success(clusterApplicationService.openLogSwitch(logSwitchParam.getClusterId()));
	}
	
	/**
	 * 关闭日志收集
	 * 
	 * @param logSwitchParam 日志收集器参数
	 * @return 无
	 */
	@AccessOnlyAdmin
	@PostMapping("/logSwitch/close")
	public RestResponse<Void> closeLogSwitch(@RequestBody LogSwitchParam logSwitchParam) {
		return this.success(clusterApplicationService.closeLogSwitch(logSwitchParam.getClusterId()));
	}
	
	/**
	 * 查询日志收集器状态
	 * 
	 * @param logSwitchParam 日志收集器参数
	 * @return 日志收集器状态
	 */
	@AccessOnlyAdmin
	@PostMapping("/logSwitch/status")
	public RestResponse<LogCollectorStatus> logSwitchStatus(@RequestBody LogSwitchParam logSwitchParam) {
		return this.success(clusterApplicationService.logSwitchStatus(logSwitchParam));
	}
}
