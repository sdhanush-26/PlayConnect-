package com.playconnect.dto;

import com.playconnect.entity.SkillLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One row in a player search result — a user paired with a specific
 * sport + skill level (since a player can appear once per sport they
 * play, e.g. "Ravi - Cricket - ADVANCED" and "Ravi - Football - BEGINNER"
 * are two separate rows if both match the search).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerSearchResponse {
    private Long userId;
    private String name;
    private Double latitude;
    private Double longitude;
    private String sportName;
    private SkillLevel skillLevel;
}
