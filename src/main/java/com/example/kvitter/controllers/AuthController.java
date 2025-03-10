package com.example.kvitter.controllers;

import com.example.kvitter.configs.UserAuthProvider;
import com.example.kvitter.dtos.CredentialsDto;
import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.dtos.RefreshTokenDto;
import com.example.kvitter.dtos.SignUpDto;
import com.example.kvitter.entities.RefreshToken;
import com.example.kvitter.entities.User;
import com.example.kvitter.exceptions.RefreshTokenExpiredException;
import com.example.kvitter.mappers.UserMapper;
import com.example.kvitter.services.RefreshTokenService;
import com.example.kvitter.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
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
        Map<String, Object> response = generateAuthResponse(user);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody SignUpDto signUpDto) {
        String lowerCaseUsername = signUpDto.userName().toLowerCase();
        String lowerCaseEmail = signUpDto.email().toLowerCase();
        DetailedUserDto user = userService.register(new SignUpDto(lowerCaseEmail, lowerCaseUsername, signUpDto.password()));
        Map<String, Object> response = generateAuthResponse(user);
        return ResponseEntity.created(URI.create("/users/" + user.getId())).body(response);
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshAccessToken(@RequestBody RefreshTokenDto refreshTokenDto) {
        try {
            String refreshToken = refreshTokenDto.refreshToken();
            
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




    public Map<String, Object> generateAuthResponse(DetailedUserDto user) {
        String accessToken = userAuthProvider.createToken(user);
        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("accessToken", accessToken);
        
        if (!refreshTokenService.checkIfUserHasActiveRefreshToken(user.getId())) {
            String refreshToken = refreshTokenService.createRefreshToken(user);
            response.put("refreshToken", refreshToken);
            return response;
        }
        
        RefreshToken refreshToken = refreshTokenService.getUserActiveRefreshToken(user.getId());
        response.put("refreshToken", refreshToken.getToken());
        return response;
    }

}
