package kinman.dbee.infrastructure.strategy.repo;

import java.io.File;
import java.io.IOException;
import java.util.List;

import kinman.dbee.api.enums.MessageCodeEnum;
import kinman.dbee.api.response.PageData;
import kinman.dbee.api.response.model.AppBranch;
import kinman.dbee.api.response.model.AppTag;
import kinman.dbee.api.response.model.GlobalConfigAgg.CodeRepo;
import kinman.dbee.infrastructure.strategy.repo.param.BranchListParam;
import kinman.dbee.infrastructure.strategy.repo.param.BranchPageParam;
import kinman.dbee.infrastructure.strategy.repo.param.TagListParam;
import kinman.dbee.infrastructure.utils.DeploymentContext;
import kinman.dbee.infrastructure.utils.FileUtils;
import kinman.dbee.infrastructure.utils.K8sUtils;
import kinman.dbee.infrastructure.utils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CodeRepoStrategy {

	public static final Logger logger = LoggerFactory.getLogger(CodeRepoStrategy.class);
	
	void clearLocalHistoryCode(DeploymentContext context) {
		String localPathOfBranch = localPathOfBranch(context);
		File[] hisBranchs = new File(localPathOfBranch(context)).listFiles();
		for(File h : hisBranchs) {
			//为了提高Node类应用install过程的性能，不删除以下文件
			if("node_modules".equals(h.getName()) || ".nuxt".equals(h.getName())) {
				continue;
			}
			try {
				if(h.isDirectory()) {
					FileUtils.deleteDirectory(h);
				}else {
					h.delete();
				}
			} catch (IOException e) {
				logger.error("Failed to clear local history code, please delete it"
						+ " manually, path is: " + localPathOfBranch, e);
				LogUtils.throwException(logger, MessageCodeEnum.DELETE_FAILURE);
			}
		}
	}
	
	public boolean downloadBranch(DeploymentContext context) {
		checkLocalPathOfBranch(context);
		clearLocalHistoryCode(context);
		return doDownloadBranch(context);
	}
	
	abstract boolean doDownloadBranch(DeploymentContext context);
	
	public abstract void mergeBranch(DeploymentContext context);
	
	String checkLocalPathOfBranch(DeploymentContext context) {
		String localPathOfBranch = localPathOfBranch(context);
		File pathFile = new File(localPathOfBranch);
		if (!pathFile.exists()) {
			logger.info("The local path of branch does not exist, and create it");
			if(pathFile.mkdirs()) {
				logger.info("Create local path of branch successfully");
			}else {
				logger.error("Failed to create local path of branch");
				return null;
			}
		}
		
		return localPathOfBranch;
	}
	
	public String localPathOfBranch(DeploymentContext context) {
		return new StringBuilder()
				.append(context.getComponentConstants().getDataPath())
				.append(K8sUtils.APP_KEY)
				.append(File.separator)
				.append(context.getApp().getAppName())
				.append(File.separator)
				.toString();
	}
	
	public abstract void createBranch(CodeRepo codeRepo, String codeRepoPath, String branchName, String orgBranchName);
	
	public abstract void deleteBranch(CodeRepo codeRepo, String codeRepoPath, String branchName);
	
	public abstract PageData<AppBranch> branchPage(CodeRepo codeRepo, BranchPageParam param);
	
	public abstract List<AppBranch> branchList(CodeRepo codeRepo, BranchListParam param);
	
	public abstract PageData<AppTag> tagPage(CodeRepo codeRepo, BranchPageParam param);
	
	public abstract List<AppTag> tagList(CodeRepo codeRepo, TagListParam param);
	
	public abstract void createTag(CodeRepo codeRepo, String codeRepoPath, String tagName, String branchName);
	
	public abstract void deleteTag(CodeRepo codeRepo, String codeRepoPath, String tagName);
}
