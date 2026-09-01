package com.playconnect.controller;

import com.playconnect.dto.Ground;
import com.playconnect.dto.GroundResponse;
import com.playconnect.service.GroundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Test in Postman:
 *   POST http://localhost:8080/api/grounds
 *   body: {"name": "City Cricket Ground", "location": "Anantapur", "latitude": 14.681, "longitude": 77.6005, "sport": {"id": 1}}
 *   GET  http://localhost:8080/api/grounds
 *   GET  http://localhost:8080/api/grounds/1
 *   DELETE http://localhost:8080/api/grounds/1
 */
@RestController
@RequestMapping("/api/grounds")
public class GroundController {

    private final GroundService groundService;

    @Autowired
    public GroundController(GroundService groundService) {
        this.groundService = groundService;
    }

    private GroundResponse toResponse(Ground ground) {
        return new GroundResponse(
                ground.getId(),
                ground.getName(),
                ground.getLocation(),
                ground.getLatitude(),
                ground.getLongitude(),
                ground.getSport() != null ? ground.getSport().getId() : null,
                ground.getSport() != null ? ground.getSport().getName() : null
        );
    }

    @PostMapping
    public ResponseEntity<GroundResponse> createGround(@RequestBody Ground ground) {
        Ground created = groundService.createGround(ground);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @GetMapping
    public ResponseEntity<List<GroundResponse>> getAllGrounds() {
        List<GroundResponse> responses = groundService.getAllGrounds().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroundResponse> getGround(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(groundService.getGround(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGround(@PathVariable Long id) {
        groundService.deleteGround(id);
        return ResponseEntity.noContent().build();
    }
}