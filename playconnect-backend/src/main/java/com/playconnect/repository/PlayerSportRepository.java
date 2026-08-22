package com.playconnect.repository;

import com.playconnect.entity.PlayerSport;
import com.playconnect.entity.SkillLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // Flexible search with optional filters. Each ":param IS NULL OR ..."
    // pair means "skip this filter if the caller didn't provide it" —
    // simpler than building a JPA Specification for just two optional
    // conditions, though Specifications are worth learning for cases
    // with many more combinable filters than this.
    @Query("""
            SELECT ps FROM PlayerSport ps
            JOIN FETCH ps.user u
            JOIN FETCH ps.sport s
            WHERE (:sportId IS NULL OR s.id = :sportId)
            AND (:skillLevel IS NULL OR ps.skillLevel = :skillLevel)
            """)
    List<PlayerSport> searchPlayers(@Param("sportId") Long sportId,
                                     @Param("skillLevel") SkillLevel skillLevel);
}