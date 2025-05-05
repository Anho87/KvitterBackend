package com.example.kvitter.KvitterTests;

import com.example.kvitter.dtos.*;
import com.example.kvitter.entities.Hashtag;
import com.example.kvitter.entities.Kvitter;
import com.example.kvitter.entities.Reply;
import com.example.kvitter.entities.User;
import com.example.kvitter.exceptions.AppException;
import com.example.kvitter.mappers.KvitterMapper;
import com.example.kvitter.mappers.ReplyMapper;
import com.example.kvitter.mappers.UserMapper;
import com.example.kvitter.repos.KvitterRepo;
import com.example.kvitter.repos.UserRepo;
import com.example.kvitter.services.AuthService;
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
    @Mock private AuthService authService;

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
    void testAddKvitter_WithValidRequest() {
        KvitterRequest request = new KvitterRequest("Hello", List.of("test"), false);
        String token = "Bearer faketoken";

        when(authService.getUserFromToken(token)).thenReturn(detailedUserDto);
        when(userRepo.findById(detailedUserDto.getId())).thenReturn(Optional.of(user));
        when(hashtagService.addHashTag("test")).thenReturn(new Hashtag());

        kvitterService.addKvitter(request, token);

        verify(kvitterRepo).save(any(Kvitter.class));
    }

    @Test
    void testAddKvitter_ThrowsIfMessageIsEmpty() {
        KvitterRequest request = new KvitterRequest("   ", List.of("tag1"), false);
        String token = "Bearer faketoken";

        when(authService.getUserFromToken(token)).thenReturn(detailedUserDto);

        AppException ex = assertThrows(AppException.class, () ->
                kvitterService.addKvitter(request, token)
        );

        assertEquals("Message can't be empty.", ex.getMessage());
    }

    @Test
    void testRemoveKvitter_DeletesCompletely() {
        String token = "Bearer faketoken";
        kvitter.setReplies(Collections.emptyList());
        kvitter.setRekvitts(Collections.emptyList());
        when(kvitterRepo.findById(any())).thenReturn(Optional.of(kvitter));

        kvitterService.removeKvitter(kvitter.getId().toString(), token);

        verify(kvitterRepo).deleteKvitterById(eq(kvitter.getId()));
    }

    @Test
    void testRemoveKvitter_MarksAsDeleted() {
        String token = "Bearer faketoken";
        kvitter.setReplies(List.of(mock(Reply.class))); 
        when(kvitterRepo.findById(any())).thenReturn(Optional.of(kvitter));

        kvitterService.removeKvitter(kvitter.getId().toString(), token);

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
        String token = "Bearer faketoken";
        when(authService.getUserFromToken(token)).thenReturn(detailedUserDto);
        
        String searchedHashtag = "testtag";
        String category = "hashtag";

        when(userRepo.findByEmailIgnoreCase(anyString())).thenReturn(user);
        when(kvitterRepo.searchByHashtag(eq(searchedHashtag), eq(user.getId()))).thenReturn(List.of(kvitter));
        when(kvitterMapper.kvitterToDetailedKvitterDTO(any(Kvitter.class))).thenReturn(detailedKvitterDto);
        
        List<DetailedDtoInterface> result = kvitterService.getSearchedKvitters(category, searchedHashtag, token);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0) instanceof DetailedKvitterDto);

        verify(userRepo).findByEmailIgnoreCase(eq(detailedUserDto.getEmail()));
        verify(kvitterRepo).searchByHashtag(eq(searchedHashtag), eq(user.getId()));
    }

    @Test
    void testGetSearchedKvitters_whenCategoryIsUser() {
        String token = "Bearer faketoken";
        when(authService.getUserFromToken(token)).thenReturn(detailedUserDto);
        
        String searchedUser = "testuser";

        when(userRepo.findByEmailIgnoreCase(anyString())).thenReturn(user);
        when(userRepo.findByUserNameIgnoreCase(eq(searchedUser))).thenReturn(Optional.of(user));
        when(userMapper.optionalToUser(any())).thenReturn(user);
        when(kvitterRepo.findAllByLoggedInUser(eq(user.getId()))).thenReturn(List.of(kvitter));
        when(kvitterMapper.kvitterToDetailedKvitterDTO(any(Kvitter.class))).thenReturn(detailedKvitterDto);

        List<DetailedDtoInterface> result = kvitterService.getSearchedKvitters("user", searchedUser, token);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0) instanceof DetailedKvitterDto);

        verify(userRepo).findByUserNameIgnoreCase(eq(searchedUser));
        verify(kvitterRepo).findAllByLoggedInUser(eq(user.getId()));
    }

    @Test
    void testGetSearchedKvitters_whenCategoryIsMessage() {
        String token = "Bearer faketoken";
        when(authService.getUserFromToken(token)).thenReturn(detailedUserDto);
        
        String searchedMessage = "hello";

        when(userRepo.findByEmailIgnoreCase(anyString())).thenReturn(user);
        when(kvitterRepo.findAllByMessageContainsIgnoreCaseAndIsPrivate(eq(searchedMessage), eq(false))).thenReturn(List.of(kvitter));
        when(kvitterMapper.kvitterToDetailedKvitterDTO(any(Kvitter.class))).thenReturn(detailedKvitterDto);

        List<DetailedDtoInterface> result = kvitterService.getSearchedKvitters("message", searchedMessage, token);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0) instanceof DetailedKvitterDto);

        verify(kvitterRepo).findAllByMessageContainsIgnoreCaseAndIsPrivate(eq(searchedMessage), eq(false));
    }

    @Test
    void testGetFilteredKvitters_Popular() {
        String token = "Bearer faketoken";
        when(authService.getUserFromToken(token)).thenReturn(detailedUserDto);
        
        when(userRepo.findByEmailIgnoreCase(user.getEmail())).thenReturn(user);
        when(kvitterRepo.findMostPopularKvitter(user.getId())).thenReturn(List.of(kvitter));
        when(kvitterMapper.kvitterToDetailedKvitterDTO(kvitter)).thenReturn(detailedKvitterDto);

        List<DetailedDtoInterface> result = kvitterService.getFilteredKvitters("popular", "ignored", token);

        assertEquals(1, result.size());
    }

    @Test
    void testGetFilteredKvitters_Following() {
        String token = "Bearer faketoken";
        when(authService.getUserFromToken(token)).thenReturn(detailedUserDto);
        
        when(userRepo.findByEmailIgnoreCase(user.getEmail())).thenReturn(user);
        when(kvitterRepo.findAllByUserFollows(user.getId())).thenReturn(List.of(kvitter));
        when(kvitterMapper.kvitterToDetailedKvitterDTO(kvitter)).thenReturn(detailedKvitterDto);

        List<DetailedDtoInterface> result = kvitterService.getFilteredKvitters("following", "ignored", token);

        assertEquals(1, result.size());
    }

    @Test
    void testGetFilteredKvitters_Latest() {
        String token = "Bearer faketoken";
        when(authService.getUserFromToken(token)).thenReturn(detailedUserDto);
        
        when(userRepo.findByEmailIgnoreCase(user.getEmail())).thenReturn(user);
        when(kvitterRepo.getLatestKvitters(user.getId())).thenReturn(List.of(kvitter));
        when(kvitterMapper.kvitterToDetailedKvitterDTO(kvitter)).thenReturn(detailedKvitterDto);

        List<DetailedDtoInterface> result = kvitterService.getFilteredKvitters("latest", "ignored", token);

        assertEquals(1, result.size());
    }

    @Test
    void testGetFilteredKvitters_UserInfo() {
        String token = "Bearer faketoken";
        when(authService.getUserFromToken(token)).thenReturn(detailedUserDto);
        
        User targetUser = new User();
        targetUser.setId(UUID.randomUUID());

        when(userRepo.findByEmailIgnoreCase(user.getEmail())).thenReturn(user);
        when(userRepo.findByUserNameIgnoreCase("target")).thenReturn(Optional.of(targetUser));
        when(userMapper.optionalToUser(Optional.of(targetUser))).thenReturn(targetUser);
        when(kvitterRepo.findAllByTargetUser(targetUser.getId(), user.getId())).thenReturn(List.of(kvitter));
        when(kvitterMapper.kvitterToDetailedKvitterDTO(kvitter)).thenReturn(detailedKvitterDto);

        List<DetailedDtoInterface> result = kvitterService.getFilteredKvitters("user-info", "target", token);

        assertEquals(1, result.size());
    }

    @Test
    void testGetFilteredKvitters_MyActivity() {
        String token = "Bearer faketoken";
        when(authService.getUserFromToken(token)).thenReturn(detailedUserDto);
        
        when(userRepo.findByEmailIgnoreCase(user.getEmail())).thenReturn(user);
        when(kvitterRepo.findAllByLoggedInUser(user.getId())).thenReturn(List.of(kvitter));
        when(kvitterMapper.kvitterToDetailedKvitterDTO(kvitter)).thenReturn(detailedKvitterDto);

        List<DetailedDtoInterface> result = kvitterService.getFilteredKvitters("myactivity", "ignored", token);

        assertEquals(1, result.size());
    }



} 