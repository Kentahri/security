package vt.security.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vt.security.config.UserPrincipal;
import vt.security.entity.User;
import vt.security.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * GET /api/users - Get all users
     * Permission: READ_USER (admin only)
     */
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * GET /api/users/{id} - Get user by ID
     * Permission: READ_USER
     */
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * GET /api/users/me - Get current user info
     * No special permission needed (authenticated users can access)
     */
    @GetMapping("/me")
    public User getCurrentUser(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * PUT /api/users/{id} - Update user
     * Permission: UPDATE_OWN_USER (if own) or UPDATE_ANY_USER (if admin)
     * Authorization checked in DynamicAuthorizationFilter
     */
    @PutMapping("/{id}")
    public String updateUser(@PathVariable Long id, @RequestBody User userUpdate) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userUpdate.getUsername() != null) {
            user.setUsername(userUpdate.getUsername());
        }

        userRepository.save(user);
        return "User updated: " + id;
    }

    /**
     * DELETE /api/users/{id} - Delete user
     * Permission: DELETE_USER (admin only)
     */
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "User deleted: " + id;
    }
}