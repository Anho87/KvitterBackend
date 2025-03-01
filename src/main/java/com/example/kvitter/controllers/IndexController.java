package com.example.kvitter.controllers;

import com.example.kvitter.dtos.DetailedKvitterDto;
import com.example.kvitter.services.KvitterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
public class IndexController {

    private final KvitterService kvitterService;

    public IndexController(KvitterService kvitterService) {
        this.kvitterService = kvitterService;
    }

    @GetMapping("/index")
    public List<DetailedKvitterDto> getAllDetailedKvittersDTO() {
        List<DetailedKvitterDto> kvitterList = kvitterService.getAllDetailedKvittersDTO();
        Collections.reverse(kvitterList);
        return kvitterList;
    }
}
