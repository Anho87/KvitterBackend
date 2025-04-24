package com.example.kvitter.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetailedReplyDto {
    private UUID id;
    private String message;
    private LocalDateTime createdDateAndTime;
    private MiniUserDto user;
    private MiniKvitterDto kvitter;
    private MiniReplyDto parentReply;
    private List<DetailedReplyDto> replies = new ArrayList<>();
    private Boolean isFollowing;
    private Boolean isActive;
}
