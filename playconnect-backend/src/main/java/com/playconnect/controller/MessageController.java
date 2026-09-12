package com.playconnect.controller;

import com.playconnect.dto.MessageResponse;
import com.playconnect.entity.Message;
import com.playconnect.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST side of chat — loading the message history when a user first
 * opens a match's chat. New messages sent WHILE the chat is open arrive
 * live via WebSocket (ChatWebSocketController) instead.
 */
@RestController
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    private MessageResponse toResponse(Message m) {
        return new MessageResponse(
                m.getId(), m.getMatch().getId(), m.getSender().getId(),
                m.getSender().getName(), m.getContent(), m.getSentAt());
    }

    @GetMapping("/api/matches/{matchId}/messages")
    public ResponseEntity<List<MessageResponse>> getMessages(@PathVariable Long matchId) {
        List<MessageResponse> responses = messageService.getMessagesForMatch(matchId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}