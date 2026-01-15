package vt.security.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import vt.security.entity.*;
import vt.security.repository.*;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final EndpointPermissionRepository endpointPermissionRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            UserRepository userRepository,
            EndpointPermissionRepository endpointPermissionRepository,
            PostRepository postRepository,
            PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
        this.endpointPermissionRepository = endpointPermissionRepository;
        this.postRepository = postRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        if (permissionRepository.count() > 0) {
            System.out.println("✅ Data already initialized, skipping...");
            return;
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("🔧 INITIALIZING DATABASE WITH SAMPLE DATA");
        System.out.println("=".repeat(60) + "\n");

        // ==================== 1. CREATE PERMISSIONS ====================
        System.out.println("📝 Creating Permissions...");

        // Post permissions
        Permission readPost = permissionRepository.save(new Permission("READ_POST"));
        Permission createPost = permissionRepository.save(new Permission("CREATE_POST"));
        Permission updateOwnPost = permissionRepository.save(new Permission("UPDATE_OWN_POST"));
        Permission updateAnyPost = permissionRepository.save(new Permission("UPDATE_ANY_POST"));
        Permission deleteOwnPost = permissionRepository.save(new Permission("DELETE_OWN_POST"));
        Permission deleteAnyPost = permissionRepository.save(new Permission("DELETE_ANY_POST"));

        // User permissions
        Permission readUser = permissionRepository.save(new Permission("READ_USER"));
        Permission updateOwnUser = permissionRepository.save(new Permission("UPDATE_OWN_USER"));
        Permission updateAnyUser = permissionRepository.save(new Permission("UPDATE_ANY_USER"));
        Permission deleteUser = permissionRepository.save(new Permission("DELETE_USER"));

        System.out.println("   ✅ Created 10 permissions");

        // ==================== 2. CREATE ROLES ====================
        System.out.println("\n👥 Creating Roles...");

        // VIEWER: chỉ đọc
        Role viewer = new Role("ROLE_VIEWER");
        viewer.setPermissions(Set.of(readPost, readUser));
        viewer = roleRepository.save(viewer);
        System.out.println("   ✅ ROLE_VIEWER: READ_POST, READ_USER");

        // EDITOR: tạo, sửa/xóa post của mình
        Role editor = new Role("ROLE_EDITOR");
        editor.setPermissions(Set.of(
                readPost, createPost, updateOwnPost, deleteOwnPost,
                readUser, updateOwnUser
        ));
        editor = roleRepository.save(editor);
        System.out.println("   ✅ ROLE_EDITOR: +CREATE, UPDATE/DELETE own posts and user");

        // ADMIN: full access
        Role admin = new Role("ROLE_ADMIN");
        admin.setPermissions(Set.of(
                readPost, createPost, updateAnyPost, deleteAnyPost,
                readUser, updateAnyUser, deleteUser
        ));
        admin = roleRepository.save(admin);
        System.out.println("   ✅ ROLE_ADMIN: Full access to everything");

        // ==================== 3. CREATE USERS ====================
        System.out.println("\n🧑 Creating Users...");

        User viewerUser = new User("viewer", passwordEncoder.encode("123456"));
        viewerUser.setRoles(Set.of(viewer));
        viewerUser = userRepository.save(viewerUser);
        System.out.println("   ✅ viewer/123456 (ROLE_VIEWER)");

        User editorUser = new User("editor", passwordEncoder.encode("123456"));
        editorUser.setRoles(Set.of(editor));
        editorUser = userRepository.save(editorUser);
        System.out.println("   ✅ editor/123456 (ROLE_EDITOR)");

        User adminUser = new User("admin", passwordEncoder.encode("123456"));
        adminUser.setRoles(Set.of(admin));
        adminUser = userRepository.save(adminUser);
        System.out.println("   ✅ admin/123456 (ROLE_ADMIN)");

        // ==================== 4. CREATE SAMPLE POSTS ====================
        System.out.println("\n📄 Creating Sample Posts...");

        Post post1 = new Post("Editor's First Post", "This is created by editor", editorUser.getId());
        postRepository.save(post1);

        Post post2 = new Post("Editor's Second Post", "Another post by editor", editorUser.getId());
        postRepository.save(post2);

        Post post3 = new Post("Admin's Post", "This is created by admin", adminUser.getId());
        postRepository.save(post3);

        System.out.println("   ✅ Created 3 sample posts");

        // ==================== 5. MAP ENDPOINTS TO PERMISSIONS ====================
        System.out.println("\n🔗 Mapping Endpoints to Permissions...");

        // Post endpoints
        endpointPermissionRepository.save(new EndpointPermission(
                "GET", "/api/posts", "READ_POST", "List all posts"
        ));
        endpointPermissionRepository.save(new EndpointPermission(
                "GET", "/api/posts/{id}", "READ_POST", "Get post by ID"
        ));
        endpointPermissionRepository.save(new EndpointPermission(
                "POST", "/api/posts", "CREATE_POST", "Create new post"
        ));
        endpointPermissionRepository.save(new EndpointPermission(
                "PUT", "/api/posts/{id}", "UPDATE_OWN_POST", "Update post"
        ));
        endpointPermissionRepository.save(new EndpointPermission(
                "DELETE", "/api/posts/{id}", "DELETE_OWN_POST", "Delete post"
        ));

        // User endpoints
        endpointPermissionRepository.save(new EndpointPermission(
                "GET", "/api/users", "READ_USER", "List all users"
        ));
        endpointPermissionRepository.save(new EndpointPermission(
                "GET", "/api/users/{id}", "READ_USER", "Get user by ID"
        ));
        endpointPermissionRepository.save(new EndpointPermission(
                "PUT", "/api/users/{id}", "UPDATE_OWN_USER", "Update user"
        ));
        endpointPermissionRepository.save(new EndpointPermission(
                "DELETE", "/api/users/{id}", "DELETE_USER", "Delete user"
        ));

        System.out.println("   ✅ Created 9 endpoint-permission mappings");

        // ==================== SUMMARY ====================
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎉 DATABASE INITIALIZATION COMPLETED!");
        System.out.println("=".repeat(60));
        System.out.println("\n📊 Summary:");
        System.out.println("   • 10 Permissions created");
        System.out.println("   • 3 Roles created (VIEWER, EDITOR, ADMIN)");
        System.out.println("   • 3 Users created");
        System.out.println("   • 3 Sample posts created");
        System.out.println("   • 9 Endpoint mappings created");

        System.out.println("\n🔑 Test Accounts:");
        System.out.println("   ┌─────────────────────────────────────────────────┐");
        System.out.println("   │ Username: viewer  | Password: 123456            │");
        System.out.println("   │ Role: VIEWER      | Access: READ only           │");
        System.out.println("   ├─────────────────────────────────────────────────┤");
        System.out.println("   │ Username: editor  | Password: 123456            │");
        System.out.println("   │ Role: EDITOR      | Access: CREATE + own posts  │");
        System.out.println("   ├─────────────────────────────────────────────────┤");
        System.out.println("   │ Username: admin   | Password: 123456            │");
        System.out.println("   │ Role: ADMIN       | Access: FULL ACCESS         │");
        System.out.println("   └─────────────────────────────────────────────────┘");

        System.out.println("\n🌐 H2 Console: http://localhost:8080/h2-console");
        System.out.println("   JDBC URL: jdbc:h2:mem:testdb");
        System.out.println("   Username: sa");
        System.out.println("   Password: (leave blank)");
        System.out.println("\n" + "=".repeat(60) + "\n");
    }
}