package com.example.kvitter.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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
    private boolean isPrivate;
//    private boolean isActive;
   
}
