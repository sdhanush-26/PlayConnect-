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
}
