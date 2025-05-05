package com.example.kvitter.controllers;

import com.example.kvitter.dtos.CredentialsDto;
import com.example.kvitter.dtos.SignUpDto;
import com.example.kvitter.services.RefreshTokenService;
import com.example.kvitter.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
   

    

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody CredentialsDto credentialsDTO) {
        return refreshTokenService.buildAuthResponseWithTokens(userService.login(new CredentialsDto(credentialsDTO.userName(), credentialsDTO.password())));
    }


    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody SignUpDto signUpDto) {
        return refreshTokenService.buildAuthResponseWithTokens(userService.register(new SignUpDto(signUpDto.email(), signUpDto.userName(), signUpDto.password())));
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(value = "refreshToken", defaultValue = "") String refreshToken){
        return refreshTokenService.logout(refreshToken);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshAccessToken(@CookieValue(value = "refreshToken", defaultValue = "") String refreshToken) {
        return refreshTokenService.refreshAccessToken(refreshToken);

    }
    
    
}
