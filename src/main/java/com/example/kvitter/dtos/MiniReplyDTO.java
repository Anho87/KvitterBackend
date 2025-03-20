package com.example.kvitter.dtos;

import com.example.kvitter.entities.Reply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MiniReplyDTO {
    private UUID id;
    private String message;
    private LocalDateTime createdDateAndTime;
    private UUID userId;
    private String userName;
}
