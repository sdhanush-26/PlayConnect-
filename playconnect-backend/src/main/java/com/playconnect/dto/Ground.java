package com.playconnect.dto;

import com.playconnect.entity.Sport;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Maps to the `grounds` table (Day 6/7). A physical venue — e.g. a
 * specific cricket ground or badminton court — with its own real
 * coordinates. This is the proper fix for what Days 22/34 flagged:
 * Match previously only had a free-text `location` field and borrowed
 * the creator's coordinates for "nearby matches" search as a stand-in.
 */
@Entity
@Table(name = "grounds")
@Data
@NoArgsConstructor
public class Ground {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 255)
    private String location;

    private Double latitude;

    private Double longitude;

    // Primary sport this ground supports — nullable since a ground could
    // theoretically host multiple sports; kept simple per the Day 7 schema.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sport_id")
    private Sport sport;
}
