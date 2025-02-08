package com.example.kvitter.Hashtag;

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
