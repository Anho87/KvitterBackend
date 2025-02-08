package com.example.kvitter.index;

import com.example.kvitter.Kvitter.DetailedKvitterDTO;
import com.example.kvitter.Kvitter.KvitterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class IndexController {

    private final KvitterService kvitterService;

    public IndexController(KvitterService kvitterService) {
        this.kvitterService = kvitterService;
    }

    @GetMapping("/index")
    public List<DetailedKvitterDTO> getAllDetailedKvittersDTO() {
        return kvitterService.getAllDetailedKvittersDTO();
    }
}
