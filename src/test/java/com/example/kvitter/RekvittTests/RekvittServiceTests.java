package com.example.kvitter.RekvittTests;

import com.example.kvitter.dtos.DetailedRekvittDto;
import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.dtos.MiniKvitterDto;
import com.example.kvitter.dtos.MiniUserDto;
import com.example.kvitter.entities.Kvitter;
import com.example.kvitter.entities.Rekvitt;
import com.example.kvitter.entities.User;
import com.example.kvitter.exceptions.AppException;
import com.example.kvitter.mappers.RekvittMapper;
import com.example.kvitter.mappers.UserMapper;
import com.example.kvitter.repos.KvitterRepo;
import com.example.kvitter.repos.RekvittRepo;
import com.example.kvitter.repos.UserRepo;
import com.example.kvitter.services.RekvittService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class RekvittServiceTests {

    @Mock
    private RekvittRepo rekvittRepo;
    @Mock
    private KvitterRepo kvitterRepo;
    @Mock
    private RekvittMapper rekvittMapper;
    @Mock
    private UserRepo userRepo;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private RekvittService rekvittService;

    private final UUID kvitterId = UUID.randomUUID();
    private final UUID rekvittId = UUID.randomUUID();
    private User user;
    private MiniUserDto miniUserDto;
    private Kvitter kvitter;
    private MiniKvitterDto miniKvitterDto;
    private Rekvitt rekvitt;
    private DetailedUserDto detailedUserDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setEmail("test@example.com");
        user.setRekvitts(new ArrayList<>());
        user.setId(UUID.randomUUID());
        
        miniUserDto = new MiniUserDto();
        miniUserDto.setEmail(user.getEmail());
        miniUserDto.setId(user.getId());

        kvitter = new Kvitter();
        kvitter.setId(kvitterId);
        kvitter.setUser(new User());
        
        miniKvitterDto = new MiniKvitterDto();
        miniKvitterDto.setId(kvitterId);
        miniKvitterDto.setUser(miniUserDto);
        

        rekvitt = new Rekvitt();
        rekvitt.setId(rekvittId);
        rekvitt.setUser(user);
        rekvitt.setOriginalKvitter(kvitter);
        rekvitt.setCreatedDateAndTime(LocalDateTime.now());

        detailedUserDto = new DetailedUserDto();
        detailedUserDto.setEmail("test@example.com");
    }

    @Test
    void testAddRekvitt_Success() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(user);
        when(kvitterRepo.findById(kvitterId)).thenReturn(Optional.of(kvitter));

        rekvittService.addRekvitt(kvitterId.toString(), detailedUserDto);

        verify(rekvittRepo, times(1)).save(any(Rekvitt.class));
    }

    @Test
    void testAddRekvitt_AlreadyExists() {
        Rekvitt existing = new Rekvitt();
        existing.setOriginalKvitter(kvitter);
        user.getRekvitts().add(existing);

        when(userRepo.findByEmail(user.getEmail())).thenReturn(user);
        when(kvitterRepo.findById(kvitterId)).thenReturn(Optional.of(kvitter));

        AppException ex = assertThrows(AppException.class, () ->
                rekvittService.addRekvitt(kvitterId.toString(), detailedUserDto));
        assertEquals("User already rekvitted this kvitter", ex.getMessage());
    }

    @Test
    void testRemoveRekvitt_Success() {
        when(rekvittRepo.findById(rekvittId)).thenReturn(Optional.of(rekvitt));

        rekvittService.removeRekvitt(rekvittId.toString());

        verify(rekvittRepo, times(1)).deleteRekvittByById(rekvittId);
    }

    @Test
    void testRemoveRekvitt_NotFound() {
        when(rekvittRepo.findById(rekvittId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                rekvittService.removeRekvitt(rekvittId.toString()));
    }

    @Test
    void testGetFilteredRekvitts_caseUserInfo() {
        User targetUser = new User();
        targetUser.setId(UUID.randomUUID());

        when(userRepo.findByEmail(user.getEmail())).thenReturn(user);
        when(userRepo.findByUserName("someoneElse")).thenReturn(Optional.of(targetUser));
        when(userMapper.optionalToUser(Optional.of(targetUser))).thenReturn(targetUser);
        when(rekvittRepo.findAllByUserId(targetUser.getId())).thenReturn(List.of(rekvitt));

        DetailedRekvittDto dto = new DetailedRekvittDto();
        dto.setOriginalKvitter(miniKvitterDto);
        when(rekvittMapper.rekvittToDetailedRekvittDto(rekvitt)).thenReturn(dto);

        var result = rekvittService.getFilteredRekvitts("User-Info", "someoneElse", detailedUserDto);

        assertEquals(1, result.size());
        verify(rekvittRepo).findAllByUserId(targetUser.getId());
    }

    @Test
    void testGetFilteredRekvitts_caseFollowing() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(user);
        when(rekvittRepo.findAllRekvittsByUserFollows(user.getId())).thenReturn(List.of(rekvitt));

        DetailedRekvittDto dto = new DetailedRekvittDto();
        dto.setOriginalKvitter(miniKvitterDto);
        when(rekvittMapper.rekvittToDetailedRekvittDto(rekvitt)).thenReturn(dto);

        var result = rekvittService.getFilteredRekvitts("Following", null, detailedUserDto);

        assertEquals(1, result.size());
        verify(rekvittRepo).findAllRekvittsByUserFollows(user.getId());
    }

    @Test
    void testGetFilteredRekvitts_caseMyActivity() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(user);
        when(rekvittRepo.findAllByUserId(user.getId())).thenReturn(List.of(rekvitt));

        DetailedRekvittDto dto = new DetailedRekvittDto();
        dto.setOriginalKvitter(miniKvitterDto);
        when(rekvittMapper.rekvittToDetailedRekvittDto(rekvitt)).thenReturn(dto);

        var result = rekvittService.getFilteredRekvitts("MyActivity", null, detailedUserDto);

        assertEquals(1, result.size());
        verify(rekvittRepo).findAllByUserId(user.getId());
    }

    @Test
    void testGetFilteredRekvitts_caseUserInfo_userNotFound() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(user);
        when(userRepo.findByUserName("nonexistent")).thenReturn(Optional.empty());

        var result = rekvittService.getFilteredRekvitts("user-info", "nonexistent", detailedUserDto);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(rekvittRepo, never()).findAllByUserId(any());
    }
}
