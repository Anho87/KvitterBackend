package com.example.kvitter.controllers;


import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.services.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/user")
    public DetailedUserDto getUserWithEmail(@RequestParam String email){
        return userService.getDetailedUserDTOByEmail(email);
    }
}
