package com.playconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Combined view for the profile page — pulls together data that lives
 * across three different tables (users, player_sport, and eventually
 * match_players + ratings) into one response the frontend can render
 * without making several separate API calls.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;

    private List<PlayerSportResponse> sports;

    // Placeholders until Match (Day 22+) and Rating (Day 55) exist.
    // Kept in the response shape now so the frontend can build the
    // profile UI today without needing to change it again later.
    private int matchesPlayed;
    private Double averageRating;
}
