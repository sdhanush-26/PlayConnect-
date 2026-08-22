package com.playconnect.repository;

import com.playconnect.entity.MatchPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchPlayerRepository extends JpaRepository<MatchPlayer, Long> {

    List<MatchPlayer> findByMatchId(Long matchId);

    Optional<MatchPlayer> findByMatchIdAndUserId(Long matchId, Long userId);

    boolean existsByMatchIdAndUserId(Long matchId, Long userId);

    long countByMatchId(Long matchId);
}
