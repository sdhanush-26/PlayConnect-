package com.playconnect.service;

import com.playconnect.entity.Match;
import com.playconnect.entity.Message;
import com.playconnect.entity.User;
import com.playconnect.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final MatchService matchService;
    private final UserService userService;

    @Autowired
    public MessageService(MessageRepository messageRepository, MatchService matchService, UserService userService) {
        this.messageRepository = messageRepository;
        this.matchService = matchService;
        this.userService = userService;
    }

    public Message sendMessage(Long matchId, Long senderId, String content) {
        Match match = matchService.getMatch(matchId);   // 404 if the match doesn't exist
        User sender = userService.getUser(senderId);     // 404 if the sender doesn't exist

        Message message = new Message();
        message.setMatch(match);
        message.setSender(sender);
        message.setContent(content);

        return messageRepository.save(message);
    }

    public List<Message> getMessagesForMatch(Long matchId) {
        matchService.getMatch(matchId); // ensures the match itself exists first
        return messageRepository.findByMatchIdOrderBySentAtAsc(matchId);
    }
}