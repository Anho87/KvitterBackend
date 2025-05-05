package com.example.kvitter.HashtagTests;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.dtos.MiniHashtagDto;
import com.example.kvitter.entities.Hashtag;
import com.example.kvitter.entities.User;
import com.example.kvitter.mappers.HashtagMapper;
import com.example.kvitter.repos.HashtagRepo;
import com.example.kvitter.services.AuthService;
import com.example.kvitter.services.HashtagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

class HashtagServiceTests {
    
 
    @Mock
    private HashtagRepo hashtagRepo;
    
    @Mock
    private HashtagMapper hashtagMapper;
    @Mock
    private AuthService authService;

    @InjectMocks
    private HashtagService hashtagService;

    private User user;
    private DetailedUserDto detailedUserDto;
    
    private Hashtag hashtag;
    
    private MiniHashtagDto miniHashtagDto;
    
    private String tag;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setUserName("testuser");

        detailedUserDto = new DetailedUserDto();
        detailedUserDto.setId(user.getId());
        detailedUserDto.setEmail(user.getEmail());
        detailedUserDto.setUserName(user.getUserName());

        tag = "#test";
        hashtag = new Hashtag(tag, LocalDateTime.now());
        
        miniHashtagDto = new MiniHashtagDto();
        miniHashtagDto.setHashtag(hashtag.getHashtag());
    }

    @Test
    void testAddHashtag() {
        hashtagService.addHashTag(tag);
        
        verify(hashtagRepo, times(1)).save(any(Hashtag.class));
    }

    @Test
    void testGetTrendingHashtags() {
        String token = "Bearer faketoken";
        when(authService.getUserFromToken(token)).thenReturn(detailedUserDto);
        when(hashtagRepo.getFiveLastHashTags()).thenReturn(List.of(hashtag));
        when(hashtagMapper.hashtagToMiniHashtagDto(hashtag)).thenReturn(miniHashtagDto);

        List<MiniHashtagDto> result = hashtagService.getTrendingHashtags(token);

        assertEquals(1, result.size());
        assertEquals(result.get(0).getHashtag(), "#test");
        verify(hashtagRepo).getFiveLastHashTags();
        verify(hashtagMapper).hashtagToMiniHashtagDto(hashtag);
    }


    @Test
    void testRemoveHashtag() {
        UUID id = UUID.randomUUID();

        hashtagService.removeHashtag(id);

        verify(hashtagRepo, times(1)).deleteHashtagById(id);
    }
}

