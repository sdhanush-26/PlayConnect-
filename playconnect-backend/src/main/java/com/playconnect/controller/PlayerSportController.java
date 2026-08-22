package com.playconnect.controller;

import com.playconnect.dto.PlayerSportResponse;
import com.playconnect.entity.PlayerSport;
import com.playconnect.entity.SkillLevel;
import com.playconnect.service.PlayerSportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Test in Postman:
 *   POST http://localhost:8080/api/player-sports
 *        body: {"userId": 1, "sportId": 1, "skillLevel": "INTERMEDIATE"}
 *   PUT  http://localhost:8080/api/player-sports/user/1/sport/1
 *        body: {"skillLevel": "ADVANCED"}
 *   GET  http://localhost:8080/api/player-sports/user/1     (all sports for a player)
 *   GET  http://localhost:8080/api/player-sports/sport/1    (all players of a sport)
 *   DELETE http://localhost:8080/api/player-sports/user/1/sport/1
 */
@RestController
@RequestMapping("/api/player-sports")
public class PlayerSportController {

    private final PlayerSportService playerSportService;

    @Autowired
    public PlayerSportController(PlayerSportService playerSportService) {
        this.playerSportService = playerSportService;
    }

    private PlayerSportResponse toResponse(PlayerSport ps) {
        return new PlayerSportResponse(
                ps.getId(),
                ps.getUser().getId(),
                ps.getUser().getName(),
                ps.getSport().getId(),
                ps.getSport().getName(),
                ps.getSkillLevel()
        );
    }

    // Simple request shape just for this endpoint — small enough that a
    // dedicated top-level DTO class isn't worth it yet.
    public record AddSportRequest(Long userId, Long sportId, SkillLevel skillLevel) {}
    public record SkillLevelRequest(SkillLevel skillLevel) {}

    @PostMapping
    public ResponseEntity<PlayerSportResponse> addPlayerSport(@RequestBody AddSportRequest request) {
        PlayerSport created = playerSportService.addPlayerSport(
                request.userId(), request.sportId(), request.skillLevel());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @PutMapping("/user/{userId}/sport/{sportId}")
    public ResponseEntity<PlayerSportResponse> updateSkillLevel(
            @PathVariable Long userId, @PathVariable Long sportId,
            @RequestBody SkillLevelRequest request) {
        PlayerSport updated = playerSportService.updateSkillLevel(
                userId, sportId, request.skillLevel());
        return ResponseEntity.ok(toResponse(updated));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PlayerSportResponse>> getSportsForUser(@PathVariable Long userId) {
        List<PlayerSportResponse> responses = playerSportService.getSportsForUser(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/sport/{sportId}")
    public ResponseEntity<List<PlayerSportResponse>> getPlayersForSport(@PathVariable Long sportId) {
        List<PlayerSportResponse> responses = playerSportService.getPlayersForSport(sportId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/user/{userId}/sport/{sportId}")
    public ResponseEntity<Void> removePlayerSport(@PathVariable Long userId, @PathVariable Long sportId) {
        playerSportService.removePlayerSport(userId, sportId);
        return ResponseEntity.noContent().build();
    }
}
