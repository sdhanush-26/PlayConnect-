package com.playconnect.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Maps to the `match_players` table (Day 6/7). Tracks who joined which
 * match, and whether the creator has accepted them (Day 27 builds the
 * accept/reject flow — for now everyone auto-joins as PENDING).
 */
@Entity
@Table(name = "match_players",
        uniqueConstraints = @UniqueConstraint(columnNames = {"match_id", "user_id"}))
@Data
@NoArgsConstructor
public class MatchPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "join_status", nullable = false)
    private JoinStatus joinStatus = JoinStatus.PENDING;

    @Column(name = "joined_at", updatable = false)
    private LocalDateTime joinedAt;

    @PrePersist
    protected void onJoin() {
        this.joinedAt = LocalDateTime.now();
    }
}
