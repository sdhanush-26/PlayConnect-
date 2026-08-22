package com.playconnect.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Maps to the `matches` table (Day 6/7). Ground is intentionally left
 * out for now — the schema supports it, but Ground as an entity/API
 * doesn't arrive until Day 36, so match_date/start_time/end_time plus
 * a plain text description of where covers Days 22-30's needs. Ground
 * gets wired in properly once it exists.
 */
@Entity
@Table(name = "matches")
@Data
@NoArgsConstructor
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_id", nullable = false)
    private Sport sport;

    // Plain text location for now (e.g. "City Cricket Ground, Anantapur")
    // until the Ground entity exists on Day 36 and this can become a
    // proper @ManyToOne relationship instead.
    @Column(length = 255)
    private String location;

    @Column(name = "match_date", nullable = false)
    private LocalDate matchDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "max_players", nullable = false)
    private Integer maxPlayers;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchStatus status = MatchStatus.OPEN;
}
