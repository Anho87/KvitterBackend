package com.example.kvitter.controllers;


import com.example.kvitter.configs.UserAuthProvider;
import com.example.kvitter.dtos.*;
import com.example.kvitter.exceptions.ExpiredTokenException;
import com.example.kvitter.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor    
public class UserController {

    private final UserService userService;
    private final UserAuthProvider userAuthProvider;
    
    @PostMapping("/followUser")
    public void followUser(@RequestBody FollowUserRequestDto request, @RequestHeader("Authorization") String token) {
        try {
            String email = request.userEmail();
            Authentication authentication = userAuthProvider.validateTokenStrongly(token.replace("Bearer ", ""));
            DetailedUserDto detailedUserDto = (DetailedUserDto) authentication.getPrincipal();
            userService.followUser(email, detailedUserDto);
        } catch (ExpiredTokenException e) {
            throw new ExpiredTokenException("Access token expired", e);
        }
    }
    
    @DeleteMapping("unFollowUser")
    public void unFollowUser(@RequestBody UnFollowUserRequestDto request, @RequestHeader("Authorization") String token){
        try {
            String email = request.userEmail();
            Authentication authentication = userAuthProvider.validateTokenStrongly(token.replace("Bearer ", ""));
            DetailedUserDto detailedUserDto = (DetailedUserDto) authentication.getPrincipal();
            userService.unFollowUser(email, detailedUserDto);
        }catch (ExpiredTokenException e) {
            throw new ExpiredTokenException("Access token expired", e);
        }
    }

    @PostMapping("/upvoteKvitter")
    public void upvoteKvitter(@RequestBody UpvoteKvitterRequestDto request, @RequestHeader("Authorization") String token){
        try{
            String kvitterId = request.kvitterId();
            Authentication authentication = userAuthProvider.validateToken(token.replace("Bearer ", ""));
            DetailedUserDto detailedUserDto = (DetailedUserDto) authentication.getPrincipal();
            userService.upvoteKvitter(kvitterId,detailedUserDto);
        }catch (ExpiredTokenException e) {
            throw new ExpiredTokenException("Access token expired", e);
        }
    }

    @DeleteMapping("/removeUpvoteOnKvitter")
    public void removeUpvoteOnKvitter(@RequestBody RemoveUpvoteOnKvitterRequestDto request, @RequestHeader("Authorization") String token){
        try{
            String kvitterId = request.kvitterId();
            Authentication authentication = userAuthProvider.validateToken(token.replace("Bearer ", ""));
            DetailedUserDto detailedUserDto = (DetailedUserDto) authentication.getPrincipal();
            userService.removeUpvoteOnKvitter(kvitterId,detailedUserDto);
        }catch (ExpiredTokenException e) {
            throw new ExpiredTokenException("Access token expired", e);
        }
    }
}
