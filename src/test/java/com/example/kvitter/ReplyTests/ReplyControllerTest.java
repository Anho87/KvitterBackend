package com.example.kvitter.ReplyTests;

import com.example.kvitter.configs.UserAuthProvider;
import com.example.kvitter.controllers.ReplyController;
import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.dtos.ReplyRequestDto;
import com.example.kvitter.dtos.RemoveKvitterRequest;
import com.example.kvitter.services.ReplyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import java.util.UUID;

import static org.mockito.Mockito.*;

class ReplyControllerTest {

    @Mock
    private ReplyService replyService;

    @Mock
    private UserAuthProvider userAuthProvider;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ReplyController replyController;

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
    void testPostReply() {
        ReplyRequestDto request = new ReplyRequestDto("Test reply message", UUID.randomUUID(), null);
        String token = "Bearer faketoken";

        when(userAuthProvider.validateTokenStrongly("faketoken")).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(detailedUserDto);

        replyController.postReply(request, token);

        verify(replyService).addReply(
                eq("Test reply message"),
                eq(request.kvitterId()),
                eq(request.parentReplyId()),
                eq(token)
        );
    }

    @Test
    void testRemoveReply() {
        RemoveKvitterRequest request = new RemoveKvitterRequest(UUID.randomUUID().toString());

        replyController.removeReply(request);

        verify(replyService).removeReply(eq(request.id()));
    }
}
