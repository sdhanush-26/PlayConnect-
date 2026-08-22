package com.playconnect.service;

import com.playconnect.entity.Sport;
import com.playconnect.exception.PlayerNotFoundException;
import com.playconnect.repository.SportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SportService {

    private final SportRepository sportRepository;

    @Autowired
    public SportService(SportRepository sportRepository) {
        this.sportRepository = sportRepository;
    }

    public Sport createSport(Sport sport) {
        if (sportRepository.existsByName(sport.getName())) {
            throw new IllegalArgumentException(
                    "A sport named " + sport.getName() + " already exists");
        }
        return sportRepository.save(sport);
    }

    public List<Sport> getAllSports() {
        return sportRepository.findAll();
    }

    public Sport getSport(Long id) {
        return sportRepository.findById(id)
                // Reusing PlayerNotFoundException here rather than creating
                // a near-identical SportNotFoundException — both represent
                // the same "lookup by id failed" case, and the message
                // still clearly states what wasn't found.
                .orElseThrow(() -> new PlayerNotFoundException(
                        "Sport not found with id: " + id));
    }

    public Sport updateSport(Long id, Sport updatedSport) {
        Sport existing = getSport(id);
        existing.setName(updatedSport.getName());
        return sportRepository.save(existing);
    }

    public void deleteSport(Long id) {
        if (!sportRepository.existsById(id)) {
            throw new PlayerNotFoundException("Sport not found with id: " + id);
        }
        sportRepository.deleteById(id);
    }
}
