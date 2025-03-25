package com.example.kvitter.services;

import com.example.kvitter.configs.UserAuthProvider;
import com.example.kvitter.dtos.CredentialsDto;
import com.example.kvitter.dtos.SignUpDto;
import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.entities.User;
import com.example.kvitter.exceptions.AppException;
import com.example.kvitter.mappers.UserMapper;
import com.example.kvitter.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    
    public DetailedUserDto login(CredentialsDto credentialsDTO){
        User user = userRepo.findByUserName(credentialsDTO.userName())
                .orElseThrow(() -> new AppException("Unkown user", HttpStatus.NOT_FOUND));
        
        if(passwordEncoder.matches(CharBuffer.wrap(credentialsDTO.password()),
                user.getPassword())){
            userRepo.save(user);
            return userMapper.userToDetailedUserDTO(user);
        }
        throw new AppException("Invalid password", HttpStatus.BAD_REQUEST);
    }
    
    public DetailedUserDto register(SignUpDto signUpDto){
        Optional<User> oUser = userRepo.findByUserName(signUpDto.userName());
        User checkMail = userRepo.findByEmail(signUpDto.email());
        
        if (oUser.isPresent()){
            throw new AppException("Username already exists", HttpStatus.BAD_REQUEST);
        }
        if (checkMail != null){
            throw new AppException("Email already exists", HttpStatus.BAD_REQUEST);
        }
        
        User user = userMapper.signUpToUser(signUpDto);
        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(signUpDto.password())));
        User savedUser =  userRepo.save(user);
        return userMapper.userToDetailedUserDTO(savedUser);
        
    }
    
    //TODO make test
    public void followUser(String email, DetailedUserDto detailedUserDto){
        User toBeFollowedUser = userRepo.findByEmail(email);
        User wantToFollowUser = userRepo.findByEmail(detailedUserDto.getEmail());
        boolean userExists = wantToFollowUser.getFollowing().stream().anyMatch(user -> user.getId() == toBeFollowedUser.getId());
        if(!userExists){
            wantToFollowUser.getFollowing().add(toBeFollowedUser);
            userRepo.save(wantToFollowUser);
        }else{
            throw new AppException("Already following that user", HttpStatus.BAD_REQUEST);
        }
    }
    
}
