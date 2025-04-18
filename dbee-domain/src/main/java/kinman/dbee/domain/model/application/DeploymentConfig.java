package kinman.dbee.domain.model.application;

import kinman.dbee.domain.model.common.ValueObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 部署配置值对象
 * <p>
 * 包含应用部署所需的各种配置参数。
 * 作为值对象实现，是不可变的。
 * </p>
 */
public class DeploymentConfig extends ValueObject {
    
    private final int cpuLimit;
    private final int memoryLimitMB;
    private final int replicas;
    private final Map<String, String> environmentVariables;
    private final Map<String, String> jvmOptions;
    private final int port;
    private final String healthCheckPath;
    
    /**
     * 创建部署配置
     *
     * @param builder 构建器
     */
    private DeploymentConfig(Builder builder) {
        this.cpuLimit = builder.cpuLimit;
        this.memoryLimitMB = builder.memoryLimitMB;
        this.replicas = builder.replicas;
        this.environmentVariables = Collections.unmodifiableMap(new HashMap<>(builder.environmentVariables));
        this.jvmOptions = Collections.unmodifiableMap(new HashMap<>(builder.jvmOptions));
        this.port = builder.port;
        this.healthCheckPath = builder.healthCheckPath;
    }
    
    /**
     * 获取CPU限制
     *
     * @return CPU限制
     */
    public int getCpuLimit() {
        return cpuLimit;
    }
    
    /**
     * 获取内存限制(MB)
     *
     * @return 内存限制(MB)
     */
    public int getMemoryLimitMB() {
        return memoryLimitMB;
    }
    
    /**
     * 获取副本数
     *
     * @return 副本数
     */
    public int getReplicas() {
        return replicas;
    }
    
    /**
     * 获取环境变量
     *
     * @return 环境变量map
     */
    public Map<String, String> getEnvironmentVariables() {
        return environmentVariables;
    }
    
    /**
     * 获取JVM选项
     *
     * @return JVM选项map
     */
    public Map<String, String> getJvmOptions() {
        return jvmOptions;
    }
    
    /**
     * 获取应用端口
     *
     * @return 应用端口
     */
    public int getPort() {
        return port;
    }
    
    /**
     * 获取健康检查路径
     *
     * @return 健康检查路径
     */
    public String getHealthCheckPath() {
        return healthCheckPath;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeploymentConfig that = (DeploymentConfig) o;
        return cpuLimit == that.cpuLimit &&
                memoryLimitMB == that.memoryLimitMB &&
                replicas == that.replicas &&
                port == that.port &&
                Objects.equals(environmentVariables, that.environmentVariables) &&
                Objects.equals(jvmOptions, that.jvmOptions) &&
                Objects.equals(healthCheckPath, that.healthCheckPath);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(cpuLimit, memoryLimitMB, replicas, environmentVariables, jvmOptions, port, healthCheckPath);
    }
    
    /**
     * 部署配置构建器
     */
    public static class Builder {
        private int cpuLimit = 1;
        private int memoryLimitMB = 512;
        private int replicas = 1;
        private final Map<String, String> environmentVariables = new HashMap<>();
        private final Map<String, String> jvmOptions = new HashMap<>();
        private int port = 8080;
        private String healthCheckPath = "/health";
        
        /**
         * 设置CPU限制
         *
         * @param cpuLimit CPU限制
         * @return 构建器实例
         */
        public Builder cpuLimit(int cpuLimit) {
            this.cpuLimit = cpuLimit;
            return this;
        }
        
        /**
         * 设置内存限制(MB)
         *
         * @param memoryLimitMB 内存限制(MB)
         * @return 构建器实例
         */
        public Builder memoryLimitMB(int memoryLimitMB) {
            this.memoryLimitMB = memoryLimitMB;
            return this;
        }
        
        /**
         * 设置副本数
         *
         * @param replicas 副本数
         * @return 构建器实例
         */
        public Builder replicas(int replicas) {
            this.replicas = replicas;
            return this;
        }
        
        /**
         * 添加环境变量
         *
         * @param key 环境变量名
         * @param value 环境变量值
         * @return 构建器实例
         */
        public Builder addEnvironmentVariable(String key, String value) {
            this.environmentVariables.put(key, value);
            return this;
        }
        
        /**
         * 设置环境变量
         *
         * @param environmentVariables 环境变量map
         * @return 构建器实例
         */
        public Builder environmentVariables(Map<String, String> environmentVariables) {
            this.environmentVariables.clear();
            if (environmentVariables != null) {
                this.environmentVariables.putAll(environmentVariables);
            }
            return this;
        }
        
        /**
         * 添加JVM选项
         *
         * @param key JVM选项名
         * @param value JVM选项值
         * @return 构建器实例
         */
        public Builder addJvmOption(String key, String value) {
            this.jvmOptions.put(key, value);
            return this;
        }
        
        /**
         * 设置JVM选项
         *
         * @param jvmOptions JVM选项map
         * @return 构建器实例
         */
        public Builder jvmOptions(Map<String, String> jvmOptions) {
            this.jvmOptions.clear();
            if (jvmOptions != null) {
                this.jvmOptions.putAll(jvmOptions);
            }
            return this;
        }
        
        /**
         * 设置应用端口
         *
         * @param port 应用端口
         * @return 构建器实例
         */
        public Builder port(int port) {
            this.port = port;
            return this;
        }
        
        /**
         * 设置健康检查路径
         *
         * @param healthCheckPath 健康检查路径
         * @return 构建器实例
         */
        public Builder healthCheckPath(String healthCheckPath) {
            this.healthCheckPath = healthCheckPath;
            return this;
        }
        
        /**
         * 构建部署配置
         *
         * @return 部署配置实例
         */
        public DeploymentConfig build() {
            return new DeploymentConfig(this);
        }
    }
} 