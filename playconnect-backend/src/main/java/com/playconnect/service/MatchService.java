package com.playconnect.service;

import com.playconnect.entity.*;
import com.playconnect.exception.InvalidMatchException;
import com.playconnect.repository.MatchPlayerRepository;
import com.playconnect.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final MatchPlayerRepository matchPlayerRepository;
    private final UserService userService;
    private final SportService sportService;

    @Autowired
    public MatchService(MatchRepository matchRepository, MatchPlayerRepository matchPlayerRepository,
                         UserService userService, SportService sportService) {
        this.matchRepository = matchRepository;
        this.matchPlayerRepository = matchPlayerRepository;
        this.userService = userService;
        this.sportService = sportService;
    }

    public Match createMatch(String title, Long creatorId, Long sportId, String location,
                              LocalDate matchDate, LocalTime startTime, LocalTime endTime,
                              Integer maxPlayers) {

        if (!endTime.isAfter(startTime)) {
            throw new InvalidMatchException("endTime must be after startTime");
        }

        User creator = userService.getUser(creatorId);   // 404 if missing
        Sport sport = sportService.getSport(sportId);     // 404 if missing

        Match match = new Match();
        match.setTitle(title);
        match.setCreator(creator);
        match.setSport(sport);
        match.setLocation(location);
        match.setMatchDate(matchDate);
        match.setStartTime(startTime);
        match.setEndTime(endTime);
        match.setMaxPlayers(maxPlayers);
        match.setStatus(MatchStatus.OPEN);

        return matchRepository.save(match);
    }

    public java.util.List<Match> getAllMatches() {
        return matchRepository.findAll();
    }

    public Match getMatch(Long id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new com.playconnect.exception.PlayerNotFoundException(
                        "Match not found with id: " + id));
    }

    // Joins a player to a match. Rules enforced here rather than at the
    // DB level alone (the UNIQUE constraint on match_players stops exact
    // duplicates, but "match is full" and clear error messages need
    // application logic).
    public MatchPlayer joinMatch(Long matchId, Long userId) {
        Match match = getMatch(matchId);
        User user = userService.getUser(userId);

        if (matchPlayerRepository.existsByMatchIdAndUserId(matchId, userId)) {
            throw new InvalidMatchException("This player has already joined this match");
        }

        if (match.getStatus() != MatchStatus.OPEN) {
            throw new InvalidMatchException("Cannot join a match with status " + match.getStatus());
        }

        long currentPlayers = matchPlayerRepository.countByMatchId(matchId);
        if (currentPlayers >= match.getMaxPlayers()) {
            throw new InvalidMatchException("This match is already full");
        }

        MatchPlayer matchPlayer = new MatchPlayer();
        matchPlayer.setMatch(match);
        matchPlayer.setUser(user);
        matchPlayer.setJoinStatus(JoinStatus.PENDING);
        MatchPlayer saved = matchPlayerRepository.save(matchPlayer);

        // If this join filled the match exactly, flip status to FULL —
        // keeps match.status accurate for search/listing without the
        // caller having to check player counts separately.
        if (currentPlayers + 1 >= match.getMaxPlayers()) {
            match.setStatus(MatchStatus.FULL);
            matchRepository.save(match);
        }

        return saved;
    }

    // Removes a player from a match. If the match was FULL, un-fills it
    // back to OPEN — mirrors the status flip in joinMatch so the two
    // operations stay symmetric.
    public void leaveMatch(Long matchId, Long userId) {
        Match match = getMatch(matchId);

        MatchPlayer matchPlayer = matchPlayerRepository.findByMatchIdAndUserId(matchId, userId)
                .orElseThrow(() -> new InvalidMatchException(
                        "This player has not joined this match"));

        matchPlayerRepository.delete(matchPlayer);

        if (match.getStatus() == MatchStatus.FULL) {
            match.setStatus(MatchStatus.OPEN);
            matchRepository.save(match);
        }
    }

    // Every creator-only action below checks requesterId == match.creator
    // before doing anything — this is the enforcement point for "only
    // the person who made the match can manage its players", ahead of
    // proper role-based auth arriving on Day 42.
    private void requireCreator(Match match, Long requesterId) {
        if (!match.getCreator().getId().equals(requesterId)) {
            throw new InvalidMatchException("Only the match creator can perform this action");
        }
    }

    public MatchPlayer respondToJoinRequest(Long matchId, Long targetUserId,
                                             Long requesterId, JoinStatus newStatus) {
        Match match = getMatch(matchId);
        requireCreator(match, requesterId);

        MatchPlayer matchPlayer = matchPlayerRepository.findByMatchIdAndUserId(matchId, targetUserId)
                .orElseThrow(() -> new InvalidMatchException(
                        "This player has not requested to join this match"));

        matchPlayer.setJoinStatus(newStatus);
        return matchPlayerRepository.save(matchPlayer);
    }

    public void removePlayer(Long matchId, Long targetUserId, Long requesterId) {
        Match match = getMatch(matchId);
        requireCreator(match, requesterId);

        MatchPlayer matchPlayer = matchPlayerRepository.findByMatchIdAndUserId(matchId, targetUserId)
                .orElseThrow(() -> new InvalidMatchException(
                        "This player is not part of this match"));

        matchPlayerRepository.delete(matchPlayer);

        if (match.getStatus() == MatchStatus.FULL) {
            match.setStatus(MatchStatus.OPEN);
            matchRepository.save(match);
        }
    }

    public java.util.List<MatchPlayer> getMatchPlayers(Long matchId) {
        getMatch(matchId); // ensures the match itself exists first
        return matchPlayerRepository.findByMatchId(matchId);
    }

    // Explicit status transitions, each creator-only and each rejecting
    // moves that don't make sense (e.g. starting an already-completed
    // match). Keeping these as separate methods rather than one generic
    // "setStatus" keeps the allowed-transition rules visible and testable.

    public Match startMatch(Long matchId, Long requesterId) {
        Match match = getMatch(matchId);
        requireCreator(match, requesterId);

        if (match.getStatus() != MatchStatus.OPEN && match.getStatus() != MatchStatus.FULL) {
            throw new InvalidMatchException(
                    "Cannot start a match with status " + match.getStatus());
        }
        match.setStatus(MatchStatus.STARTED);
        return matchRepository.save(match);
    }

    public Match completeMatch(Long matchId, Long requesterId) {
        Match match = getMatch(matchId);
        requireCreator(match, requesterId);

        if (match.getStatus() != MatchStatus.STARTED) {
            throw new InvalidMatchException(
                    "Cannot complete a match that hasn't been started (current status: "
                            + match.getStatus() + ")");
        }
        match.setStatus(MatchStatus.COMPLETED);
        return matchRepository.save(match);
    }

    public Match cancelMatch(Long matchId, Long requesterId) {
        Match match = getMatch(matchId);
        requireCreator(match, requesterId);

        if (match.getStatus() == MatchStatus.COMPLETED || match.getStatus() == MatchStatus.CANCELLED) {
            throw new InvalidMatchException(
                    "Cannot cancel a match with status " + match.getStatus());
        }
        match.setStatus(MatchStatus.CANCELLED);
        return matchRepository.save(match);
    }
}