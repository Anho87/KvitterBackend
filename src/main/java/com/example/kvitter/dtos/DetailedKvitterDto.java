package com.example.kvitter.dtos;

import com.example.kvitter.entities.Retweet;
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
public class DetailedKvitterDto {
    private UUID id;
    private String message;
    private MiniUserDto user;
    private LocalDateTime createdDateAndTime;
    private List<MiniHashtagDto> hashtags = new ArrayList<>();
    private boolean isPrivate;
    private List<MiniLikeDto> likes = new ArrayList<>();
    private List<MiniReplyDto> replies = new ArrayList<>();
    private List<MiniRetweetDto> retweets = new ArrayList<>();
}
