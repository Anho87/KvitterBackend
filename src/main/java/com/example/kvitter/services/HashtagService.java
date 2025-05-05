package com.example.kvitter.services;

import com.example.kvitter.dtos.MiniHashtagDto;
import com.example.kvitter.entities.Hashtag;
import com.example.kvitter.mappers.HashtagMapper;
import com.example.kvitter.repos.HashtagRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HashtagService {
    
    private final HashtagRepo hashtagRepo;
    private final HashtagMapper hashtagMapper;
    private final AuthService authService;



    public Hashtag addHashTag(String hashtag) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Stockholm"));
        Hashtag newHashtag = new Hashtag(hashtag,now.toLocalDateTime());
        return hashtagRepo.save(newHashtag);
    }
    
   
    public List<MiniHashtagDto> getTrendingHashtags(String token){
        authService.getUserFromToken(token);
        return mapHashtags(hashtagRepo.getFiveLastHashTags());
    }
    
    public void removeHashtag(UUID hashtagId){
        hashtagRepo.deleteHashtagById(hashtagId);
    }
    
    private List<MiniHashtagDto> mapHashtags(List<Hashtag> hashtags){
        return hashtags.stream().map(hashtagMapper::hashtagToMiniHashtagDto).collect(Collectors.toList());
    }
}
