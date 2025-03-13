package com.example.kvitter.controllers;

import com.example.kvitter.configs.UserAuthProvider;
import com.example.kvitter.dtos.CredentialsDto;
import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.dtos.SignUpDto;
import com.example.kvitter.entities.RefreshToken;
import com.example.kvitter.entities.User;
import com.example.kvitter.exceptions.RefreshTokenExpiredException;
import com.example.kvitter.mappers.UserMapper;
import com.example.kvitter.services.RefreshTokenService;
import com.example.kvitter.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserAuthProvider userAuthProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserMapper userMapper;

    

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody CredentialsDto credentialsDTO) {
        String lowerCaseUsername = credentialsDTO.userName().toLowerCase();
        DetailedUserDto user = userService.login(new CredentialsDto(lowerCaseUsername, credentialsDTO.password()));
        return generateAuthResponse(user);
    }


    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody SignUpDto signUpDto) {
        String lowerCaseUsername = signUpDto.userName().toLowerCase();
        String lowerCaseEmail = signUpDto.email().toLowerCase();
        DetailedUserDto user = userService.register(new SignUpDto(lowerCaseEmail, lowerCaseUsername, signUpDto.password()));
        return generateAuthResponse(user);
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(value = "refreshToken", defaultValue = "") String refreshToken){
        User user = refreshTokenService.getUserFromRefreshToken(refreshToken);
        refreshTokenService.removeAllUserTokens(user.getId());
        ResponseCookie expiredCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
                .build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshAccessToken(@CookieValue(value = "refreshToken", defaultValue = "") String refreshToken) {
        try {
            if (refreshToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Collections.singletonMap("error", "Refresh token is missing."));
            }
            RefreshToken validRefreshToken = refreshTokenService.verifyRefreshToken(refreshToken);
            User user = refreshTokenService.getUserFromRefreshToken(refreshToken);
            String newAccessToken = userAuthProvider.createToken(userMapper.userToDetailedUserDTO(user));
            Map<String, String> response = new HashMap<>();
            response.put("accessToken", newAccessToken);
            return ResponseEntity.ok(response);
        } catch (RefreshTokenExpiredException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "Refresh token expired or invalid."));
        }
    }
    
    private ResponseEntity<Map<String, Object>> generateAuthResponse(DetailedUserDto user) {
        String accessToken = userAuthProvider.createToken(user);
        RefreshToken refreshToken;
        if (!refreshTokenService.checkIfUserHasActiveRefreshToken(user.getId())) {
            refreshToken = refreshTokenService.createRefreshToken(user);
        }else{
            refreshToken = refreshTokenService.getUserActiveRefreshToken(user.getId());
        }
        long remainingTime = ChronoUnit.SECONDS.between(Instant.now(), refreshToken.getExpiryDate());
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
                .httpOnly(true) 
                .secure(true)   
                .sameSite("Strict") 
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

}
