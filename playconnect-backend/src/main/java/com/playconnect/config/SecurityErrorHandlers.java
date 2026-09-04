package com.playconnect.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Without this, Spring Security's defaults return a blank 401/403 body,
 * inconsistent with the clean JSON error shape GlobalExceptionHandler
 * (Day 14) uses everywhere else. These run BEFORE the request reaches
 * DispatcherServlet, so @RestControllerAdvice alone can't catch them —
 * they need to be registered directly on the security filter chain.
 */
@Component
public class SecurityErrorHandlers implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    // 401 — no token, or an invalid/expired one, on a protected endpoint.
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                          AuthenticationException authException) throws java.io.IOException {
        writeError(response, HttpStatus.UNAUTHORIZED, "Authentication required");
    }

    // 403 — valid token, but wrong role (e.g. PLAYER hitting an ADMIN-only endpoint).
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                        AccessDeniedException accessDeniedException) throws java.io.IOException {
        writeError(response, HttpStatus.FORBIDDEN, "You don't have permission to perform this action");
    }

    private void writeError(HttpServletResponse response, HttpStatus status, String message)
            throws java.io.IOException {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);

        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}