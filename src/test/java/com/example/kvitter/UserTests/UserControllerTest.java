package com.example.kvitter.UserTests;

import com.example.kvitter.configs.UserAuthProvider;
import com.example.kvitter.controllers.UserController;
import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.dtos.FollowUserRequestDto;
import com.example.kvitter.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserAuthProvider userAuthProvider;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    private DetailedUserDto detailedUserDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        detailedUserDto = new DetailedUserDto();
        detailedUserDto.setId(java.util.UUID.randomUUID());
        detailedUserDto.setEmail("test@example.com");
        detailedUserDto.setUserName("testuser");
    }

    @Test
    void testFollowUser() {
        FollowUserRequestDto request = new FollowUserRequestDto("targetuser@example.com");
        String token = "Bearer faketoken";

        when(userAuthProvider.validateTokenStrongly("faketoken")).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(detailedUserDto);

        userController.followUser(request, token);

        verify(userService).followUser(eq("targetuser@example.com"), eq(detailedUserDto));
    }

    @Test
    void testUnFollowUser() {
        var request = new com.example.kvitter.dtos.UnFollowUserRequestDto("targetuser@example.com");
        String token = "Bearer faketoken";

        when(userAuthProvider.validateTokenStrongly("faketoken")).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(detailedUserDto);

        userController.unFollowUser(request, token);

        verify(userService).unFollowUser(eq("targetuser@example.com"), eq(detailedUserDto));
    }

    @Test
    void testUpvoteKvitter() {
        var request = new com.example.kvitter.dtos.UpvoteKvitterRequestDto("kvitterId123");
        String token = "Bearer faketoken";

        when(userAuthProvider.validateToken("faketoken")).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(detailedUserDto);

        userController.upvoteKvitter(request, token);

        verify(userService).upvoteKvitter(eq("kvitterId123"), eq(detailedUserDto));
    }

    @Test
    void testRemoveUpvoteOnKvitter() {
        var request = new com.example.kvitter.dtos.RemoveUpvoteOnKvitterRequestDto("kvitterId123");
        String token = "Bearer faketoken";

        when(userAuthProvider.validateToken("faketoken")).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(detailedUserDto);

        userController.removeUpvoteOnKvitter(request, token);

        verify(userService).removeUpvoteOnKvitter(eq("kvitterId123"), eq(detailedUserDto));
    }
}

