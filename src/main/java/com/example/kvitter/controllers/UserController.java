package com.example.kvitter.controllers;


import com.example.kvitter.dtos.*;
import com.example.kvitter.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/followUser")
    public ResponseEntity<Map<String, String>> followUser(@RequestBody FollowUserRequestDto request, @RequestHeader("Authorization") String token) {
        userService.followUser(request.userEmail(), token);
        return ResponseEntity.ok(Collections.singletonMap("message", " followed!"));
    }

    @DeleteMapping("unFollowUser")
    public ResponseEntity<Map<String, String>> unFollowUser(@RequestBody UnFollowUserRequestDto request, @RequestHeader("Authorization") String token) {
        userService.unFollowUser(request.userEmail(), token);
        return ResponseEntity.ok(Collections.singletonMap("message", " unfollowed!"));

    }

    @PostMapping("/upvoteKvitter")
    public ResponseEntity<Map<String, String>> upvoteKvitter(@RequestBody UpvoteKvitterRequestDto request, @RequestHeader("Authorization") String token) {
        userService.upvoteKvitter(request.kvitterId(), token);
        return ResponseEntity.ok(Collections.singletonMap("message", "Kvitter upvoted!"));

    }

    @DeleteMapping("/removeUpvoteOnKvitter")
    public ResponseEntity<Map<String, String>> removeUpvoteOnKvitter(@RequestBody RemoveUpvoteOnKvitterRequestDto request, @RequestHeader("Authorization") String token) {
        userService.removeUpvoteOnKvitter(request.kvitterId(), token);
        return ResponseEntity.ok(Collections.singletonMap("message", "Upvote removed!"));
    }
    
    @GetMapping("/getUserFollowing")
    public List<MiniUserDto> getUserFollowing(@RequestHeader("Authorization") String token){
        return userService.getUserFollowing(token);
    }
    
    @GetMapping("/getUserInfo")
    public DetailedUserDto getUserInfo(@RequestParam String userName, @RequestHeader("Authorization") String token){
        return userService.getUserInfo(userName, token);
    }
}
