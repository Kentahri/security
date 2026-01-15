package vt.security.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vt.security.service.AuthorizationService;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DynamicAuthorizationFilter extends OncePerRequestFilter {

    private final AuthorizationService authorizationService;

    public DynamicAuthorizationFilter(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.equals("/login")
                || path.equals("/register")
                || path.startsWith("/public")
                || path.startsWith("/h2-console");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        String httpMethod = request.getMethod();
        String rawUri = request.getRequestURI();

        Long resourceId = extractResourceId(rawUri);

        System.out.println("\n[FILTER] Checking authorization for: "
                + httpMethod + " " + rawUri + " | resourceId=" + resourceId);

        boolean authorized;

        if ((httpMethod.equals("PUT") || httpMethod.equals("DELETE"))) {
            if (resourceId == null) {
                // PUT / DELETE mà không có ID → cấm
                deny(response);
                return;
            }

            authorized = authorizationService.hasPermissionForResource(
                    authentication, httpMethod, rawUri, resourceId
            );
        } else {
            authorized = authorizationService.hasPermission(
                    authentication, httpMethod, rawUri
            );
        }

        if (!authorized) {
            deny(response);
            return;
        }

        System.out.println("[FILTER] ✅ Access GRANTED\n");
        filterChain.doFilter(request, response);
    }

    private void deny(HttpServletResponse response) throws IOException {
        System.out.println("[FILTER] ❌ Access DENIED\n");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                "{\"error\":\"Access denied\",\"message\":\"You don't have permission\"}"
        );
    }

    private Long extractResourceId(String uri) {
        Pattern pattern = Pattern.compile("/(\\d+)(?:/.*)?$");
        Matcher matcher = pattern.matcher(uri);
        return matcher.find() ? Long.parseLong(matcher.group(1)) : null;
    }
}
