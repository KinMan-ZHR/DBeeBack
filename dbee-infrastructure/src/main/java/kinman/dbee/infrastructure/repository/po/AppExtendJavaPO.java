package kinman.dbee.infrastructure.repository.po;

import java.util.List;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("APP_EXTEND_JAVA")
public class AppExtendJavaPO extends AppExtendPO {

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
	 * 文件路径
	 */
	private String packageTargetPath;

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

	@Override
	public List<String> getIds() {
		return this.ids;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public Integer getDeletionStatus() {
		return this.deletionStatus;
	}

}