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
public class DetailedKvitterDto {
    private UUID id;
    private String message;
    private MiniUserDto miniUserDTO;
    private LocalDateTime createdDateAndTime;
    private List<MiniHashtagDto> hashtags = new ArrayList<>();
}
