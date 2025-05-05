package com.example.kvitter.controllers;

import com.example.kvitter.dtos.*;
import com.example.kvitter.services.KvitterService;
import com.example.kvitter.services.RekvittService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class KvitterController {

    private final KvitterService kvitterService;
    private final RekvittService rekvittService;


    @PostMapping("/postKvitter")
    public ResponseEntity<Map<String, String>> postKvitter(@RequestBody KvitterRequest request, @RequestHeader("Authorization") String token) {
        kvitterService.addKvitter(request, token);
        return ResponseEntity.ok(Collections.singletonMap("message", "Kvitter posted!"));
    }

    @DeleteMapping("/removeKvitter")
    public ResponseEntity<Map<String, String>> removeKvitter(@RequestBody RemoveKvitterRequest request, @RequestHeader("Authorization") String token) {
        kvitterService.removeKvitter(request.id(), token);
        return ResponseEntity.ok(Collections.singletonMap("message", "Kvitter deleted!"));
    }

    @GetMapping("/search")
    public List<DetailedDtoInterface> getSearchedKvitterDtoList(@RequestParam String category, @RequestParam String searched, @RequestHeader("Authorization") String token) {
        return kvitterService.getSearchedKvitters(category, searched, token);
    }


    @GetMapping("/kvitterList")
    public List<DetailedDtoInterface> getDynamicDetailedKvitterDtoList(@RequestParam String filterOption, @RequestParam(required = false) String userName, @RequestHeader("Authorization") String token) {
        List<DetailedDtoInterface> detailedInterfaceDtoList = new ArrayList<>();
        if (filterOption.equalsIgnoreCase("Following") || filterOption.equalsIgnoreCase("User-info") || filterOption.equalsIgnoreCase("MyActivity")) {
            detailedInterfaceDtoList.addAll(rekvittService.getFilteredRekvitts(filterOption, userName, token));
        }
        detailedInterfaceDtoList.addAll(kvitterService.getFilteredKvitters(filterOption, userName, token));
        if (!filterOption.equalsIgnoreCase("Popular")) {
            detailedInterfaceDtoList.sort(Comparator.comparing(DetailedDtoInterface::getCreatedDateAndTime).reversed());
        }
        return detailedInterfaceDtoList;
    }
    
    @GetMapping("/welcomePageKvitterList")
    public List<DetailedKvitterDto> getWelcomePageKvitter() {
        return kvitterService.getTenLatestKvitterThatIsNotPrivate();
    }
}
