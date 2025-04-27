package com.example.kvitter.HashtagTests;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.kvitter.entities.Hashtag;
import com.example.kvitter.mappers.HashtagMapper;
import com.example.kvitter.repos.HashtagRepo;
import com.example.kvitter.services.HashtagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HashtagServiceTests {
    
 
    @Mock
    private HashtagRepo hashtagRepo;
    
    @Mock
    private HashtagMapper hashtagMapper;

    @InjectMocks
    private HashtagService hashtagService;

    @BeforeEach
    void setUp() {
        
    }

    @Test
    void testAddHashtag() {
        String tag = "#test";
        Hashtag hashtag = new Hashtag(tag);
        when(hashtagRepo.save(any(Hashtag.class))).thenReturn(hashtag);

        Hashtag result = hashtagService.addHashTag(tag);

        assertNotNull(result);
        assertEquals(tag, result.getHashtag());
        verify(hashtagRepo, times(1)).save(any(Hashtag.class));
    }
}

