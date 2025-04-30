package com.example.kvitter.RekvittTests;

import com.example.kvitter.configs.UserAuthProvider;
import com.example.kvitter.controllers.RekvittController;
import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.dtos.RekvittRequestDto;
import com.example.kvitter.dtos.RemoveRekvittRequestDto;
import com.example.kvitter.services.RekvittService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import java.util.UUID;

import static org.mockito.Mockito.*;

class RekvittControllerTest {

    @Mock
    private RekvittService rekvittService;

    @Mock
    private UserAuthProvider userAuthProvider;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private RekvittController rekvittController;

    private DetailedUserDto detailedUserDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        detailedUserDto = new DetailedUserDto();
        detailedUserDto.setId(UUID.randomUUID());
        detailedUserDto.setEmail("test@example.com");
        detailedUserDto.setUserName("testuser");
    }

    @Test
    void testPostRekvitt() {
        RekvittRequestDto request = new RekvittRequestDto(UUID.randomUUID().toString());
        String token = "Bearer faketoken";

        when(userAuthProvider.validateTokenStrongly("faketoken")).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(detailedUserDto);

        rekvittController.postRekvitt(request, token);

        verify(rekvittService).addRekvitt(eq(request.kvitterId()), eq(detailedUserDto));
    }

    @Test
    void testRemoveRekvitt() {
        RemoveRekvittRequestDto request = new RemoveRekvittRequestDto(UUID.randomUUID().toString());
        String token = "Bearer faketoken";

        when(userAuthProvider.validateTokenStrongly("faketoken")).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(detailedUserDto);

        rekvittController.removeRekvitt(request, token);

        verify(rekvittService).removeRekvitt(eq(request.rekvittId()));
    }
}

