package vt.security.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import vt.security.config.UserPrincipal;
import vt.security.entity.EndpointPermission;
import vt.security.entity.Post;
import vt.security.repository.EndpointPermissionRepository;
import vt.security.repository.PostRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthorizationService {

    private final EndpointPermissionRepository endpointPermissionRepo;
    private final PostRepository postRepository;

    public AuthorizationService(EndpointPermissionRepository endpointPermissionRepo,
                                PostRepository postRepository) {
        this.endpointPermissionRepo = endpointPermissionRepo;
        this.postRepository = postRepository;
    }

    /* ================= BASIC PERMISSION ================= */

    public boolean hasPermission(
            Authentication authentication,
            String httpMethod,
            String uri) {

        String normalizedUri = normalizeUri(uri);

        EndpointPermission ep =
                endpointPermissionRepo
                        .findByHttpMethodAndUrlPattern(httpMethod, normalizedUri)
                        .stream()
                        .findFirst()
                        .orElse(null);

        if (ep == null) {
            System.out.println("[AUTH] ❌ No endpoint rule → DENY: "
                    + httpMethod + " " + normalizedUri);
            return false;
        }

        Set<String> userPermissions = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        boolean granted = userPermissions.contains(ep.getRequiredPermission());

        System.out.println("[AUTH] Required=" + ep.getRequiredPermission()
                + " | User=" + userPermissions
                + " | Result=" + granted);

        return granted;
    }

    /* ================= RESOURCE / OWNERSHIP ================= */

    public boolean hasPermissionForResource(
            Authentication authentication,
            String httpMethod,
            String uri,
            Long resourceId) {

        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        Set<String> userPermissions = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        String normalizedUri = normalizeUri(uri);

        // 1️⃣ ADMIN / ANY
        if (httpMethod.equals("PUT") && userPermissions.contains("UPDATE_ANY_POST")) {
            return true;
        }
        if (httpMethod.equals("DELETE") && userPermissions.contains("DELETE_ANY_POST")) {
            return true;
        }

        // 2️⃣ Check endpoint rule
        EndpointPermission ep =
                endpointPermissionRepo
                        .findByHttpMethodAndUrlPattern(httpMethod, normalizedUri)
                        .stream()
                        .findFirst()
                        .orElse(null);

        if (ep == null) {
            System.out.println("[AUTH] ❌ No endpoint rule → DENY");
            return false;
        }

        // 3️⃣ Check OWN permission
        if (!userPermissions.contains(ep.getRequiredPermission())) {
            return false;
        }

        // 4️⃣ Ownership
        Post post = postRepository.findById(resourceId).orElse(null);
        if (post == null) {
            return false;
        }

        boolean isOwner = post.getUserId().equals(user.getId());
        System.out.println("[AUTH] Ownership: " + isOwner);

        return isOwner;
    }

    /* ================= UTIL ================= */

    private String normalizeUri(String uri) {
        uri = uri.split("\\?")[0];
        return uri.replaceAll("/\\d+", "/{id}");
    }
}
