package com.example.kvitter.controllers;

import com.example.kvitter.configs.UserAuthProvider;
import com.example.kvitter.dtos.DetailedKvitterDto;
import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.dtos.KvitterRequest;
import com.example.kvitter.dtos.RemoveKvitterRequest;
import com.example.kvitter.entities.Hashtag;
import com.example.kvitter.exceptions.ExpiredTokenException;
import com.example.kvitter.services.HashtagService;
import com.example.kvitter.services.KvitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class KvitterController {

    private final KvitterService kvitterService;
    private final UserAuthProvider userAuthProvider;
    private final HashtagService hashtagService;

    @PostMapping("/postKvitter")
    public void postKvitter(@RequestBody KvitterRequest request, @RequestHeader("Authorization") String token) {
        try {
            String message = request.getMessage();
            List<String> hashtags = request.getHashtags();
            List<Hashtag> hashtagList = new ArrayList<>();
            for (String hashtag : hashtags) {
                Hashtag hashtagTemp = hashtagService.addHashTag(hashtag);
                hashtagList.add(hashtagTemp);
            }
            Authentication authentication = userAuthProvider.validateTokenStrongly(token.replace("Bearer ", ""));
            DetailedUserDto user = (DetailedUserDto) authentication.getPrincipal();
            UUID userId = user.getId();
            kvitterService.addKvitter(message, userId, hashtagList);
        } catch (ExpiredTokenException e) {
            throw new ExpiredTokenException("Access token expired", e);
        }
    }

    @DeleteMapping("/removeKvitter")
    public void removeKvitter(@RequestBody RemoveKvitterRequest request, @RequestHeader("Authorization") String token) {
        try {
            String id = request.getId();
            kvitterService.removeKvitter(id);
        } catch (ExpiredTokenException e) {
            throw new ExpiredTokenException("Access token expired", e);
        }
    }

    @GetMapping("/startPageKvitterList")
    public List<DetailedKvitterDto> getAllDetailedKvittersDTO() {
        return kvitterService.getTenRandomDetailedKvitterDTO();
    }
}
