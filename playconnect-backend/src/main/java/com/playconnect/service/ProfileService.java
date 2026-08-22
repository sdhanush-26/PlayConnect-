package com.playconnect.service;

import com.playconnect.dto.PlayerSportResponse;
import com.playconnect.dto.ProfileResponse;
import com.playconnect.entity.PlayerSport;
import com.playconnect.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Assembles the combined profile view from UserService and
 * PlayerSportService, rather than duplicating their lookup logic here.
 * This keeps each service focused on its own entity, while this class
 * handles the "combine several things into one response" concern.
 */
@Service
public class ProfileService {

    private final UserService userService;
    private final PlayerSportService playerSportService;

    @Autowired
    public ProfileService(UserService userService, PlayerSportService playerSportService) {
        this.userService = userService;
        this.playerSportService = playerSportService;
    }

    public ProfileResponse getProfile(Long userId) {
        User user = userService.getUser(userId); // throws PlayerNotFoundException if missing

        List<PlayerSportResponse> sports = playerSportService.getSportsForUser(userId).stream()
                .map(this::toPlayerSportResponse)
                .collect(Collectors.toList());

        return new ProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getLatitude(),
                user.getLongitude(),
                user.getCreatedAt(),
                sports,
                0,      // matchesPlayed — real value arrives once Match exists (Day 22+)
                null    // averageRating — real value arrives once Rating exists (Day 55)
        );
    }

    private PlayerSportResponse toPlayerSportResponse(PlayerSport ps) {
        return new PlayerSportResponse(
                ps.getId(),
                ps.getUser().getId(),
                ps.getUser().getName(),
                ps.getSport().getId(),
                ps.getSport().getName(),
                ps.getSkillLevel()
        );
    }
}
