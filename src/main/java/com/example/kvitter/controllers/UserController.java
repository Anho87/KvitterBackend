package com.example.kvitter.controllers;


import com.example.kvitter.configs.UserAuthProvider;
import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.dtos.FollowUserDto;
import com.example.kvitter.exceptions.ExpiredTokenException;
import com.example.kvitter.services.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor    
public class UserController {

    private final UserService userService;
    private final UserAuthProvider userAuthProvider;
    
    @PostMapping("/followUser")
    public void followUser(@RequestBody FollowUserDto request, @RequestHeader("Authorization") String token) {
        try {
            String email = request.userEmail();
            Authentication authentication = userAuthProvider.validateTokenStrongly(token.replace("Bearer ", ""));
            DetailedUserDto detailedUserDto = (DetailedUserDto) authentication.getPrincipal();
            userService.followUser(email, detailedUserDto);
        } catch (ExpiredTokenException e) {
            throw new ExpiredTokenException("Access token expired", e);
        }
    }
}
