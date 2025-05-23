package kinman.dbee.application.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import kinman.dbee.infrastructure.strategy.cluster.k8s.K8sClusterStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import kinman.dbee.api.enums.ClusterTypeEnum;
import kinman.dbee.api.enums.GlobalConfigItemTypeEnum;
import kinman.dbee.api.enums.ImageRepoTypeEnum;
import kinman.dbee.api.enums.MessageCodeEnum;
import kinman.dbee.api.enums.RoleTypeEnum;
import kinman.dbee.api.enums.YesOrNoEnum;
import kinman.dbee.api.response.PageData;
import kinman.dbee.api.response.model.GlobalConfigAgg;
import kinman.dbee.api.response.model.GlobalConfigAgg.CAS;
import kinman.dbee.api.response.model.GlobalConfigAgg.DingDing;
import kinman.dbee.api.response.model.GlobalConfigAgg.EnvTemplate;
import kinman.dbee.api.response.model.GlobalConfigAgg.FeiShu;
import kinman.dbee.api.response.model.GlobalConfigAgg.ImageRepo;
import kinman.dbee.api.response.model.GlobalConfigAgg.Ldap;
import kinman.dbee.api.response.model.GlobalConfigAgg.TraceTemplate;
import kinman.dbee.api.response.model.GlobalConfigAgg.WeChat;
import kinman.dbee.infrastructure.component.ComponentConstants;
import kinman.dbee.infrastructure.exception.ApplicationException;
import kinman.dbee.infrastructure.param.AppMemberParam;
import kinman.dbee.infrastructure.param.GlobalConfigParam;
import kinman.dbee.infrastructure.param.GlobalConfigQueryParam;
import kinman.dbee.infrastructure.repository.AffinityTolerationRepository;
import kinman.dbee.infrastructure.repository.AppEnvRepository;
import kinman.dbee.infrastructure.repository.AppMemberRepository;
import kinman.dbee.infrastructure.repository.AppRepository;
import kinman.dbee.infrastructure.repository.ClusterRepository;
import kinman.dbee.infrastructure.repository.DeploymentDetailRepository;
import kinman.dbee.infrastructure.repository.DeploymentVersionRepository;
import kinman.dbee.infrastructure.repository.EnvExtRepository;
import kinman.dbee.infrastructure.repository.GlobalConfigRepository;
import kinman.dbee.infrastructure.repository.MetricsRepository;
import kinman.dbee.infrastructure.repository.SysUserRepository;
import kinman.dbee.infrastructure.repository.po.AppMemberPO;
import kinman.dbee.infrastructure.repository.po.AppPO;
import kinman.dbee.infrastructure.repository.po.BasePO;
import kinman.dbee.infrastructure.repository.po.ClusterPO;
import kinman.dbee.infrastructure.strategy.cluster.ClusterStrategy;
import kinman.dbee.infrastructure.strategy.login.dto.LoginUser;
import kinman.dbee.infrastructure.utils.Constants;
import kinman.dbee.infrastructure.utils.DeploymentContext;
import kinman.dbee.infrastructure.utils.FileUtils;
import kinman.dbee.infrastructure.utils.HttpUtils;
import kinman.dbee.infrastructure.utils.LogUtils;
import kinman.dbee.infrastructure.utils.StringUtils;
import kinman.dbee.infrastructure.utils.ThreadPoolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.cloud.tools.jib.api.Containerizer;
import com.google.cloud.tools.jib.api.Jib;
import com.google.cloud.tools.jib.api.LogEvent;
import com.google.cloud.tools.jib.api.RegistryImage;
import com.google.cloud.tools.jib.api.buildplan.AbsoluteUnixPath;

/**
 * 
 * 应用层基础服务
 */
public abstract class ApplicationService {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);
	
	@Autowired
	protected SysUserRepository sysUserRepository;
	
	@Autowired
	protected GlobalConfigRepository globalConfigRepository;
	
	@Autowired
	protected ClusterRepository clusterRepository;
	
	@Autowired
	protected AppRepository appRepository;
	
	@Autowired
	protected AppMemberRepository appMemberRepository;

	@Autowired
	protected AppEnvRepository appEnvRepository;
	
	@Autowired
	protected EnvExtRepository envExtRepository;
	
	@Autowired
	protected AffinityTolerationRepository affinityTolerationRepository;
	
	@Autowired
	protected DeploymentDetailRepository deploymentDetailRepository;
	
	@Autowired
	protected DeploymentVersionRepository deploymentVersionRepository;
	
	@Autowired
	protected ComponentConstants componentConstants;
	
	@Autowired
	protected MetricsRepository metricsRepository;
	
	public GlobalConfigAgg globalConfig() {
		GlobalConfigParam param = new GlobalConfigParam();
		param.setItemTypes(Arrays.asList(
				GlobalConfigItemTypeEnum.CODE_REPO.getCode(),
				GlobalConfigItemTypeEnum.IMAGE_REPO.getCode(),
				GlobalConfigItemTypeEnum.MAVEN.getCode(),
				GlobalConfigItemTypeEnum.TRACE_TEMPLATE.getCode(),
				GlobalConfigItemTypeEnum.MORE.getCode()));
		return globalConfigRepository.queryAgg(param);
	}
	
	public GlobalConfigAgg globalConfig(GlobalConfigQueryParam queryParam) {
		GlobalConfigParam configParam = new GlobalConfigParam();
		configParam.setId(queryParam.getGlobalConfigId());
		configParam.setItemTypes(queryParam.getItemTypes());
		return globalConfigRepository.queryAgg(configParam);
	}
	
	public GlobalConfigAgg envTemplateQuery(GlobalConfigQueryParam queryParam) {
		GlobalConfigAgg globalConfigAgg = globalConfig(queryParam);
		EnvTemplate template = globalConfigAgg.getEnvTemplate();
		if(template == null) {
			return globalConfigAgg;
		}
		if(!YesOrNoEnum.YES.getCode().equals(template.getTraceStatus())) {
			return globalConfigAgg;
		}
		TraceTemplate traceTemplate = globalConfig().getTraceTemplate(template.getTraceTemplateId());
		if(traceTemplate != null) {
			template.setTraceTemplateName(traceTemplate.getName());
		}
		return globalConfigAgg;
	}
	
	protected String mavenRepo() {
		return componentConstants.getDataPath() + "maven/repository/";
	}
	
	public AppPO validateApp(String appId) {
		AppPO appPO = appRepository.queryById(appId);
		if(appPO == null) {
			LogUtils.throwException(logger, MessageCodeEnum.APP_IS_NONEXISTENCE);
		}
		return appPO;
	}
	
	protected ClusterStrategy clusterStrategy(Integer clusterType) {
		if (ClusterTypeEnum.K8S.getCode().equals(clusterType)) {
			if(componentConstants.getKubernetesClient().equals("fabric8")) {
				return new K8sClusterStrategy();
			}
		}
		return new K8sClusterStrategy();
	}
	
	protected void hasRights(LoginUser loginUser, String appId) {
		if(RoleTypeEnum.ADMIN.getCode().equals(loginUser.getRoleType())){
			return;
		}
		AppMemberParam appMemberParam = new AppMemberParam();
		appMemberParam.setAppId(appId);
		appMemberParam.setUserId(loginUser.getId());
		AppMemberPO appMemberPO = appMemberRepository.query(appMemberParam);
		if(Objects.isNull(appMemberPO)) {
			LogUtils.throwException(logger, MessageCodeEnum.NO_ACCESS_RIGHT);
		}
	}
	
	protected void hasAdminRights(LoginUser loginUser, String appId) {
		if(RoleTypeEnum.ADMIN.getCode().equals(loginUser.getRoleType())){
			return;
		}
		AppMemberParam appMemberParam = new AppMemberParam();
		appMemberParam.setAppId(appId);
		appMemberParam.setUserId(loginUser.getId());
		AppMemberPO appMemberPO = appMemberRepository.query(appMemberParam);
		if(Objects.isNull(appMemberPO)) {
			LogUtils.throwException(logger, MessageCodeEnum.NO_ACCESS_RIGHT);
		}
		String[] roleTypes = appMemberPO.getRoleType().split(",");
		Set<Integer> roleSet = new HashSet<>();
		for (String role : roleTypes) {
			roleSet.add(Integer.valueOf(role));
		}
		List<Integer> adminRole = Constants.ROLE_OF_OPERATE_APP_USER.stream()
				.filter(item -> roleSet.contains(item))
				.collect(Collectors.toList());
		if(adminRole.size() <= 0) {
			LogUtils.throwException(logger, MessageCodeEnum.NO_ACCESS_RIGHT);
		}
	}
	
	protected String fullNameOfTraceAgentImage(DeploymentContext context) {
		if(!YesOrNoEnum.YES.getCode().equals(context.getAppEnv().getTraceStatus())) {
			return null;
		}
		TraceTemplate traceTemplate = context.getGlobalConfigAgg().getTraceTemplate(context.getAppEnv().getTraceTemplateId());
		if(!StringUtils.isBlank(traceTemplate.getAgentImage())) {
			return traceTemplate.getAgentImage();
		}
		String imageName = "skywalking-agent:v" + traceTemplate.getAgentVersion();
		return fullNameOfImage(context.getGlobalConfigAgg().getImageRepo(), imageName);
	}
	
	protected String fullNameOfDbeeAgentImage(ImageRepo imageRepo) {
		String imageName = "dbee-agent:" + componentConstants.getVersion();
		return fullNameOfImage(imageRepo, imageName);
	}
	
	protected String fullNameOfImage(ImageRepo imageRepo, String nameOfImage) {
		if(imageRepo == null) {
			LogUtils.throwException(logger, MessageCodeEnum.IMAGE_REPO_IS_EMPTY);
		}
		String imgUrl = imageRepo.getUrl();
		if(imgUrl.startsWith("http")) {
			imgUrl = imgUrl.substring(imgUrl.indexOf("//") + 2);
		}
		StringBuilder fullNameOfImage = new StringBuilder(imgUrl);
		if(!imgUrl.endsWith("/")) {
			fullNameOfImage.append("/");
		}
		if(ImageRepoTypeEnum.DOCKERHUB.getValue().equals(imageRepo.getType())) {
			fullNameOfImage.append(imageRepo.getAuthName());
		}else {
			fullNameOfImage.append(Constants.DBEE_TAG);
		}
		fullNameOfImage.append("/").append(nameOfImage);
		return fullNameOfImage.toString();
	}
	
	protected String imageNameOfJdk(String baseImageVersion) {
		return Constants.IMAGE_NAME_JDK + ":" + tagOfJdk(baseImageVersion);
	}
	
	protected String tagOfJdk(String version) {
		return "v" + version;
	}
	
	public String queryJavaMajorVersion(String javaHome){
		List<String> javaVersions = queryJavaVersion(javaHome);
		String[] versions = javaVersions.get(0).split("\\.");
		if(Integer.valueOf(versions[0]) < 9) {
			return versions[0] + "." + versions[1];
		}
		return versions[0];
	}
	
	public List<String> queryJavaVersion(){
		//todo
		return queryJavaVersion("xxx");
	}
	
	public List<String> queryJavaVersion(String javaHome){
		if(StringUtils.isBlank(javaHome)) {
			return null;
		}
		
		List<String> javaVersions = new ArrayList<>();
		
		//如果没有配置Maven的Java安装目录，则取Dbee所在的Java版本
		if(StringUtils.isBlank(javaHome)){
			javaVersions.add(System.getProperty("java.version"));
			return javaVersions;
		}
		
		//从指定的Java安装目录中获取版本
		if(!javaHome.endsWith("/")) {
			javaHome = javaHome + "/";
		}
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(new String[]{javaHome + "bin/java", "-version"});
		} catch (IOException e) {
			e.printStackTrace();
		}
		try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()))){
			String versionStr = br.readLine().split("\\s+")[2].replace("\"", "");
			javaVersions.add(versionStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(process != null) {
			process.destroy();
		}
		return javaVersions;
	}
	
	protected void doNotify(String url, String reponse) {
		if(StringUtils.isBlank(url)){
			return;
		}
		
		int httpCode = 0;
		for(int i = 0; i < 5; i++) {
			httpCode = HttpUtils.post(url, reponse);
			if(httpCode == 200) {
				logger.info("Successfully to noifty url: {}", url);
				break;
			}
			logger.error("Failed to notify url: {}, http code: {}", url, httpCode);
		}
	}
	
	public void asynInitConfig() {
		ThreadPoolUtils.async(() -> {
			
			//随机休眠几秒：
			//集群部署时，服务并行启动可能带来的并发操作问题
			int secods = new Random().nextInt(10) + 1;
			try {
				Thread.sleep(secods * 1000);
			} catch (InterruptedException e) {
				//ignore
			}
			
			try {
				createDbeeConfig();
			} catch (Exception e) {
				logger.error("Failed to create dbee config", e);
			}
			
			try {
				createSecret();
			} catch (Exception e) {
				logger.error("Failed to create secret", e);
			}
			
			try {
				creatImageRepoProject();
			} catch (Exception e) {
				logger.error("Failed to creat image repo project", e);
			}
			
			try {
				buildDbeeAgentImage();
			} catch (Exception e) {
				logger.error("Failed to build dbee agent image", e);
			}
			
			try {
				downloadGradle();
			} catch (Exception e) {
				logger.error("Failed to download gradle", e);
			}
			
			try {
				downloadMaven();
			} catch (Exception e) {
				logger.error("Failed to download maven", e);
			}
			
		});
	}
	
	private void creatImageRepoProject() {
		GlobalConfigParam globalConfigParam = new GlobalConfigParam();
		globalConfigParam.setItemType(GlobalConfigItemTypeEnum.IMAGE_REPO.getCode());
		GlobalConfigAgg globalConfigAgg = globalConfigRepository.queryAgg(globalConfigParam);
		ImageRepo imageRepo = globalConfigAgg.getImageRepo();
		if(imageRepo == null) {
			return;
		}
		if(ImageRepoTypeEnum.HARBOR.getValue().equals(imageRepo.getType())) {
			try {
				createProject(imageRepo, false);
			}catch(ApplicationException e) {
				//这里为了兼容Harbor2.0接口参数类型的不同，再次调用
				createProject(imageRepo, 0);
			}
		}
	}
	
	//写入容器集群的dbee服务地址
	private void createDbeeConfig() {
		List<ClusterPO> clusters = clusterRepository.listAll();
		if(CollectionUtils.isEmpty(clusters)) {
			return;
		}
		for(ClusterPO c : clusters) {
			ClusterStrategy cluster = clusterStrategy(c.getClusterType());
			cluster.createDbeeConfig(c);
		}
	}
	
	//删除容器集群的dbee服务地址
	public void deleteDbeeConfig() {
		logger.info("Start to delete dbee config");
		List<ClusterPO> clusters = clusterRepository.listAll();
		if(CollectionUtils.isEmpty(clusters)) {
			return;
		}
		for(ClusterPO c : clusters) {
			ClusterStrategy cluster = clusterStrategy(c.getClusterType());
			cluster.deleteDbeeConfig(c);
		}
		logger.info("End to delete dbee config");
	}
	
	//创建镜像仓库认证key
	private void createSecret() {
		List<ClusterPO> clusters = clusterRepository.listAll();
		if(CollectionUtils.isEmpty(clusters)) {
			return;
		}
		GlobalConfigParam globalConfigParam = new GlobalConfigParam();
		globalConfigParam.setItemType(GlobalConfigItemTypeEnum.IMAGE_REPO.getCode());
		GlobalConfigAgg globalConfigAgg = globalConfigRepository.queryAgg(globalConfigParam);
		if(globalConfigAgg == null || globalConfigAgg.getImageRepo() == null) {
			return;
		}
		for(ClusterPO c : clusters) {
			ClusterStrategy cluster = clusterStrategy(c.getClusterType());
			cluster.createSecret(c, globalConfigAgg.getImageRepo());
		}
	}
	
	protected void createProject(ImageRepo imageRepo, Object publicType) {
        String uri = "api/v2.0/projects";
        if(!imageRepo.getUrl().endsWith("/")) {
        	uri = "/" + uri;
        }
        HttpPost httpPost = new HttpPost(imageRepo.getUrl() + uri);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(5000)
                .setConnectTimeout(5000)
                .setSocketTimeout(5000)
                .build();
        httpPost.setConfig(requestConfig);
        httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
        httpPost.setHeader("Authorization", "Basic "+ Base64.getUrlEncoder().encodeToString((imageRepo.getAuthName() + ":" + imageRepo.getAuthPassword()).getBytes()));
        String param = "{\"project_name\": \"dbee\", \"public\": " + publicType + "}";
        httpPost.setEntity(new StringEntity(param, "UTF-8"));
        try (CloseableHttpResponse response = HttpUtils.createHttpClient(imageRepo.getUrl()).execute(httpPost)){
            if (response.getStatusLine().getStatusCode() != 201
            		&& response.getStatusLine().getStatusCode() != 409) {
            	LogUtils.throwException(logger, response.getStatusLine().getReasonPhrase(),
            			MessageCodeEnum.IMAGE_REPO_PROJECT_FAILURE);
            }
        } catch (IOException e) {
        	LogUtils.throwException(logger, e, MessageCodeEnum.IMAGE_REPO_PROJECT_FAILURE);
        }
	}
	
	protected void buildDbeeAgentImage() {
		GlobalConfigParam globalConfigParam = new GlobalConfigParam();
		globalConfigParam.setItemType(GlobalConfigItemTypeEnum.IMAGE_REPO.getCode());
		GlobalConfigAgg globalConfigAgg = globalConfigRepository.queryAgg(globalConfigParam);
		if(globalConfigAgg == null || globalConfigAgg.getImageRepo() == null) {
			logger.info("The image repo does not exist, and end to build dbee agent image");
			return;
		}
		buildDbeeAgentImage(globalConfigAgg.getImageRepo());
	}
	
	protected void buildDbeeAgentImage(ImageRepo imageRepo) {
		logger.info("Start to build jvm metrics agent image");
		
		//Jib环境变量
		jibProperty();
		
		String javaAgentPath = Constants.DBEE_HOME + "/lib/ext/dbee-agent-"+ componentConstants.getVersion() +".jar";
		if(!new File(javaAgentPath).exists()) {
			javaAgentPath = Constants.DBEE_HOME + "/dbee-agent/target/dbee-agent-"+ componentConstants.getVersion() +".jar";
		}
		if(!new File(javaAgentPath).exists()) {
			logger.error("The agent file does not exist, end to build dbee agent image");
			return;
		}
		try {
			RegistryImage registryImage = RegistryImage.named(fullNameOfDbeeAgentImage(imageRepo)).addCredential(
					imageRepo.getAuthName(),
					imageRepo.getAuthPassword());
			Jib.from(Constants.BUSYBOX_IMAGE_URL)
				.addLayer(Arrays.asList(Paths.get(javaAgentPath)), AbsoluteUnixPath.get(Constants.USR_LOCAL_HOME))
				.containerize(Containerizer.to(registryImage)
						.setAllowInsecureRegistries(true)
						.addEventHandler(LogEvent.class, logEvent -> logger.info(logEvent.getMessage())));
		} catch (Exception e) {
			logger.error("Failed to build dbee agent image", e);
		}
		logger.info("End to build dbee agent image");
	}
	
	protected void downloadGradle() {
		String gradlePathName = componentConstants.getDataPath() + "gradle/";
		File gradlePath = new File(gradlePathName);
		if(!gradlePath.exists()) {
			if(gradlePath.mkdirs()) {
				logger.info("Create gradle path successfully");
			}else {
				logger.warn("Failed to create gradle path");
			}
		}
		
		File[] files = gradlePath.listFiles();
		for(File f : files) {
			if(f.getName().equals(Constants.GRADLE_VERSION)) {
				logger.info("Gradle home is {}", f.getAbsolutePath());
				return;
			}
		}
		
		File targetFile = new File(gradlePathName + "gradle.zip");
		FileUtils.downloadFile(Constants.GRADLE_FILE_URL, targetFile);
		FileUtils.unZip(targetFile.getAbsolutePath(), gradlePathName);
		targetFile.delete();
	}
	
	protected void downloadMaven() {
		String rootPath = componentConstants.getDataPath() + "maven/";
		File rootPathFile = new File(rootPath);
		if(!rootPathFile.exists()) {
			if(rootPathFile.mkdirs()) {
				logger.info("Create maven path successfully");
			}else {
				logger.warn("Failed to create maven path");
			}
		}
		
		File[] files = rootPathFile.listFiles();
		for(File f : files) {
			if(f.getName().equals(Constants.MAVEN_VERSION)) {
				logger.info("Maven home is {}", f.getAbsolutePath());
				return;
			}
		}
		
		File targetFile = new File(rootPath + "maven.zip");
		FileUtils.downloadFile(Constants.MAVEN_FILE_URL, targetFile);
		FileUtils.unTarGz(targetFile.getAbsolutePath(), rootPath);
		targetFile.delete();
	}
	
	protected String downloadGo(String version) {
		String goPath = "go-" + version;
		String rootPath = componentConstants.getDataPath() + "go/";
		File rootPathFile = new File(rootPath);
		if(!rootPathFile.exists()) {
			if(rootPathFile.mkdirs()) {
				logger.info("Create go path successfully");
			}else {
				logger.warn("Failed to create go path");
			}
		}
		
		String goHome = rootPath + goPath + "/";
		File[] files = rootPathFile.listFiles();
		for(File f : files) {
			if(f.getName().equals(goPath)) {
				logger.info("Go home is {}", f.getAbsolutePath());
				return goHome;
			}
		}
		
		String suffixName = ".linux-amd64.tar.gz";
		if(Constants.isWindows()) {
			suffixName = ".windows-amd64.zip";
		}else if(Constants.isMac()) {
			suffixName = ".darwin-amd64.tar.gz";
		}
		//fileName格式：go1.21.0.linux-amd64.tar.gz
		String fileName = "go" + version + suffixName;
		File targetFile = new File(rootPath + fileName);
		FileUtils.downloadFile(Constants.GO_FILE_PRE_URL + fileName, targetFile);
		String destPath = null;
		if(Constants.isWindows()) {
			destPath = FileUtils.unZip(targetFile.getAbsolutePath(), rootPath);
		}else {
			destPath = FileUtils.unTarGz(targetFile.getAbsolutePath(), rootPath);
		}
		new File(destPath).renameTo(new File(rootPath + goPath));
		targetFile.delete();
		return goHome;
	}
	
	protected boolean execCommand(String cmd) {
		return execCommand(null, cmd);
	}
	
	protected boolean execCommand(Map<String, String> env, String cmd) {
		List<String> cmds = systemCmd();
		cmds.add(cmd.toString());
        ProcessBuilder pb = new ProcessBuilder();
        if(env != null) {
        	pb.environment().putAll(env);
        }
        //将标准输入流和错误输入流合并，通过标准输入流读取信息
        pb.redirectErrorStream(true);
        pb.command(cmds);

        Process p = null;
        try {
            p = pb.start();
        }catch (IOException e) {
            logger.error("Failed to start proccss", e);
        }

        try(BufferedReader in = new BufferedReader(new InputStreamReader(
        		p.getInputStream(), Charset.defaultCharset()))){
            String line = null;
            while ((line = in.readLine()) != null) {
                logger.info(line);
            }
            p.waitFor();
            return p.exitValue() == 0;
        }catch (Exception e) {
        	logger.error("Failed read proccss message", e);
        	LogUtils.throwException(logger, MessageCodeEnum.PACK_FAILURE);
        }finally {
        	if(p != null) {
        		p.destroy();
        	}
        }
        return false;
    }
	
	private List<String> systemCmd(){
		List<String> cmd = new ArrayList<>();
		if(Constants.isWindows()) {
			cmd.add("cmd");
			cmd.add("/c");
		}else {
			cmd.add("/bin/sh");
			cmd.add("-c");
		}
		return cmd;
	}

	protected String javaHome(String customizedHome) {
		//优先使用指定的javaHome
		if(!StringUtils.isBlank(customizedHome)) {
			return customizedHome;
		}
		return System.getenv("JAVA_HOME");
	}
	
	protected void jibProperty() {
		System.setProperty("jib.httpTimeout", "600000");
		System.setProperty("sendCredentialsOverHttp", "true");
	}
	
	protected <D> PageData<D> zeroPageData(int pageSize) {
		PageData<D> pageData = new PageData<>();
		pageData.setPageNum(1);
		pageData.setPageCount(0);
		pageData.setPageSize(pageSize);
		pageData.setItemCount(0);
		pageData.setItems(null);
		return pageData;
	}
	
	protected <D> PageData<D> pageData(IPage<? extends BasePO> pagePO, List<D> items) {
		PageData<D> pageData = new PageData<>();
		pageData.setPageNum((int)pagePO.getCurrent());
		pageData.setPageCount((int)pagePO.getPages());
		pageData.setPageSize((int)pagePO.getSize());
		pageData.setItemCount((int)pagePO.getTotal());
		pageData.setItems(items);
		return pageData;
	}
	
	protected void randomSleep() {
		try {
			Thread.sleep(new Random().nextInt(100));
		} catch (InterruptedException e) {
			//ignore
		}
	}
}
