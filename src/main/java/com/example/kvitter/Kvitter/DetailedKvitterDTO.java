package com.example.kvitter.Kvitter;

import com.example.kvitter.Hashtag.Hashtag;
import com.example.kvitter.Hashtag.MiniHashtagDTO;
import com.example.kvitter.User.MiniUserDTO;
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
public class DetailedKvitterDTO {
    private UUID id;
    private String message;
    private MiniUserDTO miniUserDTO;
    private LocalDateTime createdDateAndTime;
    private List<MiniHashtagDTO> hashtags = new ArrayList<>();
}
