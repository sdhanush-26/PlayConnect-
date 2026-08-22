package com.playconnect.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Catches exceptions thrown anywhere in the app and converts them into
 * clean, consistent JSON error responses instead of raw stack traces.
 * This is a cross-cutting concern — one class here replaces needing
 * try/catch blocks scattered through every controller method.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 — thrown by UserService.getUser()/deleteUser() when an id
    // doesn't exist. Before today this surfaced as an unhandled 500.
    @ExceptionHandler(PlayerNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePlayerNotFound(PlayerNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // 409 Conflict — thrown by UserService.createUser() for duplicate emails.
    // Using IllegalArgumentException there was a placeholder; this makes
    // its meaning explicit at the HTTP layer.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    // 400 — thrown by MatchService for logically invalid match data
    // (e.g. endTime before startTime) that isn't a simple field-level
    // validation failure, so @Valid alone can't catch it.
    @ExceptionHandler(InvalidMatchException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidMatch(InvalidMatchException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // 400 — triggered by @Valid failures on UserRequest (blank name,
    // malformed email, etc.). Spring throws this automatically; we just
    // reshape its output into our consistent error format, listing every
    // field that failed rather than just the first one.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation failed");
        body.put("fields", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // 500 — catch-all safety net for anything unexpected. Without this,
    // an unhandled bug would still leak a raw stack trace to the client.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later.");
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}