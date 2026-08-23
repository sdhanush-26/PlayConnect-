package com.playconnect.service;

import com.playconnect.entity.PlayerSport;
import com.playconnect.entity.Sport;
import com.playconnect.entity.User;
import com.playconnect.exception.PlayerNotFoundException;
import com.playconnect.repository.PlayerSportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerSportService {

    private final PlayerSportRepository playerSportRepository;
    private final UserService userService;
    private final SportService sportService;

    @Autowired
    public PlayerSportService(PlayerSportRepository playerSportRepository,
                               UserService userService,
                               SportService sportService) {
        this.playerSportRepository = playerSportRepository;
        this.userService = userService;
        this.sportService = sportService;
    }

    // Adds a sport to a player's profile at a given skill level.
    // Looks up the real User and Sport first so bad ids fail early
    // with a clear 404, rather than a confusing foreign-key DB error.
    public PlayerSport addPlayerSport(Long userId, Long sportId, com.playconnect.entity.SkillLevel skillLevel) {
        if (playerSportRepository.existsByUserIdAndSportId(userId, sportId)) {
            throw new IllegalArgumentException(
                    "This player already has a skill level set for this sport. Use update instead.");
        }

        User user = userService.getUser(userId);       // throws PlayerNotFoundException if missing
        Sport sport = sportService.getSport(sportId);   // same

        PlayerSport playerSport = new PlayerSport();
        playerSport.setUser(user);
        playerSport.setSport(sport);
        playerSport.setSkillLevel(skillLevel);

        return playerSportRepository.save(playerSport);
    }

    // Changes the skill level for a sport the player already has.
    public PlayerSport updateSkillLevel(Long userId, Long sportId, com.playconnect.entity.SkillLevel newLevel) {
        PlayerSport existing = playerSportRepository.findByUserIdAndSportId(userId, sportId)
                .orElseThrow(() -> new PlayerNotFoundException(
                        "No sport record found for this player/sport combination"));

        existing.setSkillLevel(newLevel);
        return playerSportRepository.save(existing);
    }

    // All sports (with skill levels) for one player — used on the
    // profile page starting Day 19.
    public List<PlayerSport> getSportsForUser(Long userId) {
        return playerSportRepository.findByUserId(userId);
    }

    // All players of a given sport, at any skill level — used by
    // player search starting Day 20.
    public List<PlayerSport> getPlayersForSport(Long sportId) {
        return playerSportRepository.findBySportId(sportId);
    }

    public void removePlayerSport(Long userId, Long sportId) {
        PlayerSport existing = playerSportRepository.findByUserIdAndSportId(userId, sportId)
                .orElseThrow(() -> new PlayerNotFoundException(
                        "No sport record found for this player/sport combination"));
        playerSportRepository.delete(existing);
    }

    // Search with optional sport/skill filters, plus optional distance
    // filtering done in application code (small dataset — fine for now;
    // Day 32/33 revisit this with a proper Haversine SQL query once
    // "nearby" search is the actual focus rather than a bonus filter here).
    public List<PlayerSport> searchPlayers(Long sportId, com.playconnect.entity.SkillLevel skillLevel,
                                            Double latitude, Double longitude, Double radiusKm) {
        List<PlayerSport> results = playerSportRepository.searchPlayers(sportId, skillLevel);

        if (latitude != null && longitude != null && radiusKm != null) {
            results = results.stream()
                    .filter(ps -> withinRadius(latitude, longitude,
                            ps.getUser().getLatitude(), ps.getUser().getLongitude(), radiusKm))
                    .collect(java.util.stream.Collectors.toList());
        }

        return results;
    }

    // Simple flat-earth approximation — good enough for city-scale radius
    // filtering. Day 32 introduces the more accurate Haversine formula.
    private boolean withinRadius(double lat1, double lon1, Double lat2, Double lon2, double radiusKm) {
        if (lat2 == null || lon2 == null) return false;
        return com.playconnect.util.GeoUtils.distanceKm(lat1, lon1, lat2, lon2) <= radiusKm;
    }

    // Dedicated nearby-players lookup: unlike searchPlayers (Day 20),
    // latitude/longitude/radius are required here rather than optional,
    // and results come back sorted closest-first — matching what a
    // "players near me" screen actually needs versus general search.
    public record NearbyResult(PlayerSport playerSport, double distanceKm) {}

    public List<NearbyResult> findNearbyPlayers(Double latitude, Double longitude, Double radiusKm, Long sportId) {
        List<PlayerSport> candidates = playerSportRepository.searchPlayers(sportId, null);

        return candidates.stream()
                .filter(ps -> ps.getUser().getLatitude() != null && ps.getUser().getLongitude() != null)
                .map(ps -> new NearbyResult(ps, com.playconnect.util.GeoUtils.distanceKm(
                        latitude, longitude, ps.getUser().getLatitude(), ps.getUser().getLongitude())))
                .filter(result -> result.distanceKm() <= radiusKm)
                .sorted((a, b) -> Double.compare(a.distanceKm(), b.distanceKm()))
                .collect(java.util.stream.Collectors.toList());
    }
}