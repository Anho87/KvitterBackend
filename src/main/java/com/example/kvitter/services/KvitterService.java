package com.example.kvitter.services;

import com.example.kvitter.dtos.DetailedDtoInterface;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class KvitterService {
    private final KvitterRepo kvitterRepo;
    private final UserRepo userRepo;
    private final KvitterMapper kvitterMapper;
    private final HashtagService hashtagService;
    private final UserMapper userMapper;


    public void addKvitter(String message, List<String> hashtags, Boolean isPrivate, DetailedUserDto detailedUserDto) {
        System.out.println(isPrivate);
        List<Hashtag> hashtagList = new ArrayList<>();
        for (String hashtag : hashtags) {
            Hashtag tempHashtag = hashtagService.addHashTag(hashtag);
            hashtagList.add(tempHashtag);
        }
        UUID userId = detailedUserDto.getId();
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Stockholm"));
        Optional<User> optionalUser = userRepo.findById(userId);
        User user = optionalUser.orElseThrow(() -> new RuntimeException("User not found"));
        Kvitter kvitter = Kvitter.builder().message(message).user(user).createdDateAndTime(now.toLocalDateTime()).hashtags(hashtagList).isPrivate(isPrivate).build();
        kvitterRepo.save(kvitter);
    }

    public void removeKvitter(String id) {
        UUID uuid = UUID.fromString(id);
        Kvitter kvitter = kvitterRepo.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Kvitter not found"));
        for (Hashtag hashtag : kvitter.getHashtags()) {
            hashtag.getKvitters().remove(kvitter);
        }
        if (kvitter.getReplies().isEmpty() && kvitter.getRekvitts().isEmpty()) {
            kvitterRepo.deleteKvitterById(uuid);
        }else {
            kvitter.setMessage("Deleted...");
            kvitterRepo.save(kvitter);
        }
    }


    //TODO skriv test dela på denna så det är 3st för lättare testning
    public List<DetailedDtoInterface> getFilteredKvitters(String userName, DetailedUserDto detailedUserDto) {
        User user = userRepo.findByEmail(detailedUserDto.getEmail());
        Optional<User> optionalUser = userRepo.findByUserName(userName);
        User targetUser = userMapper.optionalToUser(optionalUser);
        List<DetailedDtoInterface> detailedInterfaceDtoList;
        if (optionalUser.isEmpty()) {
            System.out.println("Fetching all kvitters, including private ones for followers of logged-in user");
            detailedInterfaceDtoList = mapToInterfaceDtoList(kvitterRepo.getDynamicKvitterList(user.getId()));
        } else if (targetUser.getId() != user.getId()) {
            System.out.println("Fetching kvitters for target user by logged-in user");
            detailedInterfaceDtoList = mapToInterfaceDtoList(kvitterRepo.findAllByTargetUser(targetUser.getId(), user.getId()));
        } else {
            System.out.println("Fetching kvitters for logged-in user");
            detailedInterfaceDtoList = mapToInterfaceDtoList(kvitterRepo.findAllByLoggedInUser(user.getId()));
        }
        return detailedInterfaceDtoList;
    }

    private List<DetailedDtoInterface> mapToInterfaceDtoList(List<Kvitter> kvitterList) {
        return kvitterList.stream().map(kvitterMapper::kvitterToDetailedKvitterDTO).collect(Collectors.toList());
    }

    private List<DetailedKvitterDto> mapToDetailedKvitterDtoList(List<Kvitter> kvitterList) {
        return kvitterList.stream().map(kvitterMapper::kvitterToDetailedKvitterDTO).collect(Collectors.toList());
    }

    //TODO skriv test
    public List<DetailedKvitterDto> getTenLatestKvitterThatIsNotPrivate() {
        return mapToDetailedKvitterDtoList(kvitterRepo.getTenLatestKvitterThatIsNotPrivate());
    }
}
