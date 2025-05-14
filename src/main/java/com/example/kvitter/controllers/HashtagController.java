package com.example.kvitter.controllers;


import com.example.kvitter.dtos.MiniHashtagDto;
import com.example.kvitter.services.HashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HashtagController {
    
    private final HashtagService hashtagService;
    
    @GetMapping("/trendingHashtags")
    public List<MiniHashtagDto> getTrendingHashtags(){
        return hashtagService.getTrendingHashtags();
    }
}
