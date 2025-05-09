package kinman.dbee.infrastructure.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kinman.dbee.api.enums.AppMemberRoleTypeEnum;
import kinman.dbee.infrastructure.component.SpringBootApplicationHome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Constants {

	private static final Logger logger = LoggerFactory.getLogger(Constants.class);
	
	public static final String CRLF = "\r\n|\n|\r";
	
	public static final String GRADLE_VERSION = "gradle-8.2.1";
	
	public static final String MAVEN_VERSION = "apache-maven-3.9.4";
	
	public static final String GRADLE_FILE_URL = "http://file.512.team/list/" + GRADLE_VERSION + "-bin.zip";
	
	public static final String MAVEN_FILE_URL = "http://file.512.team/list/"+ MAVEN_VERSION +"-bin.tar.gz";
	
	public static final String GO_FILE_PRE_URL = "https://dl.google.com/go/";
	
	public static final String DOTNET_FILE_PRE_URL = "http://file.512.team/list/dotnet/";
	
	/**
	 * 24小时
	 */
	public static final long HOUR_24 = 24 * 60 * 60 * 1000;
	
	public static final long ONE_MB = 1024 * 1024;
	
	public static final String MB_UNIT = "MB";
	
	public static final int DAYS_14 = 14;
	
	public static final int DAYS_7 = 7;
	
	public static final int DAYS_3 = 3;
	
	public static final String USR_LOCAL_HOME = "/usr/local/";
	
	public static final String DBEE_TAG = "dbee";
	
	public static final String IMAGE_NAME_JDK = "jdk";
	
	public static final String IMAGE_NAME_TOMCAT = "tomcat";
	
	public static final String COLLECT_METRICS_URI = "/app/env/replica/metrics/add";
	
	public static final int METRICS_TABLE_SIZE = 32;
	
	public static final String BUSYBOX_IMAGE_URL = "registry.cn-hangzhou.aliyuncs.com/dbee/busybox:latest";
	
	public static final String CENTOS_IMAGE_URL = "dockerproxy.net/library/centos:latest";
	
	public static final String PYTHON_IMAGE_BASE_URL = "dockerproxy.net/library/python:";
	
	public static final String NODE_IMAGE_BASE_URL = "dockerproxy.net/library/node:";
	
	public static final String DOTNET_IMAGE_BASE_URL = "mcr.microsoft.com/dotnet/aspnet:";
	
	//格式：dockerproxy.net/library/nginx:1.23.3-alpine
	public static final String NGINX_IMAGE_URL = "dockerproxy.net/library/nginx:%s-alpine";
	
	public static final String LOCALHOST_IP = "127.0.0.1";
	
	public static final String NGINX = "nginx";
	
	/**
	 * 部署目录
	 */
	public static final String DBEE_HOME = new SpringBootApplicationHome(Constants.class)
			.getSource()
			.getParentFile()
			.getParentFile()
			.getParent();
	
	/**
	 * 默认数据路径
	 */
	public static final String DEFAULT_DATA_PATH = DBEE_HOME + "/data/";
	
	/**
	 * 默认日志路径
	 */
	public static final String DEFAULT_LOG_PATH = DBEE_HOME + "/log/";
	
	/**
	 * 配置文件路径
	 */
	public static final String CONF_PATH = DBEE_HOME + "/conf/";
	
	/**
	 * 临时目录相对路径
	 */
	public static final String RELATIVE_TMP_PATH = "tmp/";
	
	/**
	 * 可以操作应用成员的角色
	 */
	public static final List<Integer> ROLE_OF_OPERATE_APP_USER = Arrays.asList(
			AppMemberRoleTypeEnum.ADMIN.getCode(),
			AppMemberRoleTypeEnum.ARCHITECT.getCode(),
			AppMemberRoleTypeEnum.CHECKER.getCode());
	
	/**
	 * Nodejs类应用的编译文件
	 */
	public static final Set<String> NODE_APP_TARGET_FILE = new HashSet<>();
	static {
		NODE_APP_TARGET_FILE.add(".nuxt");
		NODE_APP_TARGET_FILE.add(".next");
		NODE_APP_TARGET_FILE.add("static");
		NODE_APP_TARGET_FILE.add("package.json");
		NODE_APP_TARGET_FILE.add("nuxt.config.js");
		NODE_APP_TARGET_FILE.add("next.config.js");
		NODE_APP_TARGET_FILE.add("node_modules");
	}
	
	/**
	 * 部署日志文件路径
	 */
	public static String publishedLogPath(String logPath, Date deplopyedStartTime) {
		return new StringBuilder()
    			.append(logPath)
    			.append("published/")
    			.append(new SimpleDateFormat(DateUtils.DATE_FORMAT_YYYYMMDD).format(deplopyedStartTime))
    			.append("/")
    			.toString();
	}
	
	/**
	 * 部署日志的文件路径
	 */
	public static String deploymentLogFile(String logPath, Date deplopyedStartTime, String fileName) {
		return new StringBuilder()
    			.append(publishedLogPath(logPath, deplopyedStartTime))
    			.append("deployment/")
    			.append(fileName)
    			.append(".log").toString();
	}
	
	/**
	 * 构建版本的日志文件路径
	 */
	public static String buildVersionLogFile(String logPath, Date deplopyedStartTime, String fileName) {
		return new StringBuilder()
    			.append(publishedLogPath(logPath, deplopyedStartTime))
    			.append("build/")
    			.append(fileName)
    			.append(".log").toString();
	}
	
	public static final String hostIp() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			logger.error("Failed to get localhost ip", e);
		}
		return null;
	}
	
	public static boolean isWindows() {
		return osName().indexOf("win") > -1;
	}
	
	public static boolean isMac() {
		return osName().indexOf("mac") > -1;
	}
	
	public static boolean isUnix() {
		return !isWindows() && !isMac();
	}
	
	public static String osName() {
		return System.getProperty("os.name").toLowerCase();
	}
}
