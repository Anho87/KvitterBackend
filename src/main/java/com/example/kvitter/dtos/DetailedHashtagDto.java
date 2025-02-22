package com.example.kvitter.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetailedHashtagDto {
    private UUID id;
    private String hashtag;
    private List<MiniKvitterDto> kvitters;
}
