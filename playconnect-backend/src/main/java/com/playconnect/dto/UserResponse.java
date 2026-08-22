package com.playconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Shape of the JSON sent back to clients. Deliberately has NO password
 * field — this is what stops the leak Day 12's raw-entity responses had.
 * UserController converts User -> UserResponse before returning anything.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;
}
