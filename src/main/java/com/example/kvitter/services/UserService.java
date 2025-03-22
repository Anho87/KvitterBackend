package com.example.kvitter.services;

import com.example.kvitter.dtos.CredentialsDto;
import com.example.kvitter.dtos.SignUpDto;
import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.entities.User;
import com.example.kvitter.exceptions.AppException;
import com.example.kvitter.mappers.UserMapper;
import com.example.kvitter.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.util.Optional;

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
        
        if (oUser.isPresent()){
            throw new AppException("Username already exists", HttpStatus.BAD_REQUEST);
        }
        
        User user = userMapper.signUpToUser(signUpDto);
        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(signUpDto.password())));
        User savedUser =  userRepo.save(user);
        return userMapper.userToDetailedUserDTO(savedUser);
        
    }
    
    
    private Boolean checkEmailAlreadyUsed(String email) {
        User user = userRepo.findByEmail(email);
        return user == null;
    }

    //TODO skriv test
    public void addUser(String email, String password, String userName) {
        if (checkEmailAlreadyUsed(email)) {
            String hash = passwordEncoder.encode(password);
            User user = User.builder().email(email).password(hash).userName(userName.toLowerCase()).build();
            userRepo.save(user);
        }
    }
    
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public DetailedUserDto getDetailedUserDTOByEmail(String email) {
        return userMapper.userToDetailedUserDTO(userRepo.findByEmail(email));
    }
    
}
