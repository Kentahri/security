package vt.security.service;

import vt.security.config.UserPrincipal;
import vt.security.entity.User;
import vt.security.repository.UserRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
                        new UsernameNotFoundException("User not found")
                );

        return new UserPrincipal(
                user,
                Stream.concat(
                        user.getRoles().stream()
                                .map(r -> new SimpleGrantedAuthority(r.getName())),  // Chỉ lấy tên

                        // PERMISSION (không có prefix)
                        user.getRoles().stream()
                                .flatMap(r -> r.getPermissions().stream())
                                .map(p -> new SimpleGrantedAuthority(p.getName()))
                ).toList()
        );
    }
}