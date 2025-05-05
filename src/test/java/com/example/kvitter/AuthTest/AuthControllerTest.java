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
        Map<String, Object> mockResponse = Map.of("user", detailedUserDto, "accessToken", "token");

        when(refreshTokenService.buildAuthResponseWithTokens(any())).thenReturn(ResponseEntity.ok(mockResponse));
        when(userService.login(any())).thenReturn(detailedUserDto);

        ResponseEntity<Map<String, Object>> response = authController.login(credentials);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).containsKeys("user", "accessToken");
    }

    @Test
    void testRegister() {
        SignUpDto signUpDto = new SignUpDto("test@example.com", "testuser", "password".toCharArray());
        Map<String, Object> mockResponse = Map.of("user", detailedUserDto, "accessToken", "token");

        when(refreshTokenService.buildAuthResponseWithTokens(any())).thenReturn(ResponseEntity.ok(mockResponse));
        when(userService.register(any())).thenReturn(detailedUserDto);

        ResponseEntity<Map<String, Object>> response = authController.register(signUpDto);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).containsKeys("user", "accessToken");
    }

    @Test
    void testLogout() {
        when(refreshTokenService.logout("refresh-token")).thenReturn(ResponseEntity.ok().build());

        ResponseEntity<Void> response = authController.logout("refresh-token");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void testRefreshAccessToken_success() {
        Map<String, String> responseMap = Map.of("accessToken", "new-token");
        when(refreshTokenService.refreshAccessToken("refresh-token")).thenReturn(ResponseEntity.ok(responseMap));

        ResponseEntity<Map<String, String>> response = authController.refreshAccessToken("refresh-token");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).containsKey("accessToken");
    }

    @Test
    void testRefreshAccessToken_missingToken() {
        Map<String, String> errorMap = Map.of("error", "Refresh token is missing.");
        when(refreshTokenService.refreshAccessToken("")).thenReturn(ResponseEntity.status(403).body(errorMap));

        ResponseEntity<Map<String, String>> response = authController.refreshAccessToken("");

        assertThat(response.getStatusCodeValue()).isEqualTo(403);
        assertThat(response.getBody()).containsEntry("error", "Refresh token is missing.");
    }

}
