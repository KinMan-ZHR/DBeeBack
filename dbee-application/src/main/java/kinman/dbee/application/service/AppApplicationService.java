package kinman.dbee.application.service;

import com.google.cloud.tools.jib.api.Containerizer;
import com.google.cloud.tools.jib.api.Jib;
import com.google.cloud.tools.jib.api.LogEvent;
import com.google.cloud.tools.jib.api.RegistryImage;
import com.google.cloud.tools.jib.api.buildplan.AbsoluteUnixPath;
import com.google.cloud.tools.jib.api.buildplan.Port;
import kinman.dbee.api.enums.*;
import kinman.dbee.api.param.app.AppCreationParam;
import kinman.dbee.api.param.app.AppDeletionParam;
import kinman.dbee.api.param.app.AppPageParam;
import kinman.dbee.api.param.app.AppUpdateParam;
import kinman.dbee.api.response.PageData;
import kinman.dbee.api.response.model.*;
import kinman.dbee.api.response.model.App.AppExtend;
import kinman.dbee.api.response.model.GlobalConfigAgg.ImageRepo;
import kinman.dbee.infrastructure.exception.ApplicationException;
import kinman.dbee.infrastructure.param.AppEnvParam;
import kinman.dbee.infrastructure.param.AppMemberParam;
import kinman.dbee.infrastructure.param.AppParam;
import kinman.dbee.infrastructure.param.GlobalConfigParam;
import kinman.dbee.infrastructure.repository.po.AppEnvPO;
import kinman.dbee.infrastructure.repository.po.AppPO;
import kinman.dbee.infrastructure.strategy.login.dto.LoginUser;
import kinman.dbee.infrastructure.utils.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 
 * 应用应用服务
 */
@Service
public class AppApplicationService extends BaseApplicationService<App, AppPO> {

	private static final Logger logger = LoggerFactory.getLogger(AppApplicationService.class);
	
	@Autowired
	private GlobalConfigApplicationService globalConfigApplicationService;
	
	public PageData<App> page(LoginUser loginUser, AppPageParam param) {
		AppParam bizParam = new AppParam();
		bizParam.setTechType(param.getTechType());
		bizParam.setAppName(param.getAppName());
		bizParam.setPageNum(param.getPageNum());
		bizParam.setPageSize(param.getPageSize());
		return appRepository.page(loginUser, bizParam);
	}
	
	public List<App> search(AppPageParam param) {
		AppParam bizParam = new AppParam();
		bizParam.setTechType(param.getTechType());
		bizParam.setAppName(param.getAppName());
		List<AppPO> pos = appRepository.list(bizParam);
		if(CollectionUtils.isEmpty(pos)) {
			return Collections.emptyList();
		}
		return pos.stream()
				.sorted((e1, e2)-> e1.getAppName().compareTo(e2.getAppName()))
				.map(e ->{
					App app = new App();
					app.setId(e.getId());
					app.setAppName(e.getAppName());
					return app;
				})
				.collect(Collectors.toList());
	}
	
	public App query(LoginUser loginUser, String appId) {
		return appRepository.queryWithExtendById(loginUser, appId);
	}
	
	@Transactional(rollbackFor = Throwable.class)
	public App add(LoginUser loginUser, AppCreationParam addParam) {
		validateAddParam(addParam);
		AppParam param = new AppParam();
		param.setAppName(addParam.getAppName());
		if(appRepository.query(param) != null) {
			LogUtils.throwException(logger, MessageCodeEnum.APP_NAME_EXISTENCE);
		}
		String appId = appRepository.add(buildBizParam(addParam));
		if(appId == null) {
			LogUtils.throwException(logger, MessageCodeEnum.FAILURE);
		}
		//添加管理员
		AppMemberParam appMemberParam = new AppMemberParam();
		appMemberParam.setUserId(loginUser.getId());
		appMemberParam.setLoginName(loginUser.getLoginName());
		appMemberParam.setAppId(appId);
		appMemberParam.setRoleTypes(Arrays.asList(AppMemberRoleTypeEnum.ADMIN.getCode()));
		appMemberRepository.add(appMemberParam);
		
		//异步制作镜像
		ThreadPoolUtils.async(() -> {
			buildJdkImage(addParam);
			buildTomcatImage(addParam, appId);
		});
		
		App app = new App();
		app.setId(appId);
		return app;
	}
	
	@Transactional(rollbackFor = Throwable.class)
	public Void update(LoginUser loginUser, AppUpdateParam updateParam) {
		if(StringUtils.isBlank(updateParam.getAppId())){
			LogUtils.throwException(logger, MessageCodeEnum.APP_ID_IS_NULL);
		}
		validateAddParam(updateParam);
		AppParam appParam = buildBizParam(updateParam);
		appParam.setId(updateParam.getAppId());
		if(!appRepository.update(loginUser, appParam)) {
			LogUtils.throwException(logger, MessageCodeEnum.FAILURE);
		}
		//异步制作镜像
		ThreadPoolUtils.async(() -> {
			buildJdkImage(updateParam);
			buildTomcatImage(updateParam, updateParam.getAppId());
		});
		return null;
	}
	
	private void buildJdkImage(AppCreationParam addParam) {
		if(!TechTypeEnum.SPRING_BOOT.getCode().equals(addParam.getTechType())) {
			return;
		}
		if(!ImageSourceEnum.VERSION.getCode().equals(addParam.getBaseImageSource())) {
			return;
		}
		if(!PackageFileTypeEnum.JAR.getCode().equals(addParam.getExtendSpringBootParam().getPackageFileType())) {
			return;
		}
		
		GlobalConfigParam bizParam = new GlobalConfigParam();
		bizParam.setItemTypes(Arrays.asList(GlobalConfigItemTypeEnum.MAVEN.getCode(),
				GlobalConfigItemTypeEnum.IMAGE_REPO.getCode()));
		GlobalConfigAgg globalConfigAgg = globalConfigRepository.queryAgg(bizParam);
		
		//构建镜像
		doBuildJdkImage(addParam.getBaseImageVersion(), globalConfigAgg);
	}
	
	private void buildTomcatImage(AppCreationParam addParam, String appId) {
		if(!TechTypeEnum.SPRING_BOOT.getCode().equals(addParam.getTechType())) {
			return;
		}
		if(!ImageSourceEnum.VERSION.getCode().equals(addParam.getBaseImageSource())) {
			return;
		}
		if(!PackageFileTypeEnum.WAR.getCode().equals(addParam.getExtendSpringBootParam().getPackageFileType())) {
			return;
		}
		
		GlobalConfigParam bizParam = new GlobalConfigParam();
		bizParam.setItemTypes(Arrays.asList(GlobalConfigItemTypeEnum.MAVEN.getCode(),
				GlobalConfigItemTypeEnum.IMAGE_REPO.getCode()));
		GlobalConfigAgg globalConfigAgg = globalConfigRepository.queryAgg(bizParam);
		List<String> jdkVersions = globalConfigApplicationService.queryJavaVersion("xxxxxx");
		
		//构建Jdk镜像
		String jdkImageName = doBuildJdkImage(jdkVersions.get(0), globalConfigAgg);
		
		ImageRepo imageRepo = globalConfigAgg.getImageRepo();
		String tomcatVersion = TomcatVersionEnum.getByCode(Integer.valueOf(addParam.getBaseImageVersion())).getValue();
		String tag = "v" + tomcatVersion + "-" + jdkVersions.get(0);
		String imageName = Constants.IMAGE_NAME_TOMCAT + ":" + tag;
		String fullNameOfImage = fullNameOfImage(imageRepo, imageName);
		if(imageTagExisting(imageRepo, Constants.IMAGE_NAME_TOMCAT, tag)) {
			logger.info("The image {} is existed, do not rebuild", fullNameOfImage);
			return;
		}
		
		//1.下载文件
		String fileName = String.format("apache-tomcat-%s", tomcatVersion);
		String fileNameWithExtend = fileName + ".tar.gz";
		String fileUrl = String.format("https://dlcdn.apache.org/tomcat/tomcat-%s/v%s/bin/%s",
				addParam.getBaseImageVersion(), tomcatVersion, fileNameWithExtend);
		long current = System.currentTimeMillis();
		File localFilePath = new File(componentConstants.getDataPath() + Constants.RELATIVE_TMP_PATH + "tomcat/"+ current + "/" + fileNameWithExtend);
		File parentFile = localFilePath.getParentFile();
		parentFile.mkdirs();
		FileUtils.downloadFile(fileUrl, localFilePath);
		
		//2.解压文件
		FileUtils.unTarGz(localFilePath, parentFile);
		
		//3.重命名目录为tomcat
		try {
			FileUtils.copyDirectory(new File(parentFile + "/" + fileName), new File(parentFile + "/tomcat"));
		} catch (IOException e) {
			logger.error("Failed to rename file", e);
		}
		
		logger.info("Start to build tomcat image");
		
		//3.制作Agent镜像并上传到仓库
		//Jib环境变量
		jibProperty();
		try {
			RegistryImage registryImage = RegistryImage.named(fullNameOfImage).addCredential(
					imageRepo.getAuthName(),
					imageRepo.getAuthPassword());
			Jib.from(jdkImageName)
				.addLayer(Arrays.asList(Paths.get(parentFile + "/tomcat")), AbsoluteUnixPath.get("/usr/local"))
				.setEntrypoint(Arrays.asList("sh", "-c", "chmod +x $JAVA_HOME/bin/java && sh /usr/local/tomcat/bin/catalina.sh run"))
				.addExposedPort(Port.tcp(8080))
				.containerize(Containerizer.to(registryImage)
						.setAllowInsecureRegistries(true)
						.addEventHandler(LogEvent.class, logEvent -> logger.info(logEvent.getMessage())));
		} catch (Exception e) {
			logger.error("Failed to build tomcat image", e);
		}finally {
			try {
				FileUtils.deleteDirectory(parentFile);
			} catch (IOException e) {
				logger.error("Failed to delete agent file", e);
			}
			logger.info("End to build tomcat image");
		}
	}
	
	private String doBuildJdkImage(String version, GlobalConfigAgg globalConfigAgg) {
		ImageRepo imageRepo = globalConfigAgg.getImageRepo();
		String fullNameOfImage = fullNameOfImage(imageRepo, imageNameOfJdk(version));
		if(imageTagExisting(imageRepo, Constants.IMAGE_NAME_JDK, tagOfJdk(version))) {
			logger.info("The {} is existed, do not rebuild", fullNameOfImage);
			return fullNameOfImage;
		}
		
		String javaHome = null;
//		if(globalConfigAgg.getMaven() != null && !StringUtils.isBlank(globalConfigAgg.getMaven().getJavaHome())) {
//			javaHome = globalConfigAgg.getMaven().getJavaHome();
//		}
		if(StringUtils.isBlank(javaHome)){
			javaHome = System.getProperty("java.home");
		}
		
		logger.info("Start to build jdk image");
		
		//Jib环境变量
		jibProperty();
		
		Path javaHomePath = Paths.get(javaHome);
		Map<String, String> environmentMap = new HashMap<>();
		String home = "/usr/local/" + javaHomePath.toFile().getName();
		environmentMap.put("JAVA_HOME", home);
		environmentMap.put("PATH", home + "/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin");
		try {
			RegistryImage registryImage = RegistryImage.named(fullNameOfImage).addCredential(
					imageRepo.getAuthName(),
					imageRepo.getAuthPassword());
			Jib.from(Constants.CENTOS_IMAGE_URL)
				.addLayer(Arrays.asList(javaHomePath), AbsoluteUnixPath.get("/usr/local"))
				//在exec模式下，不可以使用环境变量
				.setProgramArguments(Arrays.asList("chmod", "+x", home + "/bin/java"))
				.setEnvironment(environmentMap)
				.containerize(Containerizer.to(registryImage)
						.setAllowInsecureRegistries(true)
						.addEventHandler(LogEvent.class, logEvent -> logger.info(logEvent.getMessage())));
		} catch (Exception e) {
			LogUtils.throwException(logger, e, MessageCodeEnum.BUILD_IMAGE);
		}
		logger.info("End to build jdk image");
		return fullNameOfImage;
	}
	
	private boolean imageTagExisting(ImageRepo imageRepo, String imageName, String tag) {
        String uri = "api/v2.0/projects/"+ Constants.DBEE_TAG +"/repositories/" + imageName + "/artifacts/" + tag + "/tags";
        if(!imageRepo.getUrl().endsWith("/")) {
        	uri = "/" + uri;
        }
        HttpGet http = new HttpGet(imageRepo.getUrl() + uri);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(5000)
                .setConnectTimeout(5000)
                .setSocketTimeout(5000)
                .build();
        http.setConfig(requestConfig);
        http.setHeader("Content-Type", "application/json;charset=UTF-8");
        http.setHeader("Authorization", "Basic "+ Base64.getUrlEncoder().encodeToString((imageRepo.getAuthName()
        		+ ":" + imageRepo.getAuthPassword()).getBytes()));
        try (CloseableHttpResponse response = HttpUtils.createHttpClient(imageRepo.getUrl()).execute(http)){
            return response.getStatusLine().getStatusCode() == 200;
        } catch (IOException e) {
        	logger.error("Failed to query image tag", e);
        	return false;
        }
	}
	
	@Transactional(rollbackFor = Throwable.class)
	public Void delete(LoginUser loginUser, AppDeletionParam deleteParam) {
		if(deleteParam.getAppId() == null) {
			LogUtils.throwException(logger, MessageCodeEnum.APP_ID_IS_NULL);
		}
		//1.如果存在关联的环境，则不允许删除
		AppEnvParam appEnvParam = new AppEnvParam();
		appEnvParam.setAppId(deleteParam.getAppId());
		AppEnvPO appEnvPO = appEnvRepository.query(appEnvParam);
		if(appEnvPO != null) {
			LogUtils.throwException(logger, MessageCodeEnum.APP_ENV_DELETED);
		}
		//2.然后才能删除应用
		AppParam appParam = new AppParam();
		appParam.setId(deleteParam.getAppId());
		boolean successful = appRepository.delete(loginUser, appParam);
		appMemberRepository.deleteByAppId(deleteParam.getAppId());
		deploymentDetailRepository.deleteByAppId(deleteParam.getAppId());
		if(!successful) {
			LogUtils.throwException(logger, MessageCodeEnum.FAILURE);
		}
		return null;
	}
	
	private void validateAddParam(AppCreationParam addParam) {
		if(StringUtils.isBlank(addParam.getAppName())){
			LogUtils.throwException(logger, MessageCodeEnum.APP_NAME_IS_EMPTY);
		}
		if(StringUtils.isBlank(addParam.getCodeRepoPath())){
			LogUtils.throwException(logger, MessageCodeEnum.CODE_REPO_PATH_IS_EMPTY);
		}
		if(Objects.isNull(addParam.getTechType())){
			LogUtils.throwException(logger, MessageCodeEnum.TECH_TYPE_IS_EMPTY);
		}
		if(addParam.getAppName().length() > 32) {
			throw new ApplicationException(MessageCodeEnum.INVALID_PARAM.getCode(), "应用名称不能大于32个字符");
		}
		if(!Pattern.compile("^[a-z]+[\\-a-z]*$").matcher(addParam.getAppName()).matches()) {
			throw new ApplicationException(MessageCodeEnum.INVALID_PARAM.getCode(), "应用名称只能包含小写字母、中划线");
		}
		if(addParam.getBaseImage() != null && addParam.getBaseImage().length() > 128) {
			throw new ApplicationException(MessageCodeEnum.INVALID_PARAM.getCode(), "基础镜像不能大于128个字符");
		}
		if(addParam.getCodeRepoPath().length() > 64) {
			throw new ApplicationException(MessageCodeEnum.INVALID_PARAM.getCode(), "代码仓库地址不能大于64个字符");
		}

		if(addParam.getDescription() != null && addParam.getDescription().length() > 128) {
			throw new ApplicationException(MessageCodeEnum.INVALID_PARAM.getCode(), "应用描述不能大于128个字符");
		}
		if(TechTypeEnum.VUE.getCode().equals(addParam.getTechType())
				|| TechTypeEnum.REACT.getCode().equals(addParam.getTechType())){
			if(StringUtils.isBlank(addParam.getExtendNodeParam().getNodeVersion())) {
				throw new ApplicationException(MessageCodeEnum.INVALID_PARAM.getCode(), "Node版本不能为空");
			}
			if(Objects.isNull(addParam.getExtendNodeParam().getCompileType())) {
				throw new ApplicationException(MessageCodeEnum.INVALID_PARAM.getCode(), "编译工具不能为空");
			}
			if(addParam.getExtendNodeParam().getCompileType().equals(NodeCompileTypeEnum.NPM.getCode())
					&& StringUtils.isBlank(addParam.getExtendNodeParam().getNpmVersion())) {
				throw new ApplicationException(MessageCodeEnum.INVALID_PARAM.getCode(), "Npm版本不能为空");
			}
			if(addParam.getExtendNodeParam().getCompileType().equals(NodeCompileTypeEnum.PNPM.getCode())
					&& StringUtils.isBlank(addParam.getExtendNodeParam().getPnpmVersion())) {
				throw new ApplicationException(MessageCodeEnum.INVALID_PARAM.getCode(), "Pnpm版本不能为空");
			}
			if(addParam.getExtendNodeParam().getCompileType().equals(NodeCompileTypeEnum.YARN.getCode())
					&& StringUtils.isBlank(addParam.getExtendNodeParam().getYarnVersion())) {
				throw new ApplicationException(MessageCodeEnum.INVALID_PARAM.getCode(), "Yarn版本不能为空");
			}
		}
	}
	
	private AppParam buildBizParam(AppCreationParam requestParam) {
		AppParam bizParam = new AppParam();
		BeanUtils.copyProperties(requestParam, bizParam);
		AppExtend appExtend = null;
		if(TechTypeEnum.SPRING_BOOT.getCode().equals(requestParam.getTechType())) {
			appExtend = new AppExtendJava();
			BeanUtils.copyProperties(requestParam.getExtendSpringBootParam(), appExtend);
		}else if(TechTypeEnum.VUE.getCode().equals(requestParam.getTechType())
				|| TechTypeEnum.REACT.getCode().equals(requestParam.getTechType())) {
			appExtend = new AppExtendNode();
			BeanUtils.copyProperties(requestParam.getExtendNodeParam(), appExtend);
		}else if(TechTypeEnum.NODEJS.getCode().equals(requestParam.getTechType())
				&& requestParam.getExtendNodejsParam() != null) {
			appExtend = new AppExtendNodejs();
			BeanUtils.copyProperties(requestParam.getExtendNodejsParam(), appExtend);
		}else if(TechTypeEnum.HTML.getCode().equals(requestParam.getTechType())
				&& requestParam.getExtendHtmlParam() != null) {
			appExtend = new AppExtendHtml();
			BeanUtils.copyProperties(requestParam.getExtendHtmlParam(), appExtend);
		}
		bizParam.setAppExtend(appExtend);
		return bizParam;
	}
}
