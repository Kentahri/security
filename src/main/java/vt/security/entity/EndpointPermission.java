package vt.security.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "endpoint_permissions")
public class EndpointPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "http_method", nullable = false)
    private String httpMethod;  // GET, POST, PUT, DELETE

    @Column(name = "url_pattern", nullable = false)
    private String urlPattern;  // /api/posts, /api/posts/{id}

    @Column(name = "required_permission", nullable = false)
    private String requiredPermission;  // READ_POST, CREATE_POST, etc.

    @Column(name = "description")
    private String description;  // Mô tả endpoint

    // Constructors
    public EndpointPermission() {}

    public EndpointPermission(String httpMethod, String urlPattern, String requiredPermission) {
        this.httpMethod = httpMethod;
        this.urlPattern = urlPattern;
        this.requiredPermission = requiredPermission;
    }

    public EndpointPermission(String httpMethod, String urlPattern,
                              String requiredPermission, String description) {
        this.httpMethod = httpMethod;
        this.urlPattern = urlPattern;
        this.requiredPermission = requiredPermission;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public String getRequiredPermission() {
        return requiredPermission;
    }

    public void setRequiredPermission(String requiredPermission) {
        this.requiredPermission = requiredPermission;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}