package com.voidhub.api.user;

import com.voidhub.api.exceptions.EntityAlreadyExistsException;
import com.voidhub.api.util.Message;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDto> getUsers() {
        return userRepository.findAll().stream().map(UserDto::new).toList();
    }

    public Optional<UserDto> getUserByUsername(String username) {
        return userRepository.findById(username).map(UserDto::new);
    }

    public ResponseEntity<Message> createNewUser(NewUserForm form) {
        String username = form.getUsername();
        String password = form.getPassword();

        if (userRepository.existsById(username)) {
            throw new EntityAlreadyExistsException("Username already exists");
        }

        User user = new User(username, passwordEncoder.encode(password), Role.MEMBER);
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.OK).body(new Message("Successfully created user"));
    }

    public ResponseEntity<Message> deleteUser(String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        userRepository.delete(user);

        return ResponseEntity.status(HttpStatus.OK).body(new Message("Successfully deleted user"));
    }

    public ResponseEntity<Message> updateUserRole(String username, String roleName) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        Role role;

        try {
            role = Role.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role");
        }

        if (role.equals(user.getRole())) {
            throw new EntityAlreadyExistsException("User already has assigned role");
        }

        user.setRole(role);
        userRepository.save(user);

        return ResponseEntity.ok(new Message("Successfully updated user role"));
    }

    public ResponseEntity<Message> updateUserPassword(UpdatePasswordForm form, String username) {
        String oldPassword = form.getOldPassword();
        String newPassword = form.getNewPassword();

        User user = userRepository.findById(username)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message("Wrong password"));
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("New password must be different from the old one");
        }

        newPassword = passwordEncoder.encode(newPassword);

        user.setPassword(newPassword);
        userRepository.save(user);

        return ResponseEntity.ok(new Message("Successfully updated password"));
    }

}
