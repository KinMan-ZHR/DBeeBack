package kinman.dbee.api.param.app;

import java.io.Serializable;
import java.util.List;

/**
 * 新增应用参数模型。
 */
public class AppCreationParam implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 应用名称
	 */
	private String appName;

	/**
	 * 技术类型，1：SpringBoot，2：Node
	 */
	private Integer techType;

	/**
	 * 基础镜像来源，1：版本号，2：自定义
	 */
	private Integer baseImageSource;

	/**
	 * 基础镜像版本
	 */
	private String baseImageVersion;

	/**
	 * 基础镜像，如：openjdk:11.0.16-jdk
	 */
	private String baseImage;

	/**
	 * 代码仓库地址
	 */
	private String codeRepoPath;

	/**
	 * 亲密应用名称
	 */
	private List<String> affinityAppNames;

	/**
	 * 一级部门
	 */
	private String firstDepartment;

	/**
	 * 二级部门
	 */
	private String secondDepartment;

	/**
	 * 三级部门
	 */
	private String thirdDepartment;

	/**
	 * 应用描述
	 */
	private String description;

	/**
	 * SpringBoot应用扩展参数
	 */
	private AppExtendSpringBootCreationParam extendSpringBootParam;

	/**
	 * Node应用扩展参数
	 */
	private AppExtendNodeCreationParam extendNodeParam;

	/**
	 * Nodejs应用扩展扩展参数
	 */
	private AppExtendNodejsCreationParam extendNodejsParam;

	/**
	 * Html应用扩展扩展参数
	 */
	private AppExtendHtmlCreationParam extendHtmlParam;

	/**
	 * Go应用扩展扩展参数
	 */
	private AppExtendGoCreationParam extendGoParam;

	/**
	 * Python应用扩展扩展参数
	 */
	private AppExtendPythonCreationParam extendPythonParam;

	/**
	 * Flask应用扩展扩展参数
	 */
	private AppExtendFlaskCreationParam extendFlaskParam;

	/**
	 * Django应用扩展扩展参数
	 */
	private AppExtendDjangoCreationParam extendDjangoParam;

	/**
	 * Nuxt应用扩展扩展参数
	 */
	private AppExtendNuxtCreationParam extendNuxtParam;

	/**
	 * Next应用扩展扩展参数
	 */
	private AppExtendNextCreationParam extendNextParam;

	/**
	 * .NET应用扩展扩展参数
	 */
	private AppExtendDotNetCreationParam extendDotNetParam;

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public Integer getTechType() {
		return techType;
	}

	public void setTechType(Integer techType) {
		this.techType = techType;
	}

	public String getBaseImageVersion() {
		return baseImageVersion;
	}

	public void setBaseImageVersion(String baseImageVersion) {
		this.baseImageVersion = baseImageVersion;
	}

	public Integer getBaseImageSource() {
		return baseImageSource;
	}

	public void setBaseImageSource(Integer baseImageSource) {
		this.baseImageSource = baseImageSource;
	}

	public String getBaseImage() {
		return baseImage;
	}

	public void setBaseImage(String baseImage) {
		this.baseImage = baseImage;
	}

	public String getCodeRepoPath() {
		return codeRepoPath;
	}

	public void setCodeRepoPath(String codeRepoPath) {
		this.codeRepoPath = codeRepoPath;
	}

	public List<String> getAffinityAppNames() {
		return affinityAppNames;
	}

	public void setAffinityAppNames(List<String> affinityAppNames) {
		this.affinityAppNames = affinityAppNames;
	}

	public String getFirstDepartment() {
		return firstDepartment;
	}

	public void setFirstDepartment(String firstDepartment) {
		this.firstDepartment = firstDepartment;
	}

	public String getSecondDepartment() {
		return secondDepartment;
	}

	public void setSecondDepartment(String secondDepartment) {
		this.secondDepartment = secondDepartment;
	}

	public String getThirdDepartment() {
		return thirdDepartment;
	}

	public void setThirdDepartment(String thirdDepartment) {
		this.thirdDepartment = thirdDepartment;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public AppExtendSpringBootCreationParam getExtendSpringBootParam() {
		return extendSpringBootParam;
	}

	public void setExtendSpringBootParam(AppExtendSpringBootCreationParam extendSpringBootParam) {
		this.extendSpringBootParam = extendSpringBootParam;
	}

	public AppExtendNodeCreationParam getExtendNodeParam() {
		return extendNodeParam;
	}

	public void setExtendNodeParam(AppExtendNodeCreationParam extendNodeParam) {
		this.extendNodeParam = extendNodeParam;
	}

	public AppExtendNodejsCreationParam getExtendNodejsParam() {
		return extendNodejsParam;
	}

	public void setExtendNodejsParam(AppExtendNodejsCreationParam extendNodejsParam) {
		this.extendNodejsParam = extendNodejsParam;
	}

	public AppExtendHtmlCreationParam getExtendHtmlParam() {
		return extendHtmlParam;
	}

	public void setExtendHtmlParam(AppExtendHtmlCreationParam extendHtmlParam) {
		this.extendHtmlParam = extendHtmlParam;
	}

	public AppExtendGoCreationParam getExtendGoParam() {
		return extendGoParam;
	}

	public void setExtendGoParam(AppExtendGoCreationParam extendGoParam) {
		this.extendGoParam = extendGoParam;
	}

	public AppExtendFlaskCreationParam getExtendFlaskParam() {
		return extendFlaskParam;
	}

	public void setExtendFlaskParam(AppExtendFlaskCreationParam extendFlaskParam) {
		this.extendFlaskParam = extendFlaskParam;
	}

	public AppExtendDjangoCreationParam getExtendDjangoParam() {
		return extendDjangoParam;
	}

	public void setExtendDjangoParam(AppExtendDjangoCreationParam extendDjangoParam) {
		this.extendDjangoParam = extendDjangoParam;
	}

	public AppExtendPythonCreationParam getExtendPythonParam() {
		return extendPythonParam;
	}

	public void setExtendPythonParam(AppExtendPythonCreationParam extendPythonParam) {
		this.extendPythonParam = extendPythonParam;
	}

	public AppExtendNuxtCreationParam getExtendNuxtParam() {
		return extendNuxtParam;
	}

	public void setExtendNuxtParam(AppExtendNuxtCreationParam extendNuxtParam) {
		this.extendNuxtParam = extendNuxtParam;
	}

	public AppExtendNextCreationParam getExtendNextParam() {
		return extendNextParam;
	}

	public void setExtendNextParam(AppExtendNextCreationParam extendNextParam) {
		this.extendNextParam = extendNextParam;
	}

	public AppExtendDotNetCreationParam getExtendDotNetParam() {
		return extendDotNetParam;
	}

	public void setExtendDotNetParam(AppExtendDotNetCreationParam extendDotNetParam) {
		this.extendDotNetParam = extendDotNetParam;
	}

	/**
	 * SpringBoot应用扩展参数模型
	 * 
	 * @author Dahai 2021-09-08
	 */
	public static class AppExtendSpringBootCreationParam implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * 应用编号
		 */
		private String appId;

		/**
		 * 构建方式，1：maven，2：gradle
		 */
		private Integer packageBuildType;

		/**
		 * 文件类型，1：jar，2：war，3：zip
		 */
		private Integer packageFileType;

		/**
		 * 打包文件路径
		 */
		private String packageTargetPath;

		/**
		 * Java安装目录
		 */
		private String javaHome;

		public String getAppId() {
			return appId;
		}

		public void setAppId(String appId) {
			this.appId = appId;
		}

		public Integer getPackageBuildType() {
			return packageBuildType;
		}

		public void setPackageBuildType(Integer packageBuildType) {
			this.packageBuildType = packageBuildType;
		}

		public Integer getPackageFileType() {
			return packageFileType;
		}

		public void setPackageFileType(Integer packageFileType) {
			this.packageFileType = packageFileType;
		}

		public String getPackageTargetPath() {
			return packageTargetPath;
		}

		public void setPackageTargetPath(String packageTargetPath) {
			this.packageTargetPath = packageTargetPath;
		}

		public String getJavaHome() {
			return javaHome;
		}

		public void setJavaHome(String javaHome) {
			this.javaHome = javaHome;
		}

	}

	/**
	 * Node应用扩展参数模型
	 * 
	 * @author Dahai 2022-12-08
	 */
	public static class AppExtendNodeCreationParam implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * Node版本
		 */
		private String nodeVersion;

		/**
		 * npm版本
		 */
		private String npmVersion;

		/**
		 * pnpm版本
		 */
		private String pnpmVersion;

		/**
		 * yarn版本
		 */
		private String yarnVersion;

		/**
		 * 编译方式
		 */
		private Integer compileType;

		/**
		 * 打包文件路径
		 */
		private String packageTargetPath;

		public String getNodeVersion() {
			return nodeVersion;
		}

		public void setNodeVersion(String nodeVersion) {
			this.nodeVersion = nodeVersion;
		}

		public String getNpmVersion() {
			return npmVersion;
		}

		public void setNpmVersion(String npmVersion) {
			this.npmVersion = npmVersion;
		}

		public String getPackageTargetPath() {
			return packageTargetPath;
		}

		public void setPackageTargetPath(String packageTargetPath) {
			this.packageTargetPath = packageTargetPath;
		}

		public String getPnpmVersion() {
			return pnpmVersion;
		}

		public void setPnpmVersion(String pnpmVersion) {
			this.pnpmVersion = pnpmVersion;
		}

		public Integer getCompileType() {
			return compileType;
		}

		public void setCompileType(Integer compileType) {
			this.compileType = compileType;
		}

		public String getYarnVersion() {
			return yarnVersion;
		}

		public void setYarnVersion(String yarnVersion) {
			this.yarnVersion = yarnVersion;
		}
	}

	/**
	 * Nodejs应用扩展参数模型
	 * 
	 * @author 无双
	 */
	public static class AppExtendNodejsCreationParam implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * Node版本
		 */
		private String nodeVersion;

		/**
		 * Node镜像
		 */
		private String nodeImage;

		/**
		 * 启动文件
		 */
		private String startFile;

		public String getNodeVersion() {
			return nodeVersion;
		}

		public void setNodeVersion(String nodeVersion) {
			this.nodeVersion = nodeVersion;
		}

		public String getNodeImage() {
			return nodeImage;
		}

		public void setNodeImage(String nodeImage) {
			this.nodeImage = nodeImage;
		}

		public String getStartFile() {
			return startFile;
		}

		public void setStartFile(String startFile) {
			this.startFile = startFile;
		}

	}

	/**
	 * Html应用扩展参数模型
	 * 
	 * @author 无双
	 */
	public static class AppExtendHtmlCreationParam implements Serializable {

		private static final long serialVersionUID = 1L;

	}

	/**
	 * Go应用扩展参数模型
	 * 
	 * @author Dahai
	 */
	public static class AppExtendGoCreationParam implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * Go版本
		 */
		private String goVersion;

		public String getGoVersion() {
			return goVersion;
		}

		public void setGoVersion(String goVersion) {
			this.goVersion = goVersion;
		}

	}

	/**
	 * Python应用扩展参数模型
	 * 
	 * @author Dahai
	 */
	public static class AppExtendPythonCreationParam implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * Python版本
		 */
		private String pythonVersion;

		/**
		 * Python镜像
		 */
		private String pythonImage;

		/**
		 * 启动文件
		 */
		private String startFile;

		public String getPythonVersion() {
			return pythonVersion;
		}

		public void setPythonVersion(String pythonVersion) {
			this.pythonVersion = pythonVersion;
		}

		public String getPythonImage() {
			return pythonImage;
		}

		public void setPythonImage(String pythonImage) {
			this.pythonImage = pythonImage;
		}

		public String getStartFile() {
			return startFile;
		}

		public void setStartFile(String startFile) {
			this.startFile = startFile;
		}

	}

	/**
	 * Flask应用扩展参数模型
	 * 
	 * @author Dahai
	 */
	public static class AppExtendFlaskCreationParam extends AppExtendPythonCreationParam {

		private static final long serialVersionUID = 1L;

	}

	/**
	 * Django应用扩展参数模型
	 * 
	 * @author Dahai
	 */
	public static class AppExtendDjangoCreationParam extends AppExtendPythonCreationParam {

		private static final long serialVersionUID = 1L;

	}

	/**
	 * Nuxt应用扩展参数模型
	 * 
	 */
	public static class AppExtendNuxtCreationParam implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * Node版本
		 */
		private String nodeVersion;

		/**
		 * Node镜像
		 */
		private String nodeImage;

		/**
		 * Npm版本
		 */
		private String npmVersion;

		/**
		 * Pnpm版本
		 */
		private String pnpmVersion;

		/**
		 * Yarn版本
		 */
		private String yarnVersion;

		/**
		 * 编译方式
		 */
		private Integer compileType;

		/**
		 * 部署方式，1：动态部署，2：静态部署
		 */
		private Integer deploymentType;

		public String getNodeVersion() {
			return nodeVersion;
		}

		public void setNodeVersion(String nodeVersion) {
			this.nodeVersion = nodeVersion;
		}

		public String getNodeImage() {
			return nodeImage;
		}

		public void setNodeImage(String nodeImage) {
			this.nodeImage = nodeImage;
		}

		public String getNpmVersion() {
			return npmVersion;
		}

		public void setNpmVersion(String npmVersion) {
			this.npmVersion = npmVersion;
		}

		public String getPnpmVersion() {
			return pnpmVersion;
		}

		public void setPnpmVersion(String pnpmVersion) {
			this.pnpmVersion = pnpmVersion;
		}

		public Integer getCompileType() {
			return compileType;
		}

		public void setCompileType(Integer compileType) {
			this.compileType = compileType;
		}

		public String getYarnVersion() {
			return yarnVersion;
		}

		public void setYarnVersion(String yarnVersion) {
			this.yarnVersion = yarnVersion;
		}

		public Integer getDeploymentType() {
			return deploymentType;
		}

		public void setDeploymentType(Integer deploymentType) {
			this.deploymentType = deploymentType;
		}

	}

	/**
	 * Next应用扩展参数模型
	 * 
	 */
	public static class AppExtendNextCreationParam implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * Node版本
		 */
		private String nodeVersion;

		/**
		 * Node镜像
		 */
		private String nodeImage;

		/**
		 * Npm版本
		 */
		private String npmVersion;

		/**
		 * Pnpm版本
		 */
		private String pnpmVersion;

		/**
		 * Yarn版本
		 */
		private String yarnVersion;

		/**
		 * 编译方式
		 */
		private Integer compileType;

		/**
		 * 部署方式，1：动态部署，2：静态部署
		 */
		private Integer deploymentType;

		public String getNodeVersion() {
			return nodeVersion;
		}

		public void setNodeVersion(String nodeVersion) {
			this.nodeVersion = nodeVersion;
		}

		public String getNodeImage() {
			return nodeImage;
		}

		public void setNodeImage(String nodeImage) {
			this.nodeImage = nodeImage;
		}

		public String getNpmVersion() {
			return npmVersion;
		}

		public void setNpmVersion(String npmVersion) {
			this.npmVersion = npmVersion;
		}

		public String getPnpmVersion() {
			return pnpmVersion;
		}

		public void setPnpmVersion(String pnpmVersion) {
			this.pnpmVersion = pnpmVersion;
		}

		public Integer getCompileType() {
			return compileType;
		}

		public void setCompileType(Integer compileType) {
			this.compileType = compileType;
		}

		public String getYarnVersion() {
			return yarnVersion;
		}

		public void setYarnVersion(String yarnVersion) {
			this.yarnVersion = yarnVersion;
		}

		public Integer getDeploymentType() {
			return deploymentType;
		}

		public void setDeploymentType(Integer deploymentType) {
			this.deploymentType = deploymentType;
		}

	}

	/**
	 * .NET应用扩展参数模型
	 * 
	 */
	public static class AppExtendDotNetCreationParam implements Serializable {

		private static final long serialVersionUID = 1L;

		/**
		 * .NET版本
		 */
		private String dotNetVersion;

		/**
		 * .NET镜像
		 */
		private String dotNetImage;

		public String getDotNetVersion() {
			return dotNetVersion;
		}

		public void setDotNetVersion(String dotNetVersion) {
			this.dotNetVersion = dotNetVersion;
		}

		public String getDotNetImage() {
			return dotNetImage;
		}

		public void setDotNetImage(String dotNetImage) {
			this.dotNetImage = dotNetImage;
		}

	}
}