package com.playconnect.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * TEMPORARY Day 41/44 verification endpoint.
 *
 * Fixed a real bug found during Day 44 security testing: Spring Security
 * never leaves Authentication truly null for an unauthenticated request —
 * it substitutes a built-in "anonymousUser" principal instead. The
 * original null-check here never triggered, so an invalid/tampered
 * token fell through to building a response with Map.of(), which threw
 * on the first null value (authentication.getDetails() is null for the
 * anonymous case) and surfaced as an unrelated-looking 500.
 */
@RestController
public class AuthTestController {

    @GetMapping("/api/auth/me")
    public Map<String, Object> whoAmI() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isRealUser = authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal());

        Map<String, Object> body = new LinkedHashMap<>();
        if (!isRealUser) {
            body.put("authenticated", false);
            body.put("message", "No valid token provided");
            return body;
        }

        body.put("authenticated", true);
        body.put("email", authentication.getName());
        body.put("userId", authentication.getDetails());
        body.put("roles", authentication.getAuthorities());
        return body;
    }
}