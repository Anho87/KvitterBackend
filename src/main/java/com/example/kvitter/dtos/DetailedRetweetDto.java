package com.example.kvitter.dtos;

import com.example.kvitter.entities.Kvitter;
import com.example.kvitter.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetailedRetweetDto {
    private UUID id;
    private MiniUserDto user;
    private MiniKvitterDto originalKvitter;
}
