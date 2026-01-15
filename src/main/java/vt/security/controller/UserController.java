package vt.security.controller;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vt.security.dto.UserResponse;
import vt.security.entity.User;
import vt.security.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/{id}")
    @PreAuthorize("@userPolicy.canUpdate(authentication, #id)")
    public UserResponse updateUser(@PathVariable Long id) {

        User updatedUser = userService.updateUser(id);
        return userService.toResponse(updatedUser);
    }

}
