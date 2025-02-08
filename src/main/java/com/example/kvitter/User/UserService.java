package com.example.kvitter.User;

import com.example.kvitter.Kvitter.Kvitter;
import com.example.kvitter.Kvitter.MiniKvitterDTO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    private DetailedUserDTO userToUserDTO(User user) {
        List<MiniKvitterDTO> kvitterDTOList = new ArrayList<>();
        if (user.getKvitterList() != null) {
            for (Kvitter kvitter : user.getKvitterList()) {
                MiniKvitterDTO kvitterDTO = MiniKvitterDTO.builder().id(kvitter.getId()).message(kvitter.getMessage()).createdDateAndTime(kvitter.getCreatedDateAndTime()).build();
                kvitterDTOList.add(kvitterDTO);
            }
        }

        return DetailedUserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .kvitterList(kvitterDTOList)
                .build();
    }
    
    private Boolean checkEmailAlreadyUsed(String email) {
        User user = userRepo.findByEmail(email);
        return user == null;
    }

    
    public void addUser(String email, String password, String firstName, String lastName) {
        if (checkEmailAlreadyUsed(email)) {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            String hash = bCryptPasswordEncoder.encode(password);
            User user = User.builder().email(email).password(hash).firstName(firstName).lastName(lastName).build();
            userRepo.save(user);
        }
    }
    
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public DetailedUserDTO getDetailedUserDTOByEmail(String email) {
        return userToUserDTO(userRepo.findByEmail(email));
    }
    
}
