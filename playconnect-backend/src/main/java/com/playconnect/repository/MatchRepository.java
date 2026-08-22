package com.playconnect.repository;

import com.playconnect.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    // Matches created by a specific user — powers "my matches" (Day 29).
    List<Match> findByCreatorId(Long creatorId);

    // Matches for a specific sport — useful for browsing/filtering (Day 24).
    List<Match> findBySportId(Long sportId);
}
