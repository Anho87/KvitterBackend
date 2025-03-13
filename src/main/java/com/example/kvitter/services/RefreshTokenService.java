package com.example.kvitter.services;

import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.entities.RefreshToken;
import com.example.kvitter.entities.User;
import com.example.kvitter.exceptions.RefreshTokenExpiredException;
import com.example.kvitter.mappers.UserMapper;
import com.example.kvitter.repos.RefreshTokenRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    @Value("${app.jwt.refreshExpirationMs}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepo refreshTokenRepo;
    private final UserMapper userMapper;


    public RefreshToken createRefreshToken(DetailedUserDto detailedUserDto) {
        removeAllUserTokens(userMapper.detailedUserDTOToUser(detailedUserDto).getId());
        User user = userMapper.detailedUserDTOToUser(detailedUserDto);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(generateSecureRefreshToken());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setRevoked(false);
        refreshTokenRepo.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyRefreshToken(String token) {
        return refreshTokenRepo.findByToken(token)
                .filter(rt -> !rt.isRevoked() && rt.getExpiryDate().isAfter(Instant.now()))
                .orElseThrow(() -> new RefreshTokenExpiredException("Refresh token expired or invalid."));
    }

    public void revokeRefreshToken(RefreshToken refreshToken) {
        refreshToken.setRevoked(true);
        refreshTokenRepo.save(refreshToken);
    }

    public User getUserFromRefreshToken(String token) {
        Optional<RefreshToken> oRefreshToken = refreshTokenRepo.findByToken(token);
        if (oRefreshToken.isEmpty()) {
            throw new RefreshTokenExpiredException("Refresh token expired or invalid.");
        }
        RefreshToken refreshToken = oRefreshToken.get();
        User user = refreshToken.getUser();
        if (user == null) {
            throw new RefreshTokenExpiredException("Refresh token expired or invalid.");
        }
        return user;
    }

    public void removeAllUserTokens(UUID userId) {
        refreshTokenRepo.deleteByUserId(userId);
    }

    public boolean checkIfUserHasActiveRefreshToken(UUID userId) {
        RefreshToken refreshToken = refreshTokenRepo.findByUserId(userId);
        return refreshToken != null && refreshToken.getExpiryDate().isAfter(Instant.now());
    }

    public RefreshToken getUserActiveRefreshToken(UUID userId) {
        return refreshTokenRepo.findByUserId(userId);
    }

    private String generateSecureRefreshToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
}

