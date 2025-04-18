package kinman.dbee.domain.model.application;

import kinman.dbee.domain.model.common.ValueObject;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 版本值对象
 * <p>
 * 表示应用的版本信息，包含版本号、构建时间等信息。
 * 作为值对象实现，是不可变的。
 * </p>
 */
public class Version extends ValueObject {
    
    private final String versionNumber;
    private final String commitId;
    private final String buildNumber;
    private final LocalDateTime buildTime;
    private final String description;
    
    /**
     * 创建版本
     *
     * @param versionNumber 版本号
     * @param commitId 代码提交ID
     * @param buildNumber 构建编号
     * @param buildTime 构建时间
     * @param description 版本描述
     */
    public Version(String versionNumber, String commitId, String buildNumber, LocalDateTime buildTime, String description) {
        if (versionNumber == null || versionNumber.isEmpty()) {
            throw new IllegalArgumentException("版本号不能为空");
        }
        this.versionNumber = versionNumber;
        this.commitId = commitId;
        this.buildNumber = buildNumber;
        this.buildTime = buildTime;
        this.description = description;
    }
    
    /**
     * 创建简化版本
     *
     * @param versionNumber 版本号
     */
    public Version(String versionNumber) {
        this(versionNumber, null, null, LocalDateTime.now(), null);
    }
    
    /**
     * 获取版本号
     *
     * @return 版本号
     */
    public String getVersionNumber() {
        return versionNumber;
    }
    
    /**
     * 获取代码提交ID
     *
     * @return 代码提交ID
     */
    public String getCommitId() {
        return commitId;
    }
    
    /**
     * 获取构建编号
     *
     * @return 构建编号
     */
    public String getBuildNumber() {
        return buildNumber;
    }
    
    /**
     * 获取构建时间
     *
     * @return 构建时间
     */
    public LocalDateTime getBuildTime() {
        return buildTime;
    }
    
    /**
     * 获取版本描述
     *
     * @return 版本描述
     */
    public String getDescription() {
        return description;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Version version = (Version) o;
        return Objects.equals(versionNumber, version.versionNumber) &&
                Objects.equals(commitId, version.commitId) &&
                Objects.equals(buildNumber, version.buildNumber);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(versionNumber, commitId, buildNumber);
    }
    
    @Override
    public String toString() {
        return versionNumber + (buildNumber != null ? "-" + buildNumber : "");
    }
} 