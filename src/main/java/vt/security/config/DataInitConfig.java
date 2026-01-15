package vt.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import vt.security.entity.Permission;
import vt.security.entity.Role;
import vt.security.entity.User;
import vt.security.repository.PermissionRepository;
import vt.security.repository.RoleRepository;
import vt.security.repository.UserRepository;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataInitConfig implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {

        if (userRepository.count() > 0) {
            return; // đã có data thì không init lại
        }

        // ===== Permission =====
        Permission userRead = permissionRepository.save(
                new Permission("USER_READ", "Read user data")
        );

        Permission userWrite = permissionRepository.save(
                new Permission("USER_WRITE", "Write user data")
        );

        Permission adminManage = permissionRepository.save(
                new Permission("ADMIN_MANAGE", "Admin management")
        );

        // ===== Role =====
        Role roleNormal = new Role();
        roleNormal.setName("ROLE_NORMAL");
        roleNormal.setPermissions(Set.of());
        roleRepository.save(roleNormal);

        Role roleUser = new Role();
        roleUser.setName("ROLE_USER");
        roleUser.setPermissions(Set.of(userRead));
        roleRepository.save(roleUser);

        Role roleAdmin = new Role();
        roleAdmin.setName("ROLE_ADMIN");
        roleAdmin.setPermissions(Set.of(userRead, userWrite, adminManage));
        roleRepository.save(roleAdmin);

        // ===== User =====
        User normal = new User();
        normal.setUsername("normal");
        normal.setPassword(passwordEncoder.encode("123456"));
        normal.setEnabled(true);
        normal.setRoles(Set.of(roleNormal));
        userRepository.save(normal);

        User user = new User();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setEnabled(true);
        user.setRoles(Set.of(roleUser));
        userRepository.save(user);

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("123456"));
        admin.setEnabled(true);
        admin.setRoles(Set.of(roleAdmin));
        userRepository.save(admin);
    }
}
