package com.playconnect.dto;

import com.playconnect.entity.MatchStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchResponse {
    private Long id;
    private String title;
    private Long creatorId;
    private String creatorName;
    private Long sportId;
    private String sportName;
    private String location;
    private LocalDate matchDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer maxPlayers;
    private MatchStatus status;
}
