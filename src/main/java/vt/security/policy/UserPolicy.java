package vt.security.policy;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import vt.security.config.UserPrincipal;

@Component
public class UserPolicy {

    public boolean canUpdate(Long targetUserId, Authentication authentication) {

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserPrincipal currentUser)) {
            return false;
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return true;
        }

        return currentUser.getId().equals(targetUserId);
    }

}
