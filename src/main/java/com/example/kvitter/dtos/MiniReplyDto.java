package com.example.kvitter.dtos;

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
public class MiniReplyDto {
    private UUID id;
    private String message;
    private LocalDateTime createdDateAndTime;
    private UUID userId;
    private String userName;
    private Boolean isActive;
}
