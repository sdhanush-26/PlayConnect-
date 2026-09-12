package com.playconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private Long id;
    private Long matchId;
    private Long senderId;
    private String senderName;
    private String content;
    private LocalDateTime sentAt;
}