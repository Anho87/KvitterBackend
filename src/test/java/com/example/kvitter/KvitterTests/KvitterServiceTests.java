package com.example.kvitter.KvitterTests;

import com.example.kvitter.Kvitter.Kvitter;
import com.example.kvitter.Kvitter.KvitterRepo;
import com.example.kvitter.Kvitter.KvitterService;
import com.example.kvitter.User.User;
import com.example.kvitter.User.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

    private UUID userId = UUID.randomUUID();
    private String userEmail = "mario.bros@nintendo.com";
    private String userPassword = "itsame123";
    private String userFirstName = "Mario";
    private String userLastName = "Bros";
    private User user = User.builder()
            .id(userId)
            .email(userEmail)
            .password(userPassword)
            .firstName(userFirstName)
            .lastName(userLastName)
            .build();

    private String message = "Its'a me Mario!";

    @Test
    void addKvitterTest_success() {
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        kvitterService.addKvitter(message, user.getId());
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
            kvitterService.addKvitter(message, userId);
        });
        assertEquals("User not found", exception.getMessage());
        verify(kvitterRepo, never()).save(any(Kvitter.class));
    }
}

