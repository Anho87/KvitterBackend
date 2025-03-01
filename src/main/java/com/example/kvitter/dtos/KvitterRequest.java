package com.example.kvitter.dtos;

import com.example.kvitter.entities.Hashtag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KvitterRequest {
    private String message;
    private List<Hashtag> hashtags;
}
