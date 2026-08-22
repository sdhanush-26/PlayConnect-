package com.playconnect.dto;

import com.playconnect.entity.JoinStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchPlayerResponse {
    private Long id;
    private Long userId;
    private String userName;
    private JoinStatus joinStatus;
}
