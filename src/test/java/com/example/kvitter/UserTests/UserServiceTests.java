package com.example.kvitter.UserTests;

import com.example.kvitter.dtos.CredentialsDto;
import com.example.kvitter.dtos.SignUpDto;
import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.entities.Kvitter;
import com.example.kvitter.entities.User;
import com.example.kvitter.exceptions.AppException;
import com.example.kvitter.mappers.UserMapper;
import com.example.kvitter.repos.KvitterRepo;
import com.example.kvitter.repos.UserRepo;
import com.example.kvitter.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;
    
    @Mock
    private KvitterRepo kvitterRepo;

    @InjectMocks
    private UserService userService;

    private User user;
    private SignUpDto signUpDto;
    private CredentialsDto credentialsDto;
    private DetailedUserDto detailedUserDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .userName("testuser")
                .email("test@example.com")
                .password("encodedpassword")
                .build();

        signUpDto = new SignUpDto("testuser", "test@example.com", "password".toCharArray());
        credentialsDto = new CredentialsDto("testuser", "password".toCharArray());
        detailedUserDto = new DetailedUserDto("testuser", "test@example.com");
    }

    @Test
    void login_ValidCredentials_ReturnsUser() {
        when(userRepo.findByUserName(credentialsDto.userName())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(CharBuffer.wrap(credentialsDto.password()), user.getPassword())).thenReturn(true);
        when(userMapper.userToDetailedUserDTO(user)).thenReturn(detailedUserDto);

        DetailedUserDto result = userService.login(credentialsDto);

        assertNotNull(result);
        assertEquals(detailedUserDto.getUserName(), result.getUserName());
    }

    @Test
    void login_InvalidPassword_ThrowsException() {
        when(userRepo.findByUserName(credentialsDto.userName())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(CharBuffer.wrap(credentialsDto.password()), user.getPassword())).thenReturn(false);

        AppException exception = assertThrows(AppException.class, () -> userService.login(credentialsDto));
        assertEquals("Invalid password", exception.getMessage());
    }

    @Test
    void login_UnknownUser_ThrowsException() {
        when(userRepo.findByUserName(credentialsDto.userName())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> userService.login(credentialsDto));
        assertEquals("Unkown user", exception.getMessage());
    }

    @Test
    void register_NewUser_ReturnsUser() {
        when(userRepo.findByUserName(signUpDto.userName())).thenReturn(Optional.empty());
        when(userMapper.signUpToUser(signUpDto)).thenReturn(user);
        when(passwordEncoder.encode(CharBuffer.wrap(signUpDto.password()))).thenReturn("encodedpassword");
        when(userRepo.save(any(User.class))).thenReturn(user);
        when(userMapper.userToDetailedUserDTO(user)).thenReturn(detailedUserDto);

        DetailedUserDto result = userService.register(signUpDto);

        assertNotNull(result);
        assertEquals(detailedUserDto.getUserName(), result.getUserName());
    }

    @Test
    void register_ExistingUser_ThrowsException() {
        when(userRepo.findByUserName(signUpDto.userName())).thenReturn(Optional.of(user));

        AppException exception = assertThrows(AppException.class, () -> userService.register(signUpDto));
        assertEquals("Username already exists", exception.getMessage());
    }

    
    @Test
    void followUser_SuccessfullyFollowsUser() {
        User follower = new User();
        follower.setEmail("follower@example.com");
        follower.setFollowing(new ArrayList<>());

        User followee = new User();
        followee.setId(UUID.randomUUID());
        followee.setEmail("followee@example.com");

        when(userRepo.findByEmail("followee@example.com")).thenReturn(followee);
        when(userRepo.findByEmail("follower@example.com")).thenReturn(follower);

        DetailedUserDto dto = new DetailedUserDto("follower", "follower@example.com");

        userService.followUser("followee@example.com", dto);

        assertTrue(follower.getFollowing().contains(followee));
        verify(userRepo).save(follower);
    }

    @Test
    void followUser_AlreadyFollowing_ThrowsException() {
        User followee = new User();
        followee.setId(UUID.randomUUID());

        User follower = new User();
        follower.setEmail("follower@example.com");
        follower.setFollowing(new ArrayList<>(List.of(followee)));

        when(userRepo.findByEmail("followee@example.com")).thenReturn(followee);
        when(userRepo.findByEmail("follower@example.com")).thenReturn(follower);

        DetailedUserDto dto = new DetailedUserDto("follower", "follower@example.com");

        AppException exception = assertThrows(AppException.class, () -> userService.followUser("followee@example.com", dto));
        assertEquals("Already following that user", exception.getMessage());
    }

    @Test
    void unFollowUser_SuccessfullyUnfollowsUser() {
        User followee = new User();
        followee.setId(UUID.randomUUID());
        followee.setUserName("followee");

        User follower = new User();
        follower.setEmail("follower@example.com");
        follower.setUserName("follower");
        follower.setFollowing(new ArrayList<>(List.of(followee)));

        when(userRepo.findByEmail("followee@example.com")).thenReturn(followee);
        when(userRepo.findByEmail("follower@example.com")).thenReturn(follower);

        DetailedUserDto dto = new DetailedUserDto("follower", "follower@example.com");

        userService.unFollowUser("followee@example.com", dto);

        assertFalse(follower.getFollowing().contains(followee));
        verify(userRepo).save(follower);
    }

    @Test
    void unFollowUser_NotFollowing_ThrowsException() {
        User followee = new User();
        followee.setId(UUID.randomUUID());

        User follower = new User();
        follower.setEmail("follower@example.com");
        follower.setFollowing(new ArrayList<>());

        when(userRepo.findByEmail("followee@example.com")).thenReturn(followee);
        when(userRepo.findByEmail("follower@example.com")).thenReturn(follower);

        DetailedUserDto dto = new DetailedUserDto("follower", "follower@example.com");

        AppException exception = assertThrows(AppException.class, () -> userService.unFollowUser("followee@example.com", dto));
        assertEquals("Not following that user", exception.getMessage());
    }

    @Test
    void upvoteKvitter_SuccessfullyAddsUpvote() {
        UUID kvitterId = UUID.randomUUID();
        Kvitter kvitter = new Kvitter();
        kvitter.setId(kvitterId);

        User user = new User();
        user.setEmail("user@example.com");
        user.setLikes(new ArrayList<>());

        when(kvitterRepo.findById(kvitterId)).thenReturn(Optional.of(kvitter));
        when(userRepo.findByEmail("user@example.com")).thenReturn(user);

        DetailedUserDto dto = new DetailedUserDto("user", "user@example.com");

        userService.upvoteKvitter(kvitterId.toString(), dto);

        assertTrue(user.getLikes().contains(kvitter));
        verify(userRepo).save(user);
    }

    @Test
    void upvoteKvitter_AlreadyUpvoted_ThrowsException() {
        UUID kvitterId = UUID.randomUUID();
        Kvitter kvitter = new Kvitter();
        kvitter.setId(kvitterId);

        User user = new User();
        user.setEmail("user@example.com");
        user.setLikes(new ArrayList<>(List.of(kvitter)));

        when(kvitterRepo.findById(kvitterId)).thenReturn(Optional.of(kvitter));
        when(userRepo.findByEmail("user@example.com")).thenReturn(user);

        DetailedUserDto dto = new DetailedUserDto("user", "user@example.com");

        AppException exception = assertThrows(AppException.class, () -> userService.upvoteKvitter(kvitterId.toString(), dto));
        assertEquals("User already upvoted this kvitter", exception.getMessage());
    }

    @Test
    void removeUpvoteOnKvitter_SuccessfullyRemovesUpvote() {
        UUID kvitterId = UUID.randomUUID();
        Kvitter kvitter = new Kvitter();
        kvitter.setId(kvitterId);

        User user = new User();
        user.setEmail("user@example.com");
        user.setLikes(new ArrayList<>(List.of(kvitter)));

        when(kvitterRepo.findById(kvitterId)).thenReturn(Optional.of(kvitter));
        when(userRepo.findByEmail("user@example.com")).thenReturn(user);

        DetailedUserDto dto = new DetailedUserDto("user", "user@example.com");

        userService.removeUpvoteOnKvitter(kvitterId.toString(), dto);

        assertFalse(user.getLikes().contains(kvitter));
        verify(userRepo).save(user);
    }

    @Test
    void removeUpvoteOnKvitter_NotUpvoted_ThrowsException() {
        UUID kvitterId = UUID.randomUUID();
        Kvitter kvitter = new Kvitter();
        kvitter.setId(kvitterId);

        User user = new User();
        user.setEmail("user@example.com");
        user.setLikes(new ArrayList<>());

        when(kvitterRepo.findById(kvitterId)).thenReturn(Optional.of(kvitter));
        when(userRepo.findByEmail("user@example.com")).thenReturn(user);

        DetailedUserDto dto = new DetailedUserDto("user", "user@example.com");

        AppException exception = assertThrows(AppException.class, () -> userService.removeUpvoteOnKvitter(kvitterId.toString(), dto));
        assertEquals("User hasn't upvoted this kvitter yet", exception.getMessage());
    }


}
