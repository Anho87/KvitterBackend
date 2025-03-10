package com.example.kvitter.KvitterTests;

import com.example.kvitter.entities.Hashtag;
import com.example.kvitter.entities.Kvitter;
import com.example.kvitter.repos.KvitterRepo;
import com.example.kvitter.services.KvitterService;
import com.example.kvitter.entities.User;
import com.example.kvitter.repos.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KvitterServiceTests {

    @Mock
    private KvitterRepo kvitterRepo;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private KvitterService kvitterService;

    private final UUID userId = UUID.randomUUID();
    private final String userEmail = "mario.bros@nintendo.com";
    private final String userPassword = "itsame123";
    private final String userName = "Mario";
    private final User user = User.builder()
            .id(userId)
            .email(userEmail)
            .password(userPassword)
            .userName(userName)
            .build();

    private final String message = "Its'a me Mario!";
    
    List<Hashtag> hashtags = new ArrayList<>();

    @Test
    void addKvitterTest_success() {
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        kvitterService.addKvitter(message, user.getId(),hashtags);
        ArgumentCaptor<Kvitter> kvitterCaptor = ArgumentCaptor.forClass(Kvitter.class);
        verify(kvitterRepo).save(kvitterCaptor.capture());
        Kvitter capturedKvitter = kvitterCaptor.getValue();
        assertEquals(message, capturedKvitter.getMessage());
        assertEquals(user, capturedKvitter.getUser());
    }

    @Test
    void addKvitterTest_userNotFound() {
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            kvitterService.addKvitter(message, userId,hashtags);
        });
        assertEquals("User not found", exception.getMessage());
        verify(kvitterRepo, never()).save(any(Kvitter.class));
    }
}

