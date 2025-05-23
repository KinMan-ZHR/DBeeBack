package kinman.dbee.infrastructure.strategy.repo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import kinman.dbee.api.enums.AuthTypeEnum;
import kinman.dbee.api.enums.MessageCodeEnum;
import kinman.dbee.api.response.PageData;
import kinman.dbee.api.response.model.AppBranch;
import kinman.dbee.api.response.model.AppTag;
import kinman.dbee.api.response.model.GlobalConfigAgg.CodeRepo;
import kinman.dbee.api.response.model.GlobalConfigAgg.CodeRepo.GitLab;
import kinman.dbee.infrastructure.strategy.repo.param.BranchListParam;
import kinman.dbee.infrastructure.strategy.repo.param.BranchPageParam;
import kinman.dbee.infrastructure.strategy.repo.param.TagListParam;
import kinman.dbee.infrastructure.utils.DeploymentContext;
import kinman.dbee.infrastructure.utils.LogUtils;
import org.gitlab4j.api.Constants.MergeRequestState;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.MergeRequestApi;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.MergeRequest;
import org.gitlab4j.api.models.MergeRequestFilter;
import org.gitlab4j.api.models.MergeRequestParams;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.Tag;
import org.gitlab4j.api.models.TreeItem;
import org.gitlab4j.api.models.TreeItem.Type;


/**
 * GitLab代码仓库操作策略实现。
 * <p>
 * 此类负责通过 GitLab API (使用 gitlab4j-api 库) 与 GitLab 代码仓库进行交互，
 * 提供了下载、合并、创建、删除分支/标签以及查询列表等功能。
 * 它是 {@link CodeRepoStrategy} 策略模式的一个具体实现，用于 dbee 部署平台。
 * </p>
 *
 * @see CodeRepoStrategy
 * @see GitLabApi
 */
public class GitLabCodeRepoStrategy extends CodeRepoStrategy {

	@Override
	public boolean doDownloadBranch(DeploymentContext context) {
		String appId = context.getApp().getCodeRepoPath();
		String branchName = context.getBranchName();
		try (GitLabApi gitLabApi = gitLabApi(context.getGlobalConfigAgg().getCodeRepo())) {
			logger.info("Start to download branch");
			Project project = gitLabApi.getProjectApi().getProject(appId);
			if (project == null) {
				logger.warn("The project does not exist, appid is {}", appId);
				gitLabApi.close();
				return false;
			}

			String localPathOfBranch = localPathOfBranch(context);
			if (localPathOfBranch == null) {
				logger.warn("Failed to local path of branch, appid is {}", appId);
				gitLabApi.close();
				return false;
			}

			context.setLocalPathOfBranch(localPathOfBranch);

			List<TreeItem> trees = gitLabApi.getRepositoryApi().getTree(appId, null, branchName, true);
			for (TreeItem tree : trees) {
				File file = new File(localPathOfBranch + tree.getPath());
				if (Type.BLOB.equals(tree.getType())) {
					if (!file.exists()) {
						file.createNewFile();
					}
					byte[] buffer = new byte[1024 * 1024];
					int length = 0;
					try (InputStream inputStream = gitLabApi.getRepositoryFileApi().getRawFile(appId, branchName,
							tree.getPath());
						 FileOutputStream outStream = new FileOutputStream(file)) {
						while ((length = inputStream.read(buffer)) != -1) {
							outStream.write(buffer, 0, length);
						}
					}
				} else if (Type.TREE.equals(tree.getType()) && !file.exists()) {
					file.mkdir();
				}
			}
		} catch (Exception e) {
			logger.error("Failed to download branch", e);
			return false;
		}
		logger.info("End to download branch");
		return true;
	}

	public void mergeBranch(DeploymentContext context) {
		GitLabApi gitLabApi = gitLabApi(context.getGlobalConfigAgg().getCodeRepo());
		try {
			Branch branch = gitLabApi.getRepositoryApi().getBranch(context.getApp().getCodeRepoPath(), context.getBranchName());
			if(branch.getMerged() != null && branch.getMerged().booleanValue()) {
				logger.error("The branch has been merged");
				return;
			}
		} catch (GitLabApiException e) {
			if(e.getHttpStatus() != 404) {
				LogUtils.throwException(logger, e, MessageCodeEnum.APP_BRANCH_FAILURE);
			}
			
			//404，如果分支不存在，则判断是否是标签
			Tag tag = null;
			try {
				tag = gitLabApi.getTagsApi().getTag(context.getApp().getCodeRepoPath(), context.getBranchName());
			} catch (GitLabApiException e1) {
				logger.error("Failed to get tag, name: " + context.getBranchName(), e1);
			}
			
			//如果是标签，不合并代码
			if(tag != null) {
				logger.info("No need to merge code");
				return;
			}
			
			LogUtils.throwException(logger, e, MessageCodeEnum.APP_BRANCH_FAILURE);
		}
		
		MergeRequestApi mergeRequestApi = gitLabApi.getMergeRequestApi();
		MergeRequestFilter filter = new MergeRequestFilter();
		filter.setState(MergeRequestState.OPENED);
		filter.setProjectId(Long.valueOf(context.getApp().getCodeRepoPath()));
		MergeRequest mergeRequest = null;
		try {
			//1.如果存在未合并的请求，直接报错
			Stream<MergeRequest> stream = mergeRequestApi.getMergeRequestsStream(filter);
			if (stream != null && stream.count() > 0) {
				LogUtils.throwException(logger, MessageCodeEnum.UNFINISHED_MERGE_REQUEST_EXISTS);
			}
			// 提交合请求
			MergeRequestParams mergeRequestParams = new MergeRequestParams();
			mergeRequestParams.withSourceBranch(context.getBranchName());
			mergeRequestParams.withTargetBranch("master");
			mergeRequestParams.withTitle("dbee merge");
			mergeRequest = mergeRequestApi.createMergeRequest(context.getApp().getCodeRepoPath(),
					mergeRequestParams);
			// 接受合并请求
			mergeRequestApi.acceptMergeRequest(context.getApp().getCodeRepoPath(), mergeRequest.getIid());
		} catch (GitLabApiException e) {
			//如果合并失败，则关闭本次合并请求，目前由人工介入处理。
//			if(mergeRequest != null) {
//				try {
//					mergeRequestApi.cancelMergeRequest(context.getApp().getCodeRepoPath(), mergeRequest.getIid());
//				} catch (GitLabApiException e1) {
//					LogUtils.throwException(logger, MessageCodeEnum.MERGE_BRANCH);
//				}
//			}
			LogUtils.throwException(logger, e, MessageCodeEnum.MERGE_BRANCH);
		} finally {
			gitLabApi.close();
		}
	}

	@Override
	public void createBranch(CodeRepo codeRepo, String codeRepoPath, String branchName, String orgBranchName) {
		GitLabApi gitLabApi = gitLabApi(codeRepo);
		try {
			gitLabApi.getRepositoryApi().createBranch(codeRepoPath, branchName, orgBranchName);
		} catch (GitLabApiException e) {
			LogUtils.throwException(logger, e, MessageCodeEnum.CREATE_BRANCH_FAILURE);
		} finally {
			gitLabApi.close();
		}
	}

	@Override
	public void deleteBranch(CodeRepo codeRepo, String codeRepoPath, String branchName) {
		GitLabApi gitLabApi = gitLabApi(codeRepo);
		try {
			gitLabApi.getRepositoryApi().deleteBranch(codeRepoPath, branchName);
		} catch (GitLabApiException e) {
			LogUtils.throwException(logger, e, MessageCodeEnum.DELETE_BRANCH_FAILURE);
		} finally {
			gitLabApi.close();
		}
	}
	
	@Override
	public PageData<AppBranch> branchPage(CodeRepo codeRepo, BranchPageParam param) {
		GitLabApi gitLabApi = gitLabApi(codeRepo);
		List<Branch> list = null;
		try {
			list = gitLabApi.getRepositoryApi().getBranches(param.getAppIdOrPath(), param.getBranchName());
		} catch (GitLabApiException e) {
			LogUtils.throwException(logger, e, MessageCodeEnum.APP_BRANCH_PAGE_FAILURE);
		} finally {
			gitLabApi.close();
		}
		
		PageData<AppBranch> pageData = new PageData<>();
		int dataCount = list.size();
		if (dataCount == 0) {
			pageData.setPageNum(1);
			pageData.setPageCount(0);
			pageData.setPageSize(param.getPageSize());
			pageData.setItemCount(0);
			return pageData;
		}
		
		//按照提交时间倒排序
		list.sort((e1, e2) -> e2.getCommit().getCommittedDate().compareTo(e1.getCommit().getCommittedDate()));
		
		int pageCount = dataCount / param.getPageSize();
		if (dataCount % param.getPageSize() > 0) {
			pageCount += 1;
		}
		int pageNum = param.getPageNum() > pageCount ? pageCount : param.getPageNum();
		int startOffset = (pageNum - 1) * param.getPageSize();
		int endOffset = pageNum * param.getPageSize();
		endOffset = endOffset > dataCount ? dataCount : endOffset;
		List<Branch> page = list.subList(startOffset, endOffset);
		pageData.setItems(page.stream().map(e ->{
			AppBranch appBranch = new AppBranch();
			appBranch.setBranchName(e.getName());
			appBranch.setMergedStatus(e.getMerged() ? 1 : 0);
			appBranch.setUpdateTime(e.getCommit().getCommittedDate());
			appBranch.setCommitMessage(e.getCommit().getMessage());
			return appBranch;
		}).collect(Collectors.toList()));
		pageData.setPageNum(pageNum);
		pageData.setPageCount(pageCount);
		pageData.setPageSize(param.getPageSize());
		pageData.setItemCount(dataCount);
		
		return pageData;
	}
	
	@Override
	public List<AppBranch> branchList(CodeRepo codeRepo, BranchListParam param) {
		GitLabApi gitLabApi = gitLabApi(codeRepo);
		List<Branch> list = null;
		try {
			list = gitLabApi.getRepositoryApi().getBranches(param.getAppIdOrPath(), param.getBranchName());
		} catch (GitLabApiException e) {
			LogUtils.throwException(logger, e, MessageCodeEnum.APP_BRANCH_PAGE_FAILURE);
		} finally {
			gitLabApi.close();
		}
		
		int dataCount = list.size();
		if (dataCount == 0) {
			return Collections.emptyList();
		}
		//按照提交时间倒排序
		list.sort((e1, e2) -> e2.getCommit().getCommittedDate().compareTo(e1.getCommit().getCommittedDate()));
		return list.stream().map(e ->{
			AppBranch appBranch = new AppBranch();
			appBranch.setBranchName(e.getName());
			appBranch.setMergedStatus(e.getMerged() ? 1 : 0);
			appBranch.setUpdateTime(e.getCommit().getCommittedDate());
			return appBranch;
		}).collect(Collectors.toList());
	}
	
	@Override
	public PageData<AppTag> tagPage(CodeRepo codeRepo, BranchPageParam param) {
		GitLabApi gitLabApi = gitLabApi(codeRepo);
		List<Tag> list = null;
		try {
			list = gitLabApi.getTagsApi().getTags(param.getAppIdOrPath());
		} catch (GitLabApiException e) {
			LogUtils.throwException(logger, e, MessageCodeEnum.APP_TAG_PAGE_FAILURE);
		} finally {
			gitLabApi.close();
		}
		
		PageData<AppTag> pageData = new PageData<>();
		int dataCount = list.size();
		if (dataCount == 0) {
			pageData.setPageNum(1);
			pageData.setPageCount(0);
			pageData.setPageSize(param.getPageSize());
			pageData.setItemCount(0);
			return pageData;
		}
		
		//按照创建时间倒排序
		list.sort((e1, e2) -> e2.getCommit().getAuthoredDate().compareTo(e1.getCommit().getAuthoredDate()));
		
		int pageCount = dataCount / param.getPageSize();
		if (dataCount % param.getPageSize() > 0) {
			pageCount += 1;
		}
		int pageNum = param.getPageNum() > pageCount ? pageCount : param.getPageNum();
		int startOffset = (pageNum - 1) * param.getPageSize();
		int endOffset = pageNum * param.getPageSize();
		endOffset = endOffset > dataCount ? dataCount : endOffset;
		List<Tag> page = list.subList(startOffset, endOffset);
		pageData.setItems(page.stream().map(e ->{
			AppTag appTag = new AppTag();
			appTag.setTagName(e.getName());
			appTag.setUpdateTime(e.getCommit().getCommittedDate());
			appTag.setCommitMessage(e.getCommit().getMessage());
			return appTag;
		}).collect(Collectors.toList()));
		pageData.setPageNum(pageNum);
		pageData.setPageCount(pageCount);
		pageData.setPageSize(param.getPageSize());
		pageData.setItemCount(dataCount);
		
		return pageData;
	}
	
	@Override
	public List<AppTag> tagList(CodeRepo codeRepo, TagListParam param) {
		GitLabApi gitLabApi = gitLabApi(codeRepo);
		List<Tag> list = null;
		try {
			list = gitLabApi.getTagsApi().getTags(param.getAppIdOrPath());
		} catch (GitLabApiException e) {
			LogUtils.throwException(logger, e, MessageCodeEnum.APP_TAG_PAGE_FAILURE);
		} finally {
			gitLabApi.close();
		}
		
		int dataCount = list.size();
		if (dataCount == 0) {
			return Collections.emptyList();
		}
		//按照提交时间倒排序
		list.sort((e1, e2) -> e2.getCommit().getAuthoredDate().compareTo(e1.getCommit().getAuthoredDate()));
		return list.stream().map(e ->{
			AppTag appTag = new AppTag();
			appTag.setTagName(e.getName());
			appTag.setUpdateTime(e.getCommit().getCommittedDate());
			appTag.setCommitMessage(e.getCommit().getMessage());
			return appTag;
		}).collect(Collectors.toList());
	}
	
	@Override
	public void createTag(CodeRepo codeRepo, String codeRepoPath, String tagName, String branchName) {
		GitLabApi gitLabApi = gitLabApi(codeRepo);
		try {
			gitLabApi.getTagsApi().createTag(codeRepoPath, tagName, branchName);
		} catch (GitLabApiException e) {
			LogUtils.throwException(logger, e, MessageCodeEnum.CREATE_FAILURE);
		} finally {
			gitLabApi.close();
		}
	}

	@Override
	public void deleteTag(CodeRepo codeRepo, String codeRepoPath, String tagName) {
		GitLabApi gitLabApi = gitLabApi(codeRepo);
		try {
			gitLabApi.getTagsApi().deleteTag(codeRepoPath, tagName);
		} catch (GitLabApiException e) {
			LogUtils.throwException(logger, e, MessageCodeEnum.DELETE_FAILURE);
		} finally {
			gitLabApi.close();
		}
	}
	
	private GitLabApi gitLabApi(CodeRepo codeRepo) {
		GitLabApi gitLabApi = null;
		GitLab gitLab = codeRepo.getGitLab();
		if(AuthTypeEnum.TOKEN.getCode().equals(gitLab.getAuthType())) {
			gitLabApi = new GitLabApi(gitLab.getUrl(), gitLab.getAuthToken());
		}else if(AuthTypeEnum.ACCOUNT.getCode().equals(gitLab.getAuthType())) {
			try {
				gitLabApi = GitLabApi.oauth2Login(gitLab.getUrl(), gitLab.getAuthName(), gitLab.getAuthPassword());
			} catch (GitLabApiException e) {
				LogUtils.throwException(logger, e, MessageCodeEnum.AUTH_FAILURE);
			}
		}
		gitLabApi.setRequestTimeout(10000, 60000);
		return gitLabApi;
	}
}
