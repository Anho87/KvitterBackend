package com.example.kvitter.controllers;

import com.example.kvitter.configs.UserAuthProvider;
import com.example.kvitter.dtos.*;
import com.example.kvitter.exceptions.ExpiredTokenException;
import com.example.kvitter.services.KvitterService;
import com.example.kvitter.services.RekvittService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class KvitterController {

    private final KvitterService kvitterService;
    private final UserAuthProvider userAuthProvider;
    private final RekvittService rekvittService;

    @PostMapping("/postKvitter")
    public void postKvitter(@RequestBody KvitterRequest request, @RequestHeader("Authorization") String token) {
        try {
            String message = request.message();
            List<String> hashtags = request.hashtags();
            Boolean isPrivate = request.isPrivate();
            Authentication authentication = userAuthProvider.validateTokenStrongly(token.replace("Bearer ", ""));
            DetailedUserDto detailedUserDto = (DetailedUserDto) authentication.getPrincipal();
            kvitterService.addKvitter(message, hashtags, isPrivate, detailedUserDto);
        } catch (ExpiredTokenException e) {
            throw new ExpiredTokenException("Access token expired", e);
        }
    }


    @GetMapping("/kvitterList")
    public List<DetailedDtoInterface> getDynamicDetailedKvitterDtoList(@RequestParam(required = false) String userName, @RequestHeader("Authorization") String token) {
        Authentication authentication = userAuthProvider.validateToken(token.replace("Bearer ", ""));
        DetailedUserDto detailedUserDto = (DetailedUserDto) authentication.getPrincipal();
        List<DetailedDtoInterface> detailedInterfaceDtoList = new ArrayList<>();
        detailedInterfaceDtoList.addAll(rekvittService.getRekvitts(userName,detailedUserDto));
        detailedInterfaceDtoList.addAll(kvitterService.getFilteredKvitters(userName, detailedUserDto));
        detailedInterfaceDtoList.sort(Comparator.comparing(DetailedDtoInterface::getCreatedDateAndTime).reversed());
        return detailedInterfaceDtoList;
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
