package com.playconnect.controller;

import com.playconnect.dto.LoginRequest;
import com.playconnect.dto.LoginResponse;
import com.playconnect.dto.RegisterRequest;
import com.playconnect.dto.UserResponse;
import com.playconnect.entity.User;
import com.playconnect.service.UserService;
import com.playconnect.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Test in Postman:
 *   POST http://localhost:8080/api/auth/register
 *   POST http://localhost:8080/api/auth/login
 *        body: {"email": "newplayer@example.com", "password": "SecurePass123"}
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        User created = userService.register(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                request.getPhone(),
                request.getLatitude(),
                request.getLongitude()
        );

        // Same DTO pattern as everywhere else — never return the
        // (now-hashed, but still shouldn't be exposed) password field.
        UserResponse response = new UserResponse(
                created.getId(),
                created.getName(),
                created.getEmail(),
                created.getPhone(),
                created.getLatitude(),
                created.getLongitude(),
                created.getCreatedAt()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.login(request.getEmail(), request.getPassword());
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());

        LoginResponse response = new LoginResponse(
                token, user.getId(), user.getName(), user.getEmail());
        return ResponseEntity.ok(response);
    }
}