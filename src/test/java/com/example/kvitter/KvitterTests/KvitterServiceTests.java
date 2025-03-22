package com.example.kvitter.KvitterTests;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.kvitter.configs.UserAuthProvider;
import com.example.kvitter.entities.Kvitter;
import com.example.kvitter.entities.User;
import com.example.kvitter.mappers.KvitterMapper;
import com.example.kvitter.repos.KvitterRepo;
import com.example.kvitter.repos.UserRepo;
import com.example.kvitter.services.KvitterService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class KvitterServiceTests {

    @Mock
    private KvitterRepo kvitterRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private KvitterMapper kvitterMapper;
    @InjectMocks
    private KvitterService kvitterService;
    
    @Mock
    private UserAuthProvider userAuthProvider;

    @BeforeEach
    void setUp() {
        kvitterService = new KvitterService(kvitterRepo, userRepo, kvitterMapper, userMapper, userAuthProvider);
    }

    @Test
    void testAddKvitter() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        boolean isPrivate = false;
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        Kvitter kvitter = Kvitter.builder()
                .message("Test message")
                .user(user)
                .createdDateAndTime(LocalDateTime.now())
                .hashtags(new ArrayList<>())
                .isPrivate(isPrivate)
                .build();
        when(kvitterRepo.save(any(Kvitter.class))).thenReturn(kvitter);

        assertDoesNotThrow(() -> kvitterService.addKvitter("Test message", userId, new ArrayList<>(), isPrivate));
        verify(kvitterRepo, times(1)).save(any(Kvitter.class));
    }

    @Test
    void testRemoveKvitter() {
        UUID kvitterId = UUID.randomUUID();
        Kvitter kvitter = Kvitter.builder().id(kvitterId).hashtags(new ArrayList<>()).build();
        when(kvitterRepo.findById(kvitterId)).thenReturn(Optional.of(kvitter));

        kvitterService.removeKvitter(kvitterId.toString());

        verify(kvitterRepo, times(1)).deleteKvitterById(kvitterId);
    }

    @Test
    void testRemoveKvitterNotFound() {
        UUID kvitterId = UUID.randomUUID();
        when(kvitterRepo.findById(kvitterId)).thenReturn(Optional.empty());
        
        assertThrows(EntityNotFoundException.class, () -> kvitterService.removeKvitter(kvitterId.toString()));
    }

//    @Test
//    void testGetAllDetailedKvittersDTO() {
//        Kvitter kvitter = Kvitter.builder()
//                .id(UUID.randomUUID())
//                .message("Test message")
//                .createdDateAndTime(LocalDateTime.now())
//                .hashtags(new ArrayList<>())
//                .build();
//        List<Kvitter> kvitters = List.of(kvitter);
//        when(kvitterRepo.findAll()).thenReturn(kvitters);
//        
//        List<DetailedKvitterDto> result = kvitterService.getAllDetailedKvittersDTO();
//        
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        verify(kvitterRepo, times(1)).findAll();
//    }
}
