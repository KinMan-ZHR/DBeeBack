package kinman.dbee.api.response.model;

import java.io.Serializable;
import java.util.List;

/**
 * 应用信息
 */
public class App extends BaseDto {

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
	 * 应用描述
	 */
	private String description;

	/**
	 * 应用扩展信息，分页查询时不返回数据
	 */
	private AppExtend appExtend;

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

	public List<String> getAffinityAppNames() {
		return affinityAppNames;
	}

	public void setAffinityAppNames(List<String> affinityAppNames) {
		this.affinityAppNames = affinityAppNames;
	}

	public String getCodeRepoPath() {
		return codeRepoPath;
	}

	public void setCodeRepoPath(String codeRepoPath) {
		this.codeRepoPath = codeRepoPath;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@SuppressWarnings("unchecked")
	public <T extends AppExtend> T getAppExtend() {
		return (T) appExtend;
	}

	public void setAppExtend(AppExtend appExtend) {
		this.appExtend = appExtend;
	}

	public static abstract class AppExtend implements Serializable {

		private static final long serialVersionUID = 1L;

	}
}