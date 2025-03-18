package com.example.kvitter.RefreshTokenTests;


import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.entities.RefreshToken;
import com.example.kvitter.entities.User;
import com.example.kvitter.exceptions.RefreshTokenExpiredException;
import com.example.kvitter.mappers.UserMapper;
import com.example.kvitter.repos.RefreshTokenRepo;
import com.example.kvitter.services.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTests {

    @Mock
    private RefreshTokenRepo refreshTokenRepo;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

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

        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDurationMs", 3600000L);
    }

    @Test
    void createRefreshToken_ShouldReturnNewRefreshToken() {
        when(userMapper.detailedUserDTOToUser(detailedUserDto)).thenReturn(user);
        when(refreshTokenRepo.save(any(RefreshToken.class))).thenReturn(refreshToken);

        RefreshToken createdToken = refreshTokenService.createRefreshToken(detailedUserDto);

        assertNotNull(createdToken);
        assertEquals(user, createdToken.getUser());
        assertFalse(createdToken.isRevoked());
        verify(refreshTokenRepo, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void verifyRefreshToken_ShouldReturnValidToken() {
        when(refreshTokenRepo.findByToken(token)).thenReturn(Optional.of(refreshToken));

        RefreshToken verifiedToken = refreshTokenService.verifyRefreshToken(token);
        assertNotNull(verifiedToken);
        assertEquals(token, verifiedToken.getToken());
    }

    @Test
    void verifyRefreshToken_ShouldThrowException_WhenTokenIsExpired() {
        refreshToken.setExpiryDate(Instant.now().minusSeconds(1));
        when(refreshTokenRepo.findByToken(token)).thenReturn(Optional.of(refreshToken));

        assertThrows(RefreshTokenExpiredException.class, () -> refreshTokenService.verifyRefreshToken(token));
    }

    @Test
    void revokeRefreshToken_ShouldMarkTokenAsRevoked() {
        refreshTokenService.revokeRefreshToken(refreshToken);
        assertTrue(refreshToken.isRevoked());
        verify(refreshTokenRepo, times(1)).save(refreshToken);
    }

    @Test
    void getUserFromRefreshToken_ShouldReturnUser() {
        when(refreshTokenRepo.findByToken(token)).thenReturn(Optional.of(refreshToken));
        User retrievedUser = refreshTokenService.getUserFromRefreshToken(token);
        assertNotNull(retrievedUser);
        assertEquals(user, retrievedUser);
    }

    @Test
    void getUserFromRefreshToken_ShouldThrowException_WhenTokenNotFound() {
        when(refreshTokenRepo.findByToken(token)).thenReturn(Optional.empty());
        assertThrows(RefreshTokenExpiredException.class, () -> refreshTokenService.getUserFromRefreshToken(token));
    }

    @Test
    void removeAllUserTokens_ShouldCallDeleteByUserId() {
        UUID userId = user.getId();
        refreshTokenService.removeAllUserTokens(userId);
        verify(refreshTokenRepo, times(1)).deleteByUserId(userId);
    }

    @Test
    void checkIfUserHasActiveRefreshToken_ShouldReturnTrue_WhenTokenIsValid() {
        when(refreshTokenRepo.findByUserId(user.getId())).thenReturn(refreshToken);
        assertTrue(refreshTokenService.checkIfUserHasActiveRefreshToken(user.getId()));
    }

    @Test
    void checkIfUserHasActiveRefreshToken_ShouldReturnFalse_WhenTokenIsExpired() {
        refreshToken.setExpiryDate(Instant.now().minusSeconds(1));
        when(refreshTokenRepo.findByUserId(user.getId())).thenReturn(refreshToken);
        assertFalse(refreshTokenService.checkIfUserHasActiveRefreshToken(user.getId()));
    }

    @Test
    void getUserActiveRefreshToken_ShouldReturnToken() {
        when(refreshTokenRepo.findByUserId(user.getId())).thenReturn(refreshToken);
        RefreshToken retrievedToken = refreshTokenService.getUserActiveRefreshToken(user.getId());
        assertNotNull(retrievedToken);
        assertEquals(refreshToken, retrievedToken);
    }
}

