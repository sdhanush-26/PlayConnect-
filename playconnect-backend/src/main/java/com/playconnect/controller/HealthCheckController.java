package com.playconnect.controller;

import com.playconnect.entity.User;
import com.playconnect.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

// Temporary — just proves the server boots and routes work.
// Real controllers (UserController, MatchController, etc.) start Day 12.
@RestController
public class HealthCheckController {

    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of(
                "status", "UP",
                "service", "playconnect-backend"
        );
    }

    // ---------------------------------------------------------------
    // TEMPORARY Day 11 verification endpoint.
    // This proves UserService + UserRepository actually work end to
    // end against MySQL, ahead of Day 12's real UserController.
    // Delete this method once /api/users exists on Day 12.
    // ---------------------------------------------------------------
    private final UserService userService;

    @Autowired
    public HealthCheckController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/health/test-users")
    public List<User> testUsers() {
        // Creates one throwaway user (if not already present) then
        // returns everyone in the table — proving create + read both work.
        boolean alreadyExists = userService.getAllUsers().stream()
                .anyMatch(u -> u.getEmail().equals("test@playconnect.dev"));

        if (!alreadyExists) {
            User testUser = new User();
            testUser.setName("Test User");
            testUser.setEmail("test@playconnect.dev");
            testUser.setPassword("placeholder-hash");
            testUser.setPhone("9999999999");
            testUser.setLatitude(14.68);
            testUser.setLongitude(77.60);
            userService.createUser(testUser);
        }
        return userService.getAllUsers();
    }
}