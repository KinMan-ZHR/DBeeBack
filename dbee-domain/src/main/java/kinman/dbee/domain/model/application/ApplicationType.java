package kinman.dbee.domain.model.application;

/**
 * 应用类型枚举
 * <p>
 * 定义系统支持的各种应用类型。
 * </p>
 */
public enum ApplicationType {
    
    /**
     * Java应用（Tomcat部署）
     */
    JAVA_TOMCAT("Java Tomcat应用"),
    
    /**
     * Spring Boot应用
     */
    SPRING_BOOT("Spring Boot应用"),
    
    /**
     * Node.js应用
     */
    NODEJS("Node.js应用"),
    
    /**
     * Vue前端应用（Nginx部署）
     */
    VUE_NGINX("Vue+Nginx应用"),
    
    /**
     * React前端应用（Nginx部署）
     */
    REACT_NGINX("React+Nginx应用"),
    
    /**
     * Python应用
     */
    PYTHON("Python应用"),
    
    /**
     * Go应用
     */
    GO("Go应用"),
    
    /**
     * 数据库（MySQL）
     */
    DATABASE_MYSQL("MySQL数据库"),
    
    /**
     * 数据库（PostgreSQL）
     */
    DATABASE_POSTGRESQL("PostgreSQL数据库"),
    
    /**
     * 其他类型
     */
    OTHER("其他类型");
    
    private final String description;
    
    ApplicationType(String description) {
        this.description = description;
    }
    
    /**
     * 获取应用类型描述
     *
     * @return 应用类型描述
     */
    public String getDescription() {
        return description;
    }
} 