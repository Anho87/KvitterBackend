package com.example.kvitter.controllers;

import com.example.kvitter.configs.UserAuthProvider;
import com.example.kvitter.dtos.*;
import com.example.kvitter.exceptions.ExpiredTokenException;
import com.example.kvitter.services.KvitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class KvitterController {

    private final KvitterService kvitterService;
    private final UserAuthProvider userAuthProvider;

    @PostMapping("/postKvitter")
    public void postKvitter(@RequestBody KvitterRequest request, @RequestHeader("Authorization") String token) {
        try {
            String message = request.message();
            List<String> hashtags = request.hashtags();
            Boolean isPrivate = request.isPrivate();
            System.out.println(isPrivate);
            Authentication authentication = userAuthProvider.validateTokenStrongly(token.replace("Bearer ", ""));
            DetailedUserDto detailedUserDto = (DetailedUserDto) authentication.getPrincipal();
            kvitterService.addKvitter(message, hashtags,isPrivate, detailedUserDto);
        } catch (ExpiredTokenException e) {
            throw new ExpiredTokenException("Access token expired", e);
        }
    }

    //TODO make dynamic 
    @GetMapping("/kvitterList")
    public List<DetailedKvitterDto> getDynamicDetailedKvitterDtoList( @RequestParam(required = false) String userName, @RequestHeader("Authorization") String token) {
        Authentication authentication = userAuthProvider.validateToken(token.replace("Bearer ", ""));
        DetailedUserDto detailedUserDto = (DetailedUserDto) authentication.getPrincipal();
        return kvitterService.getFilteredKvitters(userName, detailedUserDto);
    }

    @DeleteMapping("/removeKvitter")
    public void removeKvitter(@RequestBody RemoveKvitterRequest request, @RequestHeader("Authorization") String token) {
        try {
            String id = request.id();
            kvitterService.removeKvitter(id);
        } catch (ExpiredTokenException e) {
            throw new ExpiredTokenException("Access token expired", e);
        }
    }

    @GetMapping("/welcomePageKvitterList")
    public List<DetailedKvitterDto> getWelcomePageKvitter() {
        return kvitterService.getTenLatestKvitterThatIsNotPrivate();
    }
}
