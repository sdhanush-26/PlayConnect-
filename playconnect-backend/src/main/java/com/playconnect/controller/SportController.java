package com.playconnect.controller;

import com.playconnect.entity.Sport;
import com.playconnect.service.SportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Test in Postman:
 *   POST   http://localhost:8080/api/sports    body: {"name": "Cricket"}
 *   GET    http://localhost:8080/api/sports
 *   GET    http://localhost:8080/api/sports/1
 *   PUT    http://localhost:8080/api/sports/1  body: {"name": "Updated Name"}
 *   DELETE http://localhost:8080/api/sports/1
 */
@RestController
@RequestMapping("/api/sports")
public class SportController {

    private final SportService sportService;

    @Autowired
    public SportController(SportService sportService) {
        this.sportService = sportService;
    }

    @PostMapping
    public ResponseEntity<Sport> createSport(@RequestBody Sport sport) {
        Sport created = sportService.createSport(sport);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Sport>> getAllSports() {
        return ResponseEntity.ok(sportService.getAllSports());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sport> getSport(@PathVariable Long id) {
        return ResponseEntity.ok(sportService.getSport(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sport> updateSport(@PathVariable Long id, @RequestBody Sport sport) {
        return ResponseEntity.ok(sportService.updateSport(id, sport));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSport(@PathVariable Long id) {
        sportService.deleteSport(id);
        return ResponseEntity.noContent().build();
    }
}
