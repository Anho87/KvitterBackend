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
public class DetailedRekvittDto implements DetailedDtoInterface{
    private UUID id;
    private MiniUserDto user;
    private MiniKvitterDto originalKvitter;
    private LocalDateTime createdDateAndTime;

    @Override
    public LocalDateTime getCreatedDateAndTime() {
        return this.createdDateAndTime;
    }
}
