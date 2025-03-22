package com.example.kvitter.services;

import com.example.kvitter.configs.UserAuthProvider;
import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.entities.Hashtag;
import com.example.kvitter.dtos.DetailedKvitterDto;
import com.example.kvitter.entities.Kvitter;
import com.example.kvitter.mappers.KvitterMapper;
import com.example.kvitter.mappers.UserMapper;
import com.example.kvitter.repos.KvitterRepo;
import com.example.kvitter.entities.User;
import com.example.kvitter.repos.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class KvitterService {
    private final KvitterRepo kvitterRepo;
    private final UserRepo userRepo;
    private final KvitterMapper kvitterMapper;
    private final UserAuthProvider userAuthProvider;

    public KvitterService(KvitterRepo kvitterRepo, UserRepo userRepo, KvitterMapper kvitterMapper, UserAuthProvider userAuthProvider) {
        this.kvitterRepo = kvitterRepo;
        this.userRepo = userRepo;
        this.kvitterMapper = kvitterMapper;
        this.userAuthProvider = userAuthProvider;
    }

    public void addKvitter(String message, UUID id, List<Hashtag> hashtags, Boolean isPrivate) {
        LocalDateTime localDateTime = LocalDateTime.now();
        Optional<User> optionalUser = userRepo.findById(id);
        User user = optionalUser.orElseThrow(() -> new RuntimeException("User not found"));
        Kvitter kvitter = Kvitter.builder().message(message).user(user).createdDateAndTime(localDateTime).hashtags(hashtags).isPrivate(isPrivate).build();
        kvitterRepo.save(kvitter);
    }

    public void removeKvitter(String id) {
        UUID uuid = UUID.fromString(id);
        Kvitter kvitter = kvitterRepo.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Kvitter not found"));
        for (Hashtag hashtag : kvitter.getHashtags()) {
            hashtag.getKvitters().remove(kvitter);
        }
        kvitterRepo.deleteKvitterById(uuid);
    }


    //TODO skriv test
    public List<DetailedKvitterDto> getFilteredKvitters(String userName, String token) {
        Authentication authentication = userAuthProvider.validateToken(token.replace("Bearer ", ""));
        DetailedUserDto detailedUserDto = (DetailedUserDto) authentication.getPrincipal();
        User user = userRepo.findByEmail(detailedUserDto.getEmail());
        List<Kvitter> kvitterList;
        List<DetailedKvitterDto> detailedKvitterDtoList = new ArrayList<>();
//        System.out.println("user: " + user.getUserName());
//        System.out.println("user2: " + userName);
        if (userName == null) {
            kvitterList = kvitterRepo.getAllKvitterThatIsPublic(user.getId());
            for (Kvitter kvitter : kvitterList) {
                detailedKvitterDtoList.add(kvitterMapper.kvitterToDetailedKvitterDTO(kvitter));
            }
        }
        return detailedKvitterDtoList;
    }


    //TODO skriv test
    public List<DetailedKvitterDto> getTenLatestKvitterThatIsNotPrivate() {
        List<Kvitter> kvitterList = kvitterRepo.getTenLatestKvitterThatIsNotPrivate();
        List<DetailedKvitterDto> detailedKvitterDTOSList = new ArrayList<>();
        for (Kvitter kvitter : kvitterList) {
            detailedKvitterDTOSList.add(kvitterMapper.kvitterToDetailedKvitterDTO(kvitter));
        }
        return detailedKvitterDTOSList;
    }
}
