package vt.security.service;

import org.springframework.stereotype.Service;
import vt.security.dto.UserResponse;
import vt.security.entity.Permission;
import vt.security.entity.Role;
import vt.security.entity.User;
import vt.security.repository.UserRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User updateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // demo: giả sử update username
        user.setUsername(user.getUsername());

        return userRepository.save(user);
    }

    public UserResponse toResponse(User user) {

        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        Set<String> permissions = user.getRoles().stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.isEnabled(),
                roles,
                permissions
        );
    }
}
