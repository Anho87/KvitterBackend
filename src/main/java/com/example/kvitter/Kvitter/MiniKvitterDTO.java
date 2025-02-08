package com.example.kvitter.Kvitter;


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
public class MiniKvitterDTO {
    
    private UUID id;
    private String message;
    private LocalDateTime createdDateAndTime;
   
}
