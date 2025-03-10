package com.example.kvitter.services;

import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.entities.RefreshToken;
import com.example.kvitter.entities.User;
import com.example.kvitter.exceptions.AppException;
import com.example.kvitter.mappers.UserMapper;
import com.example.kvitter.repos.RefreshTokenRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${app.jwt.refreshExpirationMs}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepo refreshTokenRepo;
    private final UserMapper userMapper;
    

    public String createRefreshToken(DetailedUserDto detailedUserDto) {
        User user =  userMapper.detailedUserDTOToUser(detailedUserDto);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(generateSecureRefreshToken());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setRevoked(false);
        refreshTokenRepo.save(refreshToken);
        return refreshToken.getToken();
    }

    public RefreshToken verifyRefreshToken(String token) {
        return refreshTokenRepo.findByToken(token)
                .filter(rt -> !rt.isRevoked() && rt.getExpiryDate().isAfter(Instant.now()))
                .orElseThrow(() -> new AppException("Invalid or expired refresh token", HttpStatus.UNAUTHORIZED));
    }

    public void revokeRefreshToken(RefreshToken refreshToken) {
        refreshToken.setRevoked(true);
        refreshTokenRepo.save(refreshToken);
    }

    public void revokeAllUserTokens(User user) {
        refreshTokenRepo.deleteByUser(user); 
    }
    
    public boolean checkIfUserHasActiveRefreshToken(UUID userId){
        return refreshTokenRepo.findByUserId(userId);
    }

    private String generateSecureRefreshToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[32]; 
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
}

