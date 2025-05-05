package com.example.kvitter.HashtagTests;

import com.example.kvitter.configs.UserAuthProvider;
import com.example.kvitter.controllers.HashtagController;
import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.dtos.MiniHashtagDto;
import com.example.kvitter.services.HashtagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class HashtagControllerTest {

    @Mock
    private HashtagService hashtagService;

    @Mock
    private UserAuthProvider userAuthProvider;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private HashtagController hashtagController;

    private DetailedUserDto detailedUserDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        detailedUserDto = new DetailedUserDto();
        detailedUserDto.setId(UUID.randomUUID());
        detailedUserDto.setEmail("test@example.com");
        detailedUserDto.setUserName("testuser");
    }

//    @Test
//    void testGetTrendingHashtags() {
//        String token = "Bearer faketoken";
//
//        when(userAuthProvider.validateToken("faketoken")).thenReturn(authentication);
//        when(authentication.getPrincipal()).thenReturn(detailedUserDto);
//        when(hashtagService.getTrendingHashtags(token)).thenReturn(List.of());
//
//        List<MiniHashtagDto> result = hashtagController.getTrendingHashtags(token);
//
//        assertThat(result).isNotNull();
//        verify(hashtagService).getTrendingHashtags();
//    }
}
