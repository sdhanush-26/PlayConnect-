package com.playconnect.exception;

/**
 * Thrown when a lookup by user/player ID finds nothing.
 * Same pattern as the Day 5 practice version, now living in the real
 * package structure so it can be wired into @RestControllerAdvice (Day 14).
 */
public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(Long id) {
        super("Player not found with id: " + id);
    }

    public PlayerNotFoundException(String message) {
        super(message);
    }
}
