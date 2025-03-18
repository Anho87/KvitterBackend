package com.example.kvitter.services;

import com.example.kvitter.entities.Hashtag;
import com.example.kvitter.dtos.MiniHashtagDto;
import com.example.kvitter.dtos.DetailedKvitterDto;
import com.example.kvitter.entities.Kvitter;
import com.example.kvitter.mappers.KvitterMapper;
import com.example.kvitter.repos.KvitterRepo;
import com.example.kvitter.dtos.MiniUserDto;
import com.example.kvitter.entities.User;
import com.example.kvitter.repos.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
    
    public KvitterService(KvitterRepo kvitterRepo, UserRepo userRepo, KvitterMapper kvitterMapper) {
        this.kvitterRepo = kvitterRepo;
        this.userRepo = userRepo;
        this.kvitterMapper = kvitterMapper;
    }

//    private DetailedKvitterDto kvitterToDetailedKvitterDTO(Kvitter kvitter) {
//        MiniUserDto miniUserDTO = new MiniUserDto();
//        List<MiniHashtagDto> miniHashtagDTOList = new ArrayList<>();
//        if (kvitter.getUser() != null){
//            miniUserDTO = MiniUserDto.builder().id(kvitter.getUser().getId()).email(kvitter.getUser().getEmail()).userName(kvitter.getUser().getUserName()).build();
//        }
//        if(kvitter.getHashtags() != null){
//            for (Hashtag hashtag: kvitter.getHashtags()) {
//                MiniHashtagDto miniHashtagDTO = MiniHashtagDto.builder().id(hashtag.getId()).hashtag(hashtag.getHashtag()).build();
//                miniHashtagDTOList.add(miniHashtagDTO);
//            }
//        }
//        return DetailedKvitterDto.builder().id(kvitter.getId()).message(kvitter.getMessage()).miniUserDTO(miniUserDTO).createdDateAndTime(kvitter.getCreatedDateAndTime()).hashtags(miniHashtagDTOList).build();
//    }

    public void addKvitter(String message, UUID id, List<Hashtag> hashtags) {
        LocalDateTime localDateTime = LocalDateTime.now();
        Optional<User> optionalUser = userRepo.findById(id);
        User user = optionalUser.orElseThrow(() -> new RuntimeException("User not found"));
        Kvitter kvitter = Kvitter.builder().message(message).user(user).createdDateAndTime(localDateTime).hashtags(hashtags).build();
        kvitterRepo.save(kvitter);
    }
    
    public void removeKvitter(String id){
        UUID uuid = UUID.fromString(id);
        Kvitter kvitter = kvitterRepo.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Kvitter not found"));
        for (Hashtag hashtag : kvitter.getHashtags()) {
            hashtag.getKvitters().remove(kvitter); 
        }
        kvitterRepo.deleteKvitterById(uuid);
    }
    
    public List<DetailedKvitterDto> getAllDetailedKvittersDTO(){
        List<Kvitter> kvitterList = kvitterRepo.findAll();
        List<DetailedKvitterDto> detailedKvitterDTOSList = new ArrayList<>();
        for (Kvitter kvitter: kvitterList ) {
            detailedKvitterDTOSList.add(kvitterMapper.kvitterToDetailedKvitterDTO(kvitter));
        }
        return detailedKvitterDTOSList;
    }
    
    //TODO: Lägga till test och göra så att det är de mest populära och inte privata
    public List<DetailedKvitterDto> getTenRandomDetailedKvitterDTO(){
        List<Kvitter> kvitterList = kvitterRepo.getAnyTenKvitter();
        List<DetailedKvitterDto> detailedKvitterDTOSList = new ArrayList<>();
        for (Kvitter kvitter: kvitterList ) {
            detailedKvitterDTOSList.add(kvitterMapper.kvitterToDetailedKvitterDTO(kvitter));
        }
        return detailedKvitterDTOSList;
    }
}
