package com.example.kvitter.controllers;

import com.example.kvitter.dtos.RekvittRequestDto;
import com.example.kvitter.dtos.RemoveRekvittRequestDto;
import com.example.kvitter.services.RekvittService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class RekvittController {
    
    private final RekvittService rekvittService;


    @PostMapping("/postRekvitt")
    public ResponseEntity<Map<String, String>> postRekvitt(@RequestBody RekvittRequestDto request, @RequestHeader("Authorization") String token) {
        rekvittService.addRekvitt(request.kvitterId(), token);
        return ResponseEntity.ok(Collections.singletonMap("message", "Rekvitt posted!"));
    }
    
    @DeleteMapping("/removeRekvitt")
    public ResponseEntity<Map<String, String>> removeRekvitt(@RequestBody RemoveRekvittRequestDto request) {
        rekvittService.removeRekvitt(request.rekvittId());
        return ResponseEntity.ok(Collections.singletonMap("message", "Rekvitt deleted!"));
    }
}
