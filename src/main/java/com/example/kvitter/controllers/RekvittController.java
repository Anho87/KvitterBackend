package com.example.kvitter.controllers;

import com.example.kvitter.configs.UserAuthProvider;
import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.dtos.RekvittRequestDto;
import com.example.kvitter.exceptions.ExpiredTokenException;
import com.example.kvitter.services.RekvittService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RekvittController {
    
    private final UserAuthProvider userAuthProvider;
    private final RekvittService rekvittService;
    
    
    @PostMapping("/postRekvitt")
    public void postRekvitt(@RequestBody RekvittRequestDto request, @RequestHeader("Authorization") String token){
        try{
            String kvitterId = request.kvitterId();
            Authentication authentication = userAuthProvider.validateTokenStrongly(token.replace("Bearer ", ""));
            DetailedUserDto detailedUserDto = (DetailedUserDto) authentication.getPrincipal();
            rekvittService.addRekvitt(kvitterId, detailedUserDto);
        } catch (ExpiredTokenException e) {
            throw new ExpiredTokenException("Access token expired", e);
        }
        
    }
}
