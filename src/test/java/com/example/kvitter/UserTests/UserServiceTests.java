package com.example.kvitter.UserTests;

import com.example.kvitter.dtos.CredentialsDto;
import com.example.kvitter.dtos.SignUpDto;
import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.entities.User;
import com.example.kvitter.exceptions.AppException;
import com.example.kvitter.mappers.UserMapper;
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
import java.util.Optional;

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
    void getUserByEmail_ExistingUser_ReturnsUser() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(user);

        User result = userService.getUserByEmail(user.getEmail());

        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void getDetailedUserDTOByEmail_ExistingUser_ReturnsDetailedUser() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(user);
        when(userMapper.userToDetailedUserDTO(user)).thenReturn(detailedUserDto);

        DetailedUserDto result = userService.getDetailedUserDTOByEmail(user.getEmail());

        assertNotNull(result);
        assertEquals(detailedUserDto.getUserName(), result.getUserName());
    }
}
