package com.example.kvitter.RefreshTokenTests;


import com.example.kvitter.configs.UserAuthProvider;
import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.entities.RefreshToken;
import com.example.kvitter.entities.User;
import com.example.kvitter.mappers.UserMapper;
import com.example.kvitter.repos.RefreshTokenRepo;
import com.example.kvitter.services.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTests {

    @Mock private RefreshTokenRepo refreshTokenRepo;
    @Mock private UserMapper userMapper;
    @Mock private UserAuthProvider userAuthProvider;

    @InjectMocks private RefreshTokenService refreshTokenService;

    private User user;
    private RefreshToken refreshToken;
    private DetailedUserDto detailedUserDto;
    private final String token = "sampleToken";

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());

        refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(token);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(3600));
        refreshToken.setRevoked(false);

        detailedUserDto = new DetailedUserDto();
        detailedUserDto.setId(user.getId());

        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDurationMs", 3600000L);
    }

    @Test
    void testBuildAuthResponseWithTokens_newRefreshTokenCreated() {
        when(userAuthProvider.createToken(detailedUserDto)).thenReturn("access-token");
        when(refreshTokenRepo.findByUserId(user.getId())).thenReturn(null); // No active token
        when(userMapper.detailedUserDTOToUser(detailedUserDto)).thenReturn(user);
        when(refreshTokenRepo.save(any())).thenReturn(refreshToken);

        ResponseEntity<Map<String, Object>> response = refreshTokenService.buildAuthResponseWithTokens(detailedUserDto);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("accessToken"));
        assertTrue(response.getBody().containsKey("user"));
    }

    @Test
    void testBuildAuthResponseWithTokens_existingRefreshTokenUsed() {
        when(userAuthProvider.createToken(detailedUserDto)).thenReturn("access-token");
        when(refreshTokenRepo.findByUserId(user.getId())).thenReturn(refreshToken);

        ResponseEntity<Map<String, Object>> response = refreshTokenService.buildAuthResponseWithTokens(detailedUserDto);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("accessToken"));
    }

    @Test
    void testRefreshAccessToken_success() {
        when(refreshTokenRepo.findByToken(token)).thenReturn(Optional.of(refreshToken));
        when(userMapper.userToDetailedUserDTO(user)).thenReturn(detailedUserDto);
        when(userAuthProvider.createToken(detailedUserDto)).thenReturn("new-access-token");

        ResponseEntity<Map<String, String>> response = refreshTokenService.refreshAccessToken(token);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("new-access-token", response.getBody().get("accessToken"));
    }

    @Test
    void testRefreshAccessToken_expired() {
        refreshToken.setExpiryDate(Instant.now().minusSeconds(10));
        when(refreshTokenRepo.findByToken(token)).thenReturn(Optional.of(refreshToken));

        ResponseEntity<Map<String, String>> response = refreshTokenService.refreshAccessToken(token);

        assertEquals(403, response.getStatusCodeValue());
        assertEquals("Refresh token expired or invalid.", response.getBody().get("error"));
    }

    @Test
    void testRefreshAccessToken_missing() {
        ResponseEntity<Map<String, String>> response = refreshTokenService.refreshAccessToken("");

        assertEquals(403, response.getStatusCodeValue());
        assertEquals("Refresh token is missing.", response.getBody().get("error"));
    }

    @Test
    void testLogout() {
        when(refreshTokenRepo.findByToken(token)).thenReturn(Optional.of(refreshToken));

        ResponseEntity<Void> response = refreshTokenService.logout(token);

        assertEquals(200, response.getStatusCodeValue());
        verify(refreshTokenRepo).deleteByUserId(user.getId());
    }
}
