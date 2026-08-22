package com.playconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Maps to the `sports` table (already created in Day 6/7's schema.sql).
 * Deliberately simple — just an id and a unique name. Examples:
 * Cricket, Football, Badminton, Volleyball, Basketball, Tennis, Table Tennis.
 */
@Entity
@Table(name = "sports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;
}
