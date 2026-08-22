package com.playconnect.controller;

import com.playconnect.dto.UserRequest;
import com.playconnect.dto.UserResponse;
import com.playconnect.entity.User;
import com.playconnect.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST API for User management — now DTO-based.
 *
 * Requests come in as UserRequest (validated via @Valid before this
 * method body even runs). Responses go out as UserResponse, which has
 * no password field — fixing the leak from Day 12's raw-entity responses.
 *
 * Test in Postman:
 *   POST   http://localhost:8080/api/users
 *   GET    http://localhost:8080/api/users
 *   GET    http://localhost:8080/api/users/1
 *   PUT    http://localhost:8080/api/users/1
 *   DELETE http://localhost:8080/api/users/1
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Converts an internal User entity into the safe, public-facing DTO.
    // Every method below routes its return value through this.
    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getLatitude(),
                user.getLongitude(),
                user.getCreatedAt()
        );
    }

    // POST /api/users
    // @Valid triggers UserRequest's @NotBlank/@Email checks. A missing
    // name or malformed email now returns 400 Bad Request automatically,
    // with a body listing exactly which fields failed and why —
    // Spring Boot generates that response by default, no extra code needed.
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // plain text for now — BCrypt hashing arrives Day 39
        user.setPhone(request.getPhone());
        user.setLatitude(request.getLatitude());
        user.setLongitude(request.getLongitude());

        User created = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    // GET /api/users
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> responses = userService.getAllUsers().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // GET /api/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(userService.getUser(id)));
    }

    // PUT /api/users/{id}
    // Reuses UserRequest so the same validation rules apply to updates.
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                     @Valid @RequestBody UserRequest request) {
        User updatedData = new User();
        updatedData.setName(request.getName());
        updatedData.setPhone(request.getPhone());
        updatedData.setLatitude(request.getLatitude());
        updatedData.setLongitude(request.getLongitude());

        User updated = userService.updateUser(id, updatedData);
        return ResponseEntity.ok(toResponse(updated));
    }

    // DELETE /api/users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}