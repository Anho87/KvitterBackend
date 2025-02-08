package com.example.kvitter.Kvitter;

import com.example.kvitter.Hashtag.Hashtag;
import com.example.kvitter.Hashtag.MiniHashtagDTO;
import com.example.kvitter.User.MiniUserDTO;
import com.example.kvitter.User.User;
import com.example.kvitter.User.UserRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class KvitterService {
    private final KvitterRepo kvitterRepo;
    private final UserRepo userRepo;
    
    public KvitterService(KvitterRepo kvitterRepo, UserRepo userRepo) {
        this.kvitterRepo = kvitterRepo;
        this.userRepo = userRepo;
    }

    private DetailedKvitterDTO kvitterToDetailedKvitterDTO(Kvitter kvitter) {
        MiniUserDTO miniUserDTO = new MiniUserDTO();
        List<MiniHashtagDTO> miniHashtagDTOList = new ArrayList<>();
        if (kvitter.getUser() != null){
            miniUserDTO = MiniUserDTO.builder().id(kvitter.getUser().getId()).email(kvitter.getUser().getEmail()).firstName(kvitter.getUser().getFirstName()).lastName(kvitter.getUser().getLastName()).build();
        }
        if(kvitter.getHashtags() != null){
            for (Hashtag hashtag: kvitter.getHashtags()) {
                MiniHashtagDTO miniHashtagDTO = MiniHashtagDTO.builder().id(hashtag.getId()).hashtag(hashtag.getHashtag()).build();
                miniHashtagDTOList.add(miniHashtagDTO);
            }
        }
        return DetailedKvitterDTO.builder().id(kvitter.getId()).message(kvitter.getMessage()).miniUserDTO(miniUserDTO).createdDateAndTime(kvitter.getCreatedDateAndTime()).hashtags(miniHashtagDTOList).build();
    }

    public void addKvitter(String message, UUID id, List<Hashtag> hashtags) {
        LocalDateTime localDateTime = LocalDateTime.now();
        Optional<User> optionalUser = userRepo.findById(id);
        User user = optionalUser.orElseThrow(() -> new RuntimeException("User not found"));
        Kvitter kvitter = Kvitter.builder().message(message).user(user).createdDateAndTime(localDateTime).hashtags(hashtags).build();
        kvitterRepo.save(kvitter);
    }

    private MiniKvitterDTO kvitterToMiniKvitterDTO(Kvitter kvitter) {
        return MiniKvitterDTO.builder().id(kvitter.getId()).message(kvitter.getMessage()).createdDateAndTime(kvitter.getCreatedDateAndTime()).build();
    }
    
    
    public List<DetailedKvitterDTO> getAllDetailedKvittersDTO(){
        List<Kvitter> kvitterList = kvitterRepo.findAll();
        List<DetailedKvitterDTO> detailedKvitterDTOSList = new ArrayList<>();
        for (Kvitter kvitter: kvitterList ) {
            detailedKvitterDTOSList.add(kvitterToDetailedKvitterDTO(kvitter));
        }
        return detailedKvitterDTOSList;
    }
}
