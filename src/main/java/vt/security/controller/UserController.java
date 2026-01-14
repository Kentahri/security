package vt.security.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @PutMapping("/{id}")
    @PreAuthorize("@userPolicy.canUpdate(#id, authentication)")
    public String updateUser(@PathVariable Long id) {
        return "User updated: " + id;
    }
}
