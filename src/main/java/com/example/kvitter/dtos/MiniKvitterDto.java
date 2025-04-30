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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MiniKvitterDto {
    
    private UUID id;
    private String message;
    private MiniUserDto user;
    private LocalDateTime createdDateAndTime;
    private List<MiniHashtagDto> hashtags = new ArrayList<>();
    private Boolean isPrivate;
    private Boolean isActive;
    private List<MiniUserDto> likes = new ArrayList<>();
    private Boolean isFollowing;
    private Boolean isLiked;
   
}
