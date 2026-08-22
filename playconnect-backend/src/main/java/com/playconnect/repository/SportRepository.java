package com.playconnect.repository;

import com.playconnect.entity.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SportRepository extends JpaRepository<Sport, Long> {

    // Used to prevent duplicate sport names, and to look up a sport
    // by name when seeding data or validating a match creation request.
    Optional<Sport> findByName(String name);

    boolean existsByName(String name);
}
