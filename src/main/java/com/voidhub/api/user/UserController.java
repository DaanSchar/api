package com.voidhub.api.user;

import com.voidhub.api.util.Message;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private static final String ADMIN = "hasRole('ADMIN')";

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize(ADMIN)
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @PostMapping
    public ResponseEntity<Message> createNewUser(@RequestBody @Valid NewUserForm form) {
        return userService.createNewUser(form);
    }

    @PutMapping("/password")
    @PreAuthorize("hasAnyRole('ADMIN','MEMBER')")
    public ResponseEntity<Message> updateUserPassword(@Valid @RequestBody UpdatePasswordForm form, Principal principal) {
        return userService.updateUserPassword(form, principal.getName());
    }

    @PutMapping("/role")
    @PreAuthorize(ADMIN)
    public ResponseEntity<Message> updateUserRole(@Valid @RequestBody UpdateRoleForm form) {
        return userService.updateUserRole(form.getUsername(), form.getRole());
    }

    @GetMapping("{username}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    public UserDto getUserByUsername(@PathVariable(value = "username") String username) {
        return userService.getUserByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @DeleteMapping("{username}")
    @PreAuthorize(ADMIN)
    public ResponseEntity<Message> deleteUser(@PathVariable(value = "username") String username) {
        return userService.deleteUser(username);
    }

    @DeleteMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    public ResponseEntity<Message> deleteUser(Principal principal) {
        return userService.deleteUser(principal.getName());
    }

}
