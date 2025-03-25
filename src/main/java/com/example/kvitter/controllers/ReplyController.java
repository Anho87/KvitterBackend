package com.example.kvitter.controllers;

import com.example.kvitter.configs.UserAuthProvider;
import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.dtos.RemoveKvitterRequest;
import com.example.kvitter.dtos.ReplyRequestDto;
import com.example.kvitter.exceptions.ExpiredTokenException;
import com.example.kvitter.services.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ReplyController {
    
    private final ReplyService replyService;
    private final UserAuthProvider userAuthProvider;
    
    @PostMapping("/postReply")
    public void postReply(@RequestBody ReplyRequestDto request, @RequestHeader("Authorization") String token){
        try{
            String message = request.message();
            UUID kvitterId = request.kvitterId();
            UUID parentReplyId = request.parentReplyId();
            Authentication authentication = userAuthProvider.validateTokenStrongly(token.replace("Bearer ", ""));
            DetailedUserDto detailedUserDto = (DetailedUserDto) authentication.getPrincipal();
            replyService.addReply(message,kvitterId,parentReplyId,detailedUserDto);
        }  catch (ExpiredTokenException e) {
            throw new ExpiredTokenException("Access token expired", e);
        }
    }

    @DeleteMapping("/removeReply")
    public void removeKvitter(@RequestBody RemoveKvitterRequest request, @RequestHeader("Authorization") String token) {
        try {
            String id = request.id();
            replyService.removeReply(id);
        } catch (ExpiredTokenException e) {
            throw new ExpiredTokenException("Access token expired", e);
        }
    }
}
