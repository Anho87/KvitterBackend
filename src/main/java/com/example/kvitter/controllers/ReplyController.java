package com.example.kvitter.controllers;

import com.example.kvitter.dtos.RemoveKvitterRequest;
import com.example.kvitter.dtos.ReplyRequestDto;
import com.example.kvitter.services.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ReplyController {
    
    private final ReplyService replyService;
    
    @PostMapping("/postReply")
    public ResponseEntity<Map<String, String>> postReply(@RequestBody ReplyRequestDto request, @RequestHeader("Authorization") String token){
        replyService.addReply(request.message(),request.kvitterId(),request.parentReplyId(),token);
        return ResponseEntity.ok(Collections.singletonMap("message", "Reply posted!"));
    }

    @DeleteMapping("/removeReply")
    public ResponseEntity<Map<String, String>> removeReply(@RequestBody RemoveKvitterRequest request, @RequestHeader("Authorization") String token) {
            replyService.removeReply(request.id(), token);
        return ResponseEntity.ok(Collections.singletonMap("message", "Reply deleted!"));
    }
}
