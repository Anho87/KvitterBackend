package com.example.kvitter.controllers;

import com.example.kvitter.configs.UserAuthProvider;
import com.example.kvitter.dtos.CredentialsDto;
import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.dtos.SignUpDto;
import com.example.kvitter.services.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserAuthProvider userAuthProvider;


    @PostMapping("/login")
    public ResponseEntity<DetailedUserDto> login(@RequestBody CredentialsDto credentialsDTO) {
        String lowerCaseUsername = credentialsDTO.userName().toLowerCase();
        DetailedUserDto user = userService.login(new CredentialsDto(lowerCaseUsername, credentialsDTO.password()));
        user.setToken(userAuthProvider.createToken(user));
        return ResponseEntity.ok(user);
    }


    @PostMapping("/register")
    public ResponseEntity<DetailedUserDto> register(@RequestBody SignUpDto signUpDto) {
        String lowerCaseUsername = signUpDto.userName().toLowerCase();
        String lowerCaseEmail = signUpDto.email().toLowerCase();
        DetailedUserDto user = userService.register(new SignUpDto(lowerCaseEmail,lowerCaseUsername, signUpDto.password()));
        user.setToken(userAuthProvider.createToken(user));
        return ResponseEntity.created(URI.create("/users/" + user.getId())).body(user);
    }
}
