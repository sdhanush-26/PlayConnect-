package com.playconnect.service;

import com.playconnect.entity.Match;
import com.playconnect.entity.MatchStatus;
import com.playconnect.entity.Sport;
import com.playconnect.entity.User;
import com.playconnect.exception.InvalidMatchException;
import com.playconnect.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final UserService userService;
    private final SportService sportService;

    @Autowired
    public MatchService(MatchRepository matchRepository, UserService userService, SportService sportService) {
        this.matchRepository = matchRepository;
        this.userService = userService;
        this.sportService = sportService;
    }

    public Match createMatch(String title, Long creatorId, Long sportId, String location,
                              LocalDate matchDate, LocalTime startTime, LocalTime endTime,
                              Integer maxPlayers) {

        if (!endTime.isAfter(startTime)) {
            throw new InvalidMatchException("endTime must be after startTime");
        }

        User creator = userService.getUser(creatorId);   // 404 if missing
        Sport sport = sportService.getSport(sportId);     // 404 if missing

        Match match = new Match();
        match.setTitle(title);
        match.setCreator(creator);
        match.setSport(sport);
        match.setLocation(location);
        match.setMatchDate(matchDate);
        match.setStartTime(startTime);
        match.setEndTime(endTime);
        match.setMaxPlayers(maxPlayers);
        match.setStatus(MatchStatus.OPEN);

        return matchRepository.save(match);
    }
}
