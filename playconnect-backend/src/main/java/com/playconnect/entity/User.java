package com.playconnect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Maps to the `users` table (already created in Day 6/7's schema.sql).
 * Hibernate reconciles this class against that existing table on startup
 * because application.properties has spring.jpa.hibernate.ddl-auto=update.
 */
@Entity
@Table(name = "users")
@Data                 // Lombok: generates getters, setters, toString, equals/hashCode
@NoArgsConstructor     // JPA requires a no-args constructor
@AllArgsConstructor    // convenient for tests / quick object creation
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    // Stores a BCrypt hash once Spring Security is wired in (Day 39).
    // Never store plain-text passwords here.
    @Column(nullable = false)
    private String password;

    @Column(length = 20)
    private String phone;

    private Double latitude;

    private Double longitude;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Set automatically right before the row is first inserted —
    // matches the DEFAULT CURRENT_TIMESTAMP behavior from the SQL schema,
    // but explicit here so it works the same regardless of DB defaults.
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
