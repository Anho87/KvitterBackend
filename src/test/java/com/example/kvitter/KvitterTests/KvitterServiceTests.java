package com.example.kvitter.KvitterTests;

import com.example.kvitter.dtos.DetailedDtoInterface;
import com.example.kvitter.dtos.DetailedKvitterDto;
import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.dtos.MiniUserDto;
import com.example.kvitter.entities.Hashtag;
import com.example.kvitter.entities.Kvitter;
import com.example.kvitter.entities.Reply;
import com.example.kvitter.entities.User;
import com.example.kvitter.mappers.KvitterMapper;
import com.example.kvitter.mappers.ReplyMapper;
import com.example.kvitter.mappers.UserMapper;
import com.example.kvitter.repos.KvitterRepo;
import com.example.kvitter.repos.UserRepo;
import com.example.kvitter.services.HashtagService;
import com.example.kvitter.services.KvitterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class KvitterServiceTests {

    @Mock private KvitterRepo kvitterRepo;
    @Mock private UserRepo userRepo;
    @Mock private KvitterMapper kvitterMapper;
    @Mock private HashtagService hashtagService;
    @Mock private UserMapper userMapper;
    @Mock private ReplyMapper replyMapper;

    @InjectMocks private KvitterService kvitterService;

    private User user;
    private Kvitter kvitter;
    private DetailedUserDto detailedUserDto;
    private DetailedKvitterDto detailedKvitterDto;
    
    private MiniUserDto miniUserDto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setUserName("testuser");

        detailedUserDto = new DetailedUserDto();
        detailedUserDto.setId(user.getId());
        detailedUserDto.setEmail(user.getEmail());
        detailedUserDto.setUserName(user.getUserName());
        
        miniUserDto = new MiniUserDto();
        miniUserDto.setId(detailedUserDto.getId());
        miniUserDto.setEmail(detailedUserDto.getEmail());
        miniUserDto.setUserName(detailedUserDto.getUserName());

        kvitter = new Kvitter();
        kvitter.setId(UUID.randomUUID());
        kvitter.setMessage("Hello World");
        kvitter.setUser(user);
        kvitter.setIsActive(true);

        detailedKvitterDto = new DetailedKvitterDto();
        detailedKvitterDto.setId(kvitter.getId());
        detailedKvitterDto.setMessage(kvitter.getMessage());
        detailedKvitterDto.setUser(miniUserDto);
        detailedKvitterDto.setIsActive(kvitter.getIsActive());
        
    }

    @Test
    void testAddKvitter() {
        when(userRepo.findById(any())).thenReturn(Optional.of(user));
        when(hashtagService.addHashTag(anyString())).thenReturn(new Hashtag());

        kvitterService.addKvitter("Hello", List.of("test"), false, detailedUserDto);

        verify(kvitterRepo).save(any(Kvitter.class));
    }

    @Test
    void testRemoveKvitter_DeletesCompletely() {
        kvitter.setReplies(Collections.emptyList());
        kvitter.setRekvitts(Collections.emptyList());
        when(kvitterRepo.findById(any())).thenReturn(Optional.of(kvitter));

        kvitterService.removeKvitter(kvitter.getId().toString());

        verify(kvitterRepo).deleteKvitterById(eq(kvitter.getId()));
    }

    @Test
    void testRemoveKvitter_MarksAsDeleted() {
        kvitter.setReplies(List.of(mock(Reply.class))); 
        when(kvitterRepo.findById(any())).thenReturn(Optional.of(kvitter));

        kvitterService.removeKvitter(kvitter.getId().toString());

        assertFalse(kvitter.getIsActive());
        assertEquals("Deleted...", kvitter.getMessage());
        verify(kvitterRepo).save(kvitter);
    }

    @Test
    void testGetFilteredKvitters_whenTargetUserIsEmpty() {
        List<Kvitter> kvitterList = List.of(kvitter);
        
        when(userRepo.findByEmail("test@example.com")).thenReturn(user);
        when(userRepo.findByUserName("nonexistent")).thenReturn(Optional.empty());
        when(kvitterRepo.getDynamicKvitterList(user.getId())).thenReturn(kvitterList);
        when(kvitterMapper.kvitterToDetailedKvitterDTO(any())).thenReturn(detailedKvitterDto);

        List<DetailedDtoInterface> result = kvitterService.getFilteredKvitters("nonexistent", detailedUserDto);

        assertEquals(1, result.size());
        assertTrue(result.get(0) instanceof DetailedKvitterDto);
    }

    @Test
    void testGetFilteredKvitters_whenTargetUserIsDifferent() {
        User targetUser = new User();
        targetUser.setId(UUID.randomUUID()); 
        
        List<Kvitter> kvitterList = List.of(kvitter);

        when(userRepo.findByEmail("test@example.com")).thenReturn(user);
        when(userRepo.findByUserName("someoneElse")).thenReturn(Optional.of(targetUser));
        when(userMapper.optionalToUser(Optional.of(targetUser))).thenReturn(targetUser);
        when(kvitterRepo.findAllByTargetUser(targetUser.getId(), user.getId())).thenReturn(kvitterList);
        when(kvitterMapper.kvitterToDetailedKvitterDTO(any())).thenReturn(detailedKvitterDto);

        List<DetailedDtoInterface> result = kvitterService.getFilteredKvitters("someoneElse", detailedUserDto);

        assertEquals(1, result.size());
    }

    @Test
    void testGetFilteredKvitters_whenTargetUserIsSameAsLoggedIn() {
        User targetUser = new User();
        targetUser.setId(user.getId());

        List<Kvitter> kvitterList = List.of(kvitter);

        when(userRepo.findByEmail("test@example.com")).thenReturn(user);
        when(userRepo.findByUserName("sameUser")).thenReturn(Optional.of(targetUser));
        when(userMapper.optionalToUser(Optional.of(targetUser))).thenReturn(targetUser);
        when(kvitterRepo.findAllByLoggedInUser(targetUser.getId())).thenReturn(kvitterList);
        when(kvitterMapper.kvitterToDetailedKvitterDTO(any())).thenReturn(detailedKvitterDto);

        List<DetailedDtoInterface> result = kvitterService.getFilteredKvitters("sameUser", detailedUserDto);

        assertEquals(1, result.size());
    }
    
    @Test
    void testGetTenLatestKvitterThatIsNotPrivate() {
        DetailedKvitterDto detailedDto = new DetailedKvitterDto();
        detailedDto.setIsActive(true);
        when(kvitterRepo.getTenLatestKvitterThatIsNotPrivate()).thenReturn(List.of(kvitter));
        when(kvitterMapper.kvitterToDetailedKvitterDTO(any())).thenReturn(detailedDto);

        List<DetailedKvitterDto> result = kvitterService.getTenLatestKvitterThatIsNotPrivate();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsActive());
    }

    @Test
    void testGetSearchedKvitters_whenCategoryIsHashtag() {
        String searchedHashtag = "testtag";
        String category = "hashtag";

        when(userRepo.findByEmail(anyString())).thenReturn(user);
        when(kvitterRepo.searchByHashtag(eq(searchedHashtag), eq(user.getId()))).thenReturn(List.of(kvitter));
        when(kvitterMapper.kvitterToDetailedKvitterDTO(any(Kvitter.class))).thenReturn(detailedKvitterDto);
        
        List<DetailedDtoInterface> result = kvitterService.getSearchedKvitters(category, searchedHashtag, detailedUserDto);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0) instanceof DetailedKvitterDto);

        verify(userRepo).findByEmail(eq(detailedUserDto.getEmail()));
        verify(kvitterRepo).searchByHashtag(eq(searchedHashtag), eq(user.getId()));
    }


} 