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

    @Test
    void testGetSearchedKvitters_whenCategoryIsUser() {
        String searchedUser = "testuser";

        when(userRepo.findByEmail(anyString())).thenReturn(user);
        when(userRepo.findByUserName(eq(searchedUser))).thenReturn(Optional.of(user));
        when(userMapper.optionalToUser(any())).thenReturn(user);
        when(kvitterRepo.findAllByLoggedInUser(eq(user.getId()))).thenReturn(List.of(kvitter));
        when(kvitterMapper.kvitterToDetailedKvitterDTO(any(Kvitter.class))).thenReturn(detailedKvitterDto);

        List<DetailedDtoInterface> result = kvitterService.getSearchedKvitters("user", searchedUser, detailedUserDto);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0) instanceof DetailedKvitterDto);

        verify(userRepo).findByUserName(eq(searchedUser));
        verify(kvitterRepo).findAllByLoggedInUser(eq(user.getId()));
    }

    @Test
    void testGetSearchedKvitters_whenCategoryIsMessage() {
        String searchedMessage = "hello";

        when(userRepo.findByEmail(anyString())).thenReturn(user);
        when(kvitterRepo.findAllByMessageContainsIgnoreCaseAndIsPrivate(eq(searchedMessage), eq(false))).thenReturn(List.of(kvitter));
        when(kvitterMapper.kvitterToDetailedKvitterDTO(any(Kvitter.class))).thenReturn(detailedKvitterDto);

        List<DetailedDtoInterface> result = kvitterService.getSearchedKvitters("message", searchedMessage, detailedUserDto);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0) instanceof DetailedKvitterDto);

        verify(kvitterRepo).findAllByMessageContainsIgnoreCaseAndIsPrivate(eq(searchedMessage), eq(false));
    }

    @Test
    void testGetFilteredKvitters_Popular() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(user);
        when(kvitterRepo.findMostPopularKvitter(user.getId())).thenReturn(List.of(kvitter));
        when(kvitterMapper.kvitterToDetailedKvitterDTO(kvitter)).thenReturn(detailedKvitterDto);

        List<DetailedDtoInterface> result = kvitterService.getFilteredKvitters("popular", "ignored", detailedUserDto);

        assertEquals(1, result.size());
    }

    @Test
    void testGetFilteredKvitters_Following() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(user);
        when(kvitterRepo.findAllByUserFollows(user.getId())).thenReturn(List.of(kvitter));
        when(kvitterMapper.kvitterToDetailedKvitterDTO(kvitter)).thenReturn(detailedKvitterDto);

        List<DetailedDtoInterface> result = kvitterService.getFilteredKvitters("following", "ignored", detailedUserDto);

        assertEquals(1, result.size());
    }

    @Test
    void testGetFilteredKvitters_Latest() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(user);
        when(kvitterRepo.getLatestKvitters(user.getId())).thenReturn(List.of(kvitter));
        when(kvitterMapper.kvitterToDetailedKvitterDTO(kvitter)).thenReturn(detailedKvitterDto);

        List<DetailedDtoInterface> result = kvitterService.getFilteredKvitters("latest", "ignored", detailedUserDto);

        assertEquals(1, result.size());
    }

    @Test
    void testGetFilteredKvitters_UserInfo() {
        User targetUser = new User();
        targetUser.setId(UUID.randomUUID());

        when(userRepo.findByEmail(user.getEmail())).thenReturn(user);
        when(userRepo.findByUserName("target")).thenReturn(Optional.of(targetUser));
        when(userMapper.optionalToUser(Optional.of(targetUser))).thenReturn(targetUser);
        when(kvitterRepo.findAllByTargetUser(targetUser.getId(), user.getId())).thenReturn(List.of(kvitter));
        when(kvitterMapper.kvitterToDetailedKvitterDTO(kvitter)).thenReturn(detailedKvitterDto);

        List<DetailedDtoInterface> result = kvitterService.getFilteredKvitters("user-info", "target", detailedUserDto);

        assertEquals(1, result.size());
    }

    @Test
    void testGetFilteredKvitters_MyActivity() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(user);
        when(kvitterRepo.findAllByLoggedInUser(user.getId())).thenReturn(List.of(kvitter));
        when(kvitterMapper.kvitterToDetailedKvitterDTO(kvitter)).thenReturn(detailedKvitterDto);

        List<DetailedDtoInterface> result = kvitterService.getFilteredKvitters("myactivity", "ignored", detailedUserDto);

        assertEquals(1, result.size());
    }



} 