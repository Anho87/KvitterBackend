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
public class DetailedKvitterDto implements DetailedDtoInterface{
    private UUID id;
    private String message;
    private MiniUserDto user;
    private LocalDateTime createdDateAndTime;
    private List<MiniHashtagDto> hashtags = new ArrayList<>();
    private boolean isPrivate;
//    private boolean isActive;
    private List<MiniLikeDto> likes = new ArrayList<>();
    private List<DetailedReplyDto> replies = new ArrayList<>();
    private List<MiniRekvittDto> rekvitts = new ArrayList<>();
}
