package com.example.kvitter.services;

import com.example.kvitter.dtos.CredentialsDto;
import com.example.kvitter.dtos.SignUpDto;
import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.entities.Kvitter;
import com.example.kvitter.entities.User;
import com.example.kvitter.exceptions.AppException;
import com.example.kvitter.mappers.UserMapper;
import com.example.kvitter.repos.KvitterRepo;
import com.example.kvitter.repos.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final KvitterRepo kvitterRepo;
    private final AuthService authService;
    
    public DetailedUserDto login(CredentialsDto credentialsDTO){
        User user = userRepo.findByUserNameIgnoreCase(credentialsDTO.userName())
                .orElseThrow(() -> new AppException("Unkown user", HttpStatus.NOT_FOUND));
        
        if(passwordEncoder.matches(CharBuffer.wrap(credentialsDTO.password()),
                user.getPassword())){
            userRepo.save(user);
            return userMapper.userToDetailedUserDTO(user);
        }
        throw new AppException("Invalid password", HttpStatus.BAD_REQUEST);
    }
    
    public DetailedUserDto register(SignUpDto signUpDto){
        Optional<User> oUser = userRepo.findByUserNameIgnoreCase(signUpDto.userName());
        User checkMail = userRepo.findByEmailIgnoreCase(signUpDto.email());
        
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
    
   
    public void followUser(String email, String token){
        DetailedUserDto detailedUserDto = authService.getUserFromToken(token);
        User toBeFollowedUser = userRepo.findByEmailIgnoreCase(email);
        User wantToFollowUser = userRepo.findByEmailIgnoreCase(detailedUserDto.getEmail());
        boolean userExists = wantToFollowUser.getFollowing().stream().anyMatch(user -> user.getId() == toBeFollowedUser.getId());
        if(!userExists){
            wantToFollowUser.getFollowing().add(toBeFollowedUser);
            userRepo.save(wantToFollowUser);
        }else{
            throw new AppException("Already following that user", HttpStatus.BAD_REQUEST);
        }
    }
    
  
    public void unFollowUser(String email, String token) {
        DetailedUserDto detailedUserDto = authService.getUserFromToken(token);
        User toBeUnFollowedUser = userRepo.findByEmailIgnoreCase(email);
        User wantToUnFollowUser = userRepo.findByEmailIgnoreCase(detailedUserDto.getEmail());
        boolean userExists = wantToUnFollowUser.getFollowing().stream().anyMatch(user -> user.getId() == toBeUnFollowedUser.getId());
        if (userExists){
            System.out.println("User: " + wantToUnFollowUser.getUserName() + " is unfollowing: " + toBeUnFollowedUser.getUserName());
            wantToUnFollowUser.getFollowing().remove(toBeUnFollowedUser);
            userRepo.save(wantToUnFollowUser);
        }else{
            throw new AppException("Not following that user", HttpStatus.BAD_REQUEST);
        }
        
    }
    
    
    public void upvoteKvitter(String kvitterId, String token){
        DetailedUserDto detailedUserDto = authService.getUserFromToken(token);
        UUID uuid = UUID.fromString(kvitterId);
        Kvitter kvitter = kvitterRepo.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Kvitter not found"));
        User user = userRepo.findByEmailIgnoreCase(detailedUserDto.getEmail());
        boolean alreadyUpvoted = user.getLikes().stream().anyMatch(kvitterInList -> kvitterInList.getId() == kvitter.getId());
        if (!alreadyUpvoted){
        user.getLikes().add(kvitter);
            userRepo.save(user);
        }else {
            throw new AppException("User already upvoted this kvitter", HttpStatus.BAD_REQUEST);
        }
    }

 
    public void removeUpvoteOnKvitter(String kvitterId, String token){
        DetailedUserDto detailedUserDto = authService.getUserFromToken(token);
        UUID uuid = UUID.fromString(kvitterId);
        Kvitter kvitter = kvitterRepo.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Kvitter not found"));
        User user = userRepo.findByEmailIgnoreCase(detailedUserDto.getEmail());
        boolean alreadyUpvoted = user.getLikes().stream().anyMatch(kvitterInList -> kvitterInList.getId() == kvitter.getId());
        if (alreadyUpvoted){
        user.getLikes().remove(kvitter);
            userRepo.save(user);
        }else {
            throw new AppException("User hasn't upvoted this kvitter yet", HttpStatus.BAD_REQUEST);
        }
    }
    
}
