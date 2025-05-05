package com.example.kvitter.services;

import com.example.kvitter.configs.UserAuthProvider;
import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.entities.RefreshToken;
import com.example.kvitter.entities.User;
import com.example.kvitter.exceptions.RefreshTokenExpiredException;
import com.example.kvitter.mappers.UserMapper;
import com.example.kvitter.repos.RefreshTokenRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    @Value("${app.jwt.refreshExpirationMs}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepo refreshTokenRepo;
    private final UserMapper userMapper;
    private final UserAuthProvider userAuthProvider;

    public ResponseEntity<Void>  logout(String refreshToken){
        User user = getUserFromRefreshToken(refreshToken);
        removeAllUserTokens(user.getId());
        ResponseCookie expiredCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
                .build();
    }
    
    public ResponseEntity<Map<String, String>> refreshAccessToken(String refreshToken){
        try {
            if (refreshToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Collections.singletonMap("error", "Refresh token is missing."));
            }
            verifyRefreshToken(refreshToken);
            User user = getUserFromRefreshToken(refreshToken);
            String newAccessToken = userAuthProvider.createToken(userMapper.userToDetailedUserDTO(user));
            Map<String, String> response = new HashMap<>();
            response.put("accessToken", newAccessToken);
            return ResponseEntity.ok(response);
        } catch (RefreshTokenExpiredException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "Refresh token expired or invalid."));
        }
    }
    
    public ResponseEntity<Map<String, Object>> buildAuthResponseWithTokens(DetailedUserDto user) {
        String accessToken = userAuthProvider.createToken(user);
        RefreshToken refreshToken;
        if (!checkIfUserHasActiveRefreshToken(user.getId())) {
            refreshToken = createRefreshToken(user);
        }else{
            refreshToken = getUserActiveRefreshToken(user.getId());
        }
        long remainingTime = ChronoUnit.SECONDS.between(Instant.now(), refreshToken.getExpiryDate());
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(Math.max(0, remainingTime))
                .build();
        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("accessToken", accessToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(response);
    }

    private RefreshToken createRefreshToken(DetailedUserDto detailedUserDto) {
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
    
    private User getUserFromRefreshToken(String token) {
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
    
   private void removeAllUserTokens(UUID userId) {
        refreshTokenRepo.deleteByUserId(userId);
    }

    private boolean checkIfUserHasActiveRefreshToken(UUID userId) {
        RefreshToken refreshToken = refreshTokenRepo.findByUserId(userId);
        return refreshToken != null && refreshToken.getExpiryDate().isAfter(Instant.now());
    }

    private void verifyRefreshToken(String token) {
       refreshTokenRepo.findByToken(token)
                .filter(rt -> !rt.isRevoked() && rt.getExpiryDate().isAfter(Instant.now()))
                .orElseThrow(() -> new RefreshTokenExpiredException("Refresh token expired or invalid."));
    }

    private RefreshToken getUserActiveRefreshToken(UUID userId) {
        return refreshTokenRepo.findByUserId(userId);
    }

    private String generateSecureRefreshToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
}

