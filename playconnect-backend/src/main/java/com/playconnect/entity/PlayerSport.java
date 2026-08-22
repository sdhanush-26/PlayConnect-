package com.playconnect.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Maps to the `player_sport` table (Day 6/7). This is the join table
 * that lets a User play multiple Sports, each at its own skill level.
 *
 * Uses @ManyToOne on both sides rather than a raw userId/sportId pair
 * so Hibernate can load the related User/Sport objects directly when
 * needed (e.g. showing a player's name alongside their sport list),
 * without a separate manual lookup.
 */
@Entity
@Table(name = "player_sport",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "sport_id"}))
@Data
@NoArgsConstructor
public class PlayerSport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_id", nullable = false)
    private Sport sport;

    @Enumerated(EnumType.STRING)
    @Column(name = "skill_level", nullable = false)
    private SkillLevel skillLevel;
}
