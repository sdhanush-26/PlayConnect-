package com.playconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Flat response shape for Ground — avoids returning the raw entity
 * directly, which was crashing Jackson when it tried to serialize the
 * lazy-loaded Sport relationship (a Hibernate proxy object, not a plain
 * Sport). Same lesson as Day 13's UserResponse: entities go in, DTOs
 * come out.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroundResponse {
    private Long id;
    private String name;
    private String location;
    private Double latitude;
    private Double longitude;
    private Long sportId;
    private String sportName;
}