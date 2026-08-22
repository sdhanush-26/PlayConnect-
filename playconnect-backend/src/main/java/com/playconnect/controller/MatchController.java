package com.playconnect.controller;

import com.playconnect.dto.MatchPlayerResponse;
import com.playconnect.dto.MatchRequest;
import com.playconnect.dto.MatchResponse;
import com.playconnect.entity.JoinStatus;
import com.playconnect.entity.Match;
import com.playconnect.entity.MatchPlayer;
import com.playconnect.service.MatchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Test in Postman:
 *   POST http://localhost:8080/api/matches
 *   body: {
 *     "title": "Sunday Cricket",
 *     "creatorId": 1,
 *     "sportId": 1,
 *     "location": "City Cricket Ground, Anantapur",
 *     "matchDate": "2026-09-01",
 *     "startTime": "09:00:00",
 *     "endTime": "12:00:00",
 *     "maxPlayers": 11
 *   }
 */
@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;

    @Autowired
    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    private MatchResponse toResponse(Match match) {
        return new MatchResponse(
                match.getId(),
                match.getTitle(),
                match.getCreator().getId(),
                match.getCreator().getName(),
                match.getSport().getId(),
                match.getSport().getName(),
                match.getLocation(),
                match.getMatchDate(),
                match.getStartTime(),
                match.getEndTime(),
                match.getMaxPlayers(),
                match.getStatus()
        );
    }

    @PostMapping
    public ResponseEntity<MatchResponse> createMatch(@Valid @RequestBody MatchRequest request) {
        Match created = matchService.createMatch(
                request.getTitle(),
                request.getCreatorId(),
                request.getSportId(),
                request.getLocation(),
                request.getMatchDate(),
                request.getStartTime(),
                request.getEndTime(),
                request.getMaxPlayers()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @GetMapping
    public ResponseEntity<List<MatchResponse>> getAllMatches() {
        List<MatchResponse> responses = matchService.getAllMatches().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchResponse> getMatch(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(matchService.getMatch(id)));
    }

    // POST /api/matches/{id}/join?userId=2
    // userId as a query param for now — becomes "current logged-in user"
    // automatically once JWT auth exists (Day 40+), removing the need
    // for the client to pass it explicitly.
    @PostMapping("/{id}/join")
    public ResponseEntity<Void> joinMatch(@PathVariable Long id, @RequestParam Long userId) {
        matchService.joinMatch(id, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // DELETE /api/matches/{id}/leave?userId=2
    @DeleteMapping("/{id}/leave")
    public ResponseEntity<Void> leaveMatch(@PathVariable Long id, @RequestParam Long userId) {
        matchService.leaveMatch(id, userId);
        return ResponseEntity.noContent().build();
    }

    private MatchPlayerResponse toPlayerResponse(MatchPlayer mp) {
        return new MatchPlayerResponse(
                mp.getId(), mp.getUser().getId(), mp.getUser().getName(), mp.getJoinStatus());
    }

    // GET /api/matches/{id}/players — full roster with join status.
    @GetMapping("/{id}/players")
    public ResponseEntity<List<MatchPlayerResponse>> getMatchPlayers(@PathVariable Long id) {
        List<MatchPlayerResponse> responses = matchService.getMatchPlayers(id).stream()
                .map(this::toPlayerResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // PUT /api/matches/{id}/players/{userId}?requesterId=1&status=ACCEPTED
    // requesterId identifies the creator making the request — becomes
    // "current logged-in user" automatically once JWT auth exists (Day 40+).
    @PutMapping("/{id}/players/{userId}")
    public ResponseEntity<MatchPlayerResponse> respondToJoinRequest(
            @PathVariable Long id, @PathVariable Long userId,
            @RequestParam Long requesterId, @RequestParam JoinStatus status) {
        MatchPlayer updated = matchService.respondToJoinRequest(id, userId, requesterId, status);
        return ResponseEntity.ok(toPlayerResponse(updated));
    }

    // DELETE /api/matches/{id}/players/{userId}?requesterId=1
    // Creator-initiated removal — distinct from leaveMatch, which is
    // player-initiated and requires no permission check.
    @DeleteMapping("/{id}/players/{userId}")
    public ResponseEntity<Void> removePlayer(
            @PathVariable Long id, @PathVariable Long userId, @RequestParam Long requesterId) {
        matchService.removePlayer(id, userId, requesterId);
        return ResponseEntity.noContent().build();
    }

    // PUT /api/matches/{id}/start?requesterId=1
    @PutMapping("/{id}/start")
    public ResponseEntity<MatchResponse> startMatch(@PathVariable Long id, @RequestParam Long requesterId) {
        return ResponseEntity.ok(toResponse(matchService.startMatch(id, requesterId)));
    }

    // PUT /api/matches/{id}/complete?requesterId=1
    @PutMapping("/{id}/complete")
    public ResponseEntity<MatchResponse> completeMatch(@PathVariable Long id, @RequestParam Long requesterId) {
        return ResponseEntity.ok(toResponse(matchService.completeMatch(id, requesterId)));
    }

    // PUT /api/matches/{id}/cancel?requesterId=1
    @PutMapping("/{id}/cancel")
    public ResponseEntity<MatchResponse> cancelMatch(@PathVariable Long id, @RequestParam Long requesterId) {
        return ResponseEntity.ok(toResponse(matchService.cancelMatch(id, requesterId)));
    }
}