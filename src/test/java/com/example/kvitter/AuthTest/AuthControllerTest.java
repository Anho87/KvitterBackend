package com.example.kvitter.AuthTest;

import com.example.kvitter.configs.UserAuthProvider;
import com.example.kvitter.controllers.AuthController;
import com.example.kvitter.dtos.CredentialsDto;
import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.dtos.SignUpDto;
import com.example.kvitter.entities.RefreshToken;
import com.example.kvitter.entities.User;
import com.example.kvitter.services.RefreshTokenService;
import com.example.kvitter.services.UserService;
import com.example.kvitter.mappers.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private UserService userService;
    @Mock
    private UserAuthProvider userAuthProvider;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthController authController;

    private DetailedUserDto detailedUserDto;
    private RefreshToken refreshToken;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        detailedUserDto = new DetailedUserDto();
        detailedUserDto.setId(UUID.randomUUID());
        detailedUserDto.setEmail("test@example.com");
        detailedUserDto.setUserName("testuser");

        refreshToken = RefreshToken.builder()
                .token("refresh-token")
                .expiryDate(Instant.now().plusSeconds(600))
                .build();

        user = new User();
        user.setId(detailedUserDto.getId());
    }

    @Test
    void testLogin() {
        CredentialsDto credentials = new CredentialsDto("testuser", "password".toCharArray());

        when(userService.login(any())).thenReturn(detailedUserDto);
        when(userAuthProvider.createToken(any())).thenReturn("access-token");
        when(refreshTokenService.checkIfUserHasActiveRefreshToken(any())).thenReturn(false);
        when(refreshTokenService.createRefreshToken(any())).thenReturn(refreshToken);

        ResponseEntity<Map<String, Object>> response = authController.login(credentials);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).containsKeys("user", "accessToken");
    }

    @Test
    void testRegister() {
        SignUpDto signUpDto = new SignUpDto("test@example.com", "testuser", "password".toCharArray());

        when(userService.register(any())).thenReturn(detailedUserDto);
        when(userAuthProvider.createToken(any())).thenReturn("access-token");
        when(refreshTokenService.checkIfUserHasActiveRefreshToken(any())).thenReturn(false);
        when(refreshTokenService.createRefreshToken(any())).thenReturn(refreshToken);

        ResponseEntity<Map<String, Object>> response = authController.register(signUpDto);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).containsKeys("user", "accessToken");
    }

    @Test
    void testLogout() {
        when(refreshTokenService.getUserFromRefreshToken(any())).thenReturn(user);

        ResponseEntity<Void> response = authController.logout("refresh-token");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        verify(refreshTokenService).removeAllUserTokens(user.getId());
    }

    @Test
    void testRefreshAccessToken_success() {
        when(refreshTokenService.verifyRefreshToken(any())).thenReturn(refreshToken);
        when(refreshTokenService.getUserFromRefreshToken(any())).thenReturn(user);
        when(userMapper.userToDetailedUserDTO(any())).thenReturn(detailedUserDto);
        when(userAuthProvider.createToken(any())).thenReturn("new-access-token");

        ResponseEntity<Map<String, String>> response = authController.refreshAccessToken("refresh-token");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).containsKey("accessToken");
    }

    @Test
    void testRefreshAccessToken_missingToken() {
        ResponseEntity<Map<String, String>> response = authController.refreshAccessToken("");

        assertThat(response.getStatusCodeValue()).isEqualTo(403);
        assertThat(response.getBody()).containsEntry("error", "Refresh token is missing.");
    }
}
