package com.playconnect.controller;

import com.playconnect.dto.MessageResponse;
import com.playconnect.entity.Message;
import com.playconnect.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * WebSocket side of chat. A client sends a message to
 * /app/chat/{matchId}, this saves it to the database (so it's part of
 * the permanent history REST clients can load), then broadcasts it to
 * everyone currently subscribed to /topic/match/{matchId}.
 *
 * Uses SimpMessagingTemplate rather than @SendTo because the
 * destination needs the matchId baked in dynamically — @SendTo only
 * supports a fixed destination string, not one built from a path variable.
 */
@Controller
public class ChatWebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ChatWebSocketController(MessageService messageService, SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }

    // Incoming payload shape: {"senderId": 1, "content": "hello!"}
    public record ChatMessageRequest(Long senderId, String content) {}

    @MessageMapping("/chat/{matchId}")
    public void sendMessage(@DestinationVariable Long matchId, ChatMessageRequest request) {
        Message saved = messageService.sendMessage(matchId, request.senderId(), request.content());

        MessageResponse response = new MessageResponse(
                saved.getId(), matchId, saved.getSender().getId(),
                saved.getSender().getName(), saved.getContent(), saved.getSentAt());

        messagingTemplate.convertAndSend("/topic/match/" + matchId, response);
    }
}