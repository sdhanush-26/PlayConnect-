package com.playconnect.controller;

import com.playconnect.dto.MatchRequest;
import com.playconnect.dto.MatchResponse;
import com.playconnect.entity.Match;
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
}