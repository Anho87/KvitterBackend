package com.example.kvitter.services;

import com.example.kvitter.configs.UserAuthProvider;
import com.example.kvitter.dtos.DetailedUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserAuthProvider userAuthProvider;

    
    public DetailedUserDto getUserFromToken(String token) {
        Authentication authentication = userAuthProvider.validateTokenStrongly(token.replace("Bearer ", ""));
        return (DetailedUserDto) authentication.getPrincipal();
    }
}
