package com.playconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.playconnect.dto.Ground;

import java.util.List;

@Repository
public interface GroundRepository extends JpaRepository<Ground, Long> {

    List<Ground> findBySportId(Long sportId);
}
