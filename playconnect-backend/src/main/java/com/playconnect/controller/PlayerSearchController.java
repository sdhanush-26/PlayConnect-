package com.playconnect.controller;

import com.playconnect.dto.PlayerSearchResponse;
import com.playconnect.entity.PlayerSport;
import com.playconnect.entity.SkillLevel;
import com.playconnect.service.PlayerSportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Test in Postman — all params are optional and combinable:
 *   GET http://localhost:8080/api/players
 *   GET http://localhost:8080/api/players?sportId=1
 *   GET http://localhost:8080/api/players?skillLevel=ADVANCED
 *   GET http://localhost:8080/api/players?sportId=1&skillLevel=ADVANCED
 *   GET http://localhost:8080/api/players?latitude=14.68&longitude=77.60&radiusKm=10
 *
 * Note: "availability" filtering (from the Day 20 plan) is deferred —
 * it needs Match/MatchPlayer data to mean anything real, which doesn't
 * exist until Day 22+. Adding it now would mean either a fake filter
 * that does nothing, or a half-built one that breaks once matches exist.
 */
@RestController
public class PlayerSearchController {

    private final PlayerSportService playerSportService;

    @Autowired
    public PlayerSearchController(PlayerSportService playerSportService) {
        this.playerSportService = playerSportService;
    }

    @GetMapping("/api/players")
    public ResponseEntity<List<PlayerSearchResponse>> searchPlayers(
            @RequestParam(required = false) Long sportId,
            @RequestParam(required = false) SkillLevel skillLevel,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double radiusKm) {

        List<PlayerSport> results = playerSportService.searchPlayers(
                sportId, skillLevel, latitude, longitude, radiusKm);

        List<PlayerSearchResponse> response = results.stream()
                .map(ps -> new PlayerSearchResponse(
                        ps.getUser().getId(),
                        ps.getUser().getName(),
                        ps.getUser().getLatitude(),
                        ps.getUser().getLongitude(),
                        ps.getSport().getName(),
                        ps.getSkillLevel(),
                        null // distance not calculated for general search
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // GET /api/players/nearby?latitude=14.68&longitude=77.60&radiusKm=10&sportId=1
    // latitude/longitude/radiusKm are all required here — this endpoint's
    // whole purpose is proximity, unlike /api/players where location is
    // just one of several optional filters.
    @GetMapping("/api/players/nearby")
    public ResponseEntity<List<PlayerSearchResponse>> nearbyPlayers(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam Double radiusKm,
            @RequestParam(required = false) Long sportId) {

        List<PlayerSportService.NearbyResult> results =
                playerSportService.findNearbyPlayers(latitude, longitude, radiusKm, sportId);

        List<PlayerSearchResponse> response = results.stream()
                .map(result -> {
                    PlayerSport ps = result.playerSport();
                    return new PlayerSearchResponse(
                            ps.getUser().getId(),
                            ps.getUser().getName(),
                            ps.getUser().getLatitude(),
                            ps.getUser().getLongitude(),
                            ps.getSport().getName(),
                            ps.getSkillLevel(),
                            Math.round(result.distanceKm() * 10) / 10.0 // round to 1 decimal
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}