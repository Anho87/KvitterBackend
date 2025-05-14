package com.example.kvitter.UserTests;

import com.example.kvitter.controllers.UserController;
import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.dtos.FollowUserRequestDto;
import com.example.kvitter.dtos.MiniUserDto;
import com.example.kvitter.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

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
    void testGetUserInfo(){
        String userName = detailedUserDto.getUserName();
        
        when(userService.getUserInfo(userName)).thenReturn(detailedUserDto);
        DetailedUserDto result = userController.getUserInfo(userName);
        
        assertThat(result).isEqualTo(detailedUserDto);
    }
    @Test
    void testGetUserFollowing(){
        String token = "Bearer faketoken";
        List<MiniUserDto> mockResult = new ArrayList<>();
        when(userService.getUserFollowing(token)).thenReturn(mockResult);
        
        List<MiniUserDto> result = userController.getUserFollowing(token);
        
        assertThat(result).isEqualTo(mockResult);
        verify(userService).getUserFollowing(token);
    }
    @Test
    void testFollowUser() {
        FollowUserRequestDto request = new FollowUserRequestDto("targetuser@example.com");
        String token = "Bearer faketoken";
        

        userController.followUser(request, token);
        verify(userService).followUser(eq("targetuser@example.com"), eq(token));
    }

    @Test
    void testUnFollowUser() {
        var request = new com.example.kvitter.dtos.UnFollowUserRequestDto("targetuser@example.com");
        String token = "Bearer faketoken";
        

        userController.unFollowUser(request, token);

        verify(userService).unFollowUser(eq("targetuser@example.com"), eq(token));
    }

    @Test
    void testUpvoteKvitter() {
        var request = new com.example.kvitter.dtos.UpvoteKvitterRequestDto("kvitterId123");
        String token = "Bearer faketoken";
        
        userController.upvoteKvitter(request, token);

        verify(userService).upvoteKvitter(eq("kvitterId123"), eq(token));
    }

    @Test
    void testRemoveUpvoteOnKvitter() {
        var request = new com.example.kvitter.dtos.RemoveUpvoteOnKvitterRequestDto("kvitterId123");
        String token = "Bearer faketoken";
        
        userController.removeUpvoteOnKvitter(request, token);

        verify(userService).removeUpvoteOnKvitter(eq("kvitterId123"), eq(token));
    }
}

