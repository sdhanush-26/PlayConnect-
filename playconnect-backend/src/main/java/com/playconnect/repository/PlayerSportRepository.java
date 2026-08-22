package com.playconnect.repository;

import com.playconnect.entity.PlayerSport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerSportRepository extends JpaRepository<PlayerSport, Long> {

    // All sports a given user plays — powers the profile page (Day 19).
    List<PlayerSport> findByUserId(Long userId);

    // All players of a given sport — powers player search (Day 20).
    List<PlayerSport> findBySportId(Long sportId);

    // Used to enforce "a user can't have two skill levels for the same
    // sport" at the application level, matching the DB's UNIQUE constraint.
    Optional<PlayerSport> findByUserIdAndSportId(Long userId, Long sportId);

    boolean existsByUserIdAndSportId(Long userId, Long sportId);
}
