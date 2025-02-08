package com.example.kvitter.Hashtag;


import com.example.kvitter.Kvitter.MiniKvitterDTO;
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
public class DetailedHashtagDTO {
    private UUID id;
    private String hashtag;
    private List<MiniKvitterDTO> kvitters;
}
