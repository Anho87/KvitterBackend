package com.example.kvitter.controllers;

import com.example.kvitter.configs.UserAuthProvider;
import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.dtos.KvitterRequest;
import com.example.kvitter.entities.Hashtag;
import com.example.kvitter.exceptions.AppException;
import com.example.kvitter.services.KvitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class KvitterController {
    
    private final KvitterService kvitterService;
    private final UserAuthProvider userAuthProvider;
    
    @PostMapping("/postKvitter")
    public void postKvitter(@RequestBody KvitterRequest request, @RequestHeader("Authorization") String token){
        try{
            String message = request.getMessage();
            List<Hashtag> hashtags = request.getHashtags();
            Authentication authentication = userAuthProvider.validateTokenStrongly(token.replace("Bearer ", ""));
            DetailedUserDto user = (DetailedUserDto) authentication.getPrincipal();
            UUID userId = user.getId();
            kvitterService.addKvitter(message,userId,hashtags);
        }catch (AppException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Token", e);
        }
    }
}
