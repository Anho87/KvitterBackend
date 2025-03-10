package com.example.kvitter.controllers;

import com.example.kvitter.configs.UserAuthProvider;
import com.example.kvitter.dtos.CredentialsDto;
import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.dtos.RefreshTokenDto;
import com.example.kvitter.dtos.SignUpDto;
import com.example.kvitter.exceptions.AppException;
import com.example.kvitter.services.RefreshTokenService;
import com.example.kvitter.services.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserAuthProvider userAuthProvider;
    private final RefreshTokenService refreshTokenService;


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody CredentialsDto credentialsDTO) {
        String lowerCaseUsername = credentialsDTO.userName().toLowerCase();
        DetailedUserDto user = userService.login(new CredentialsDto(lowerCaseUsername, credentialsDTO.password()));
        Map<String,Object> response = generateAuthResponse(user);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody SignUpDto signUpDto) {
        String lowerCaseUsername = signUpDto.userName().toLowerCase();
        String lowerCaseEmail = signUpDto.email().toLowerCase();
        DetailedUserDto user = userService.register(new SignUpDto(lowerCaseEmail, lowerCaseUsername, signUpDto.password()));
        Map<String,Object> response = generateAuthResponse(user);
        return ResponseEntity.created(URI.create("/users/" + user.getId())).body(response);
    }

//    @PostMapping("/refreshToken")
//    public ResponseEntity<DetailedUserDto> refreshToken(@RequestBody RefreshTokenDto refreshToken) {
//        try{
//            
//            
//        }catch (AppException e) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Refresh Token", e);
//        }
//    }


    public Map<String, Object> generateAuthResponse(DetailedUserDto user) {
        String accessToken = userAuthProvider.createToken(user);
        Boolean refreshTokenExists = refreshTokenService.checkIfUserHasActiveRefreshToken(user.getId());
//        String refreshToken = refreshTokenService.createRefreshToken(user);
        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        return response;
    }

}
