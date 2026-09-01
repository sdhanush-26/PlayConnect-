package com.playconnect.service;

import com.playconnect.dto.Ground;
import com.playconnect.exception.PlayerNotFoundException;
import com.playconnect.repository.GroundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroundService {

    private final GroundRepository groundRepository;

    @Autowired
    public GroundService(GroundRepository groundRepository) {
        this.groundRepository = groundRepository;
    }

    public Ground createGround(Ground ground) {
        return groundRepository.save(ground);
    }

    public List<Ground> getAllGrounds() {
        return groundRepository.findAll();
    }

    public Ground getGround(Long id) {
        return groundRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException("Ground not found with id: " + id));
    }

    public void deleteGround(Long id) {
        if (!groundRepository.existsById(id)) {
            throw new PlayerNotFoundException("Ground not found with id: " + id);
        }
        groundRepository.deleteById(id);
    }
}