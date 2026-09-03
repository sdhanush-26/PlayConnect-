package com.playconnect.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * TEMPORARY Day 41 verification endpoint — proves JwtAuthenticationFilter
 * is correctly reading tokens and populating SecurityContextHolder,
 * ahead of Day 43's real protected endpoints. Not listed in the plan as
 * its own deliverable; exists purely so today's work is observable.
 */
@RestController
public class AuthTestController {

    @GetMapping("/api/auth/me")
    public Map<String, Object> whoAmI(Authentication authentication) {
        if (authentication == null) {
            return Map.of("authenticated", false, "message", "No valid token provided");
        }
        return Map.of(
                "authenticated", true,
                "email", authentication.getName(),
                "userId", authentication.getDetails(),
                "roles", authentication.getAuthorities()
        );
    }
}