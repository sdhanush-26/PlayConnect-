package com.playconnect.dto;

import com.playconnect.entity.SkillLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Flat, clean response shape for a player's sport + skill level —
 * avoids exposing the full nested User/Sport entity graph (which would
 * include the password field again, same issue Day 13 fixed).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerSportResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long sportId;
    private String sportName;
    private SkillLevel skillLevel;
}
