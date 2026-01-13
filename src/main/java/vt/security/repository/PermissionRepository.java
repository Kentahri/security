package vt.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vt.security.entity.Permission;


public interface PermissionRepository extends JpaRepository<Permission, Long> {
}
