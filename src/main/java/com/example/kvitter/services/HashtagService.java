package com.example.kvitter.services;

import com.example.kvitter.entities.Hashtag;
import com.example.kvitter.repos.HashtagRepo;
import org.springframework.stereotype.Service;

@Service
public class HashtagService {
    
    private final HashtagRepo hashtagRepo;

    public HashtagService(HashtagRepo hashtagRepo) {
        this.hashtagRepo = hashtagRepo;
    }

    public Hashtag addHashTag(String hashtag) {
        Hashtag newHashtag = new Hashtag(hashtag);
        return hashtagRepo.save(newHashtag);
    }
}
