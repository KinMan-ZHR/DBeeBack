package kinman.dbee.api.response.model;

import kinman.dbee.api.response.model.App.AppExtend;

public class AppExtendJava extends AppExtend {

	private static final long serialVersionUID = 1L;

	/**
	 * 构建方式，1：maven，2：gradle
	 */
	private Integer packageBuildType;

	/**
	 * 文件类型，1：jar，2：war，3：zip
	 */
	private Integer packageFileType;

	/**
	 * 文件路径
	 */
	private String packageTargetPath;

	/**
	 * Java安装目录
	 */
	private String javaHome;

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