package com.example.kvitter.ReplyTests;

import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.entities.Kvitter;
import com.example.kvitter.entities.Reply;
import com.example.kvitter.entities.User;
import com.example.kvitter.repos.KvitterRepo;
import com.example.kvitter.repos.ReplyRepo;
import com.example.kvitter.repos.UserRepo;
import com.example.kvitter.services.AuthService;
import com.example.kvitter.services.ReplyService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class ReplyServiceTests {


    @Mock
    private ReplyRepo replyRepo;
    @Mock
    private UserRepo userRepo;
    @Mock
    private KvitterRepo kvitterRepo;
    @Mock
    private AuthService authService;
    @InjectMocks
    private ReplyService replyService;

    private final UUID userId = UUID.randomUUID();
    private final UUID kvitterId = UUID.randomUUID();
    private final UUID replyId = UUID.randomUUID();

    private User user;
    private Kvitter kvitter;
    private Reply parentReply;
    private DetailedUserDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(userId);

        kvitter = new Kvitter();
        kvitter.setId(kvitterId);

        parentReply = new Reply();
        parentReply.setId(replyId);
        parentReply.setReplies(new ArrayList<>());

        userDto = new DetailedUserDto();
        userDto.setId(userId);
        
    }

    @Test
    void testAddReply_ToKvitter() {
        String token = "Bearer faketoken";
        when(authService.getUserFromToken(token)).thenReturn(userDto);
        
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(kvitterRepo.findById(kvitterId)).thenReturn(Optional.of(kvitter));

        replyService.addReply("This is a reply", kvitterId, null, token);

        verify(replyRepo, times(1)).save(any(Reply.class));
    }

    @Test
    void testAddReply_ToParentReply() {
        String token = "Bearer faketoken";
        when(authService.getUserFromToken(token)).thenReturn(userDto);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(replyRepo.findById(replyId)).thenReturn(Optional.of(parentReply));

        replyService.addReply("Nested reply", null, replyId, token);

        verify(replyRepo).save(argThat(reply -> reply.getParentReply() != null && reply.getParentReply().getId().equals(replyId)));
    }

    @Test
    void testAddReply_UserNotFound() {
        String token = "Bearer faketoken";
        when(authService.getUserFromToken(token)).thenReturn(userDto);
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                replyService.addReply("Reply text", kvitterId, null, token));
    }

    @Test
    void testAddReply_KvitterNotFound() {
        String token = "Bearer faketoken";
        when(authService.getUserFromToken(token)).thenReturn(userDto);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(kvitterRepo.findById(kvitterId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                replyService.addReply("Reply text", kvitterId, null, token));
    }

    @Test
    void testAddReply_ParentReplyNotFound() {
        String token = "Bearer faketoken";
        when(authService.getUserFromToken(token)).thenReturn(userDto);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(replyRepo.findById(replyId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                replyService.addReply("Reply text", null, replyId, token));
    }

    @Test
    void testRemoveReply_NoReplies_Delete() {
        Reply reply = new Reply();
        reply.setId(replyId);
        reply.setReplies(Collections.emptyList());

        when(replyRepo.findById(replyId)).thenReturn(Optional.of(reply));

        replyService.removeReply(replyId.toString());

        verify(replyRepo).deleteReplyById(replyId);
    }

    @Test
    void testRemoveReply_WithReplies_SoftDelete() {
        Reply reply = new Reply();
        reply.setId(replyId);
        reply.setMessage("Original Message");
        reply.setReplies(List.of(new Reply())); 
        reply.setIsActive(true);

        when(replyRepo.findById(replyId)).thenReturn(Optional.of(reply));

        replyService.removeReply(replyId.toString());

        assertFalse(reply.getIsActive());
        assertEquals("Deleted...", reply.getMessage());
        verify(replyRepo).save(reply);
    }

    @Test
    void testRemoveReply_NotFound() {
        when(replyRepo.findById(replyId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                replyService.removeReply(replyId.toString()));
    }
}
