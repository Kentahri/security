package vt.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vt.security.entity.EndpointPermission;

import java.util.List;

@Repository
public interface EndpointPermissionRepository extends JpaRepository<EndpointPermission, Long> {

    /**
     * Tìm endpoint permission theo HTTP method và URL pattern
     */
    List<EndpointPermission> findByHttpMethodAndUrlPattern(String httpMethod, String urlPattern);

    /**
     * Tìm tất cả permissions cho 1 URL pattern
     */
    List<EndpointPermission> findByUrlPattern(String urlPattern);

    /**
     * Tìm tất cả endpoints yêu cầu 1 permission cụ thể
     */
    List<EndpointPermission> findByRequiredPermission(String requiredPermission);
}