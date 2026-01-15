package vt.security.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    // Công khai, không cần xác thực
    @GetMapping("/public")
    public String publicEndpoint() {
        return "Public endpoint - no authentication required";
    }

    // Chỉ cần đăng nhập (token hợp lệ)
    @GetMapping("/api/profile")
    public String userProfile() {
        return "USER profile - authenticated";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/api/user/only")
    public String onlyUser() {
        return "Only USER can access";
    }

    // Chỉ ROLE_ADMIN
    @GetMapping("/api/admin/only")
    public String onlyAdmin() {
        return "Only ADMIN can access";
    }

    // Permission level (authority)
    @PreAuthorize("hasAuthority('USER_READ')")
    @GetMapping("/api/user/read")
    public String userRead() {
        return "User READ permission";
    }

    @PreAuthorize("hasAuthority('ADMIN_MANAGE')")
    @GetMapping("/api/admin/manage")
    public String adminManage() {
        return "Admin MANAGE permission";
    }
}

