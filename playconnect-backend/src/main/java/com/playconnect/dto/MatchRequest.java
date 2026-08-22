package com.playconnect.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class MatchRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "creatorId is required")
    private Long creatorId;

    @NotNull(message = "sportId is required")
    private Long sportId;

    private String location;

    @NotNull(message = "matchDate is required")
    @Future(message = "matchDate must be in the future")
    private LocalDate matchDate;

    @NotNull(message = "startTime is required")
    private LocalTime startTime;

    @NotNull(message = "endTime is required")
    private LocalTime endTime;

    @NotNull(message = "maxPlayers is required")
    @Min(value = 2, message = "maxPlayers must be at least 2")
    private Integer maxPlayers;
}
