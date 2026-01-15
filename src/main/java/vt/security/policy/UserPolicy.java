package vt.security.policy;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import vt.security.config.UserPrincipal;

@Component("userPolicy")
public class UserPolicy {

    public boolean canUpdate(
            Authentication authentication,
            Long targetUserId
    ) {

        if (!(authentication.getPrincipal() instanceof UserPrincipal subject)) {
            return false;
        }

        boolean isAdmin = subject.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) return true;

        return subject.getId().equals(targetUserId);
    }
}

