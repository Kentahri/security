package vt.security.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vt.security.config.UserPrincipal;
import vt.security.entity.User;
import vt.security.repository.UserRepository;

import java.util.stream.Stream;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username)
                );

        return new UserPrincipal(
                user,
                Stream.concat(
                        // Roles (đã có prefix ROLE_ trong DB)
                        user.getRoles().stream()
                                .map(r -> new SimpleGrantedAuthority(r.getName())),

                        // Permissions (không có prefix)
                        user.getRoles().stream()
                                .flatMap(r -> r.getPermissions().stream())
                                .map(p -> new SimpleGrantedAuthority(p.getName()))
                ).toList()
        );
    }
}