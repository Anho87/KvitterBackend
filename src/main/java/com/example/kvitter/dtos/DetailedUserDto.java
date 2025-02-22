package com.example.kvitter.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetailedUserDto {
    private UUID id;
    private String email;
    private String userName;
    private List<MiniKvitterDto> kvitterList;
    private String token;
    
}
