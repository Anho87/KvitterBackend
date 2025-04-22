package com.example.kvitter.services;

import com.example.kvitter.dtos.DetailedDtoInterface;
import com.example.kvitter.dtos.DetailedRekvittDto;
import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.entities.Kvitter;
import com.example.kvitter.entities.Rekvitt;
import com.example.kvitter.entities.User;
import com.example.kvitter.exceptions.AppException;
import com.example.kvitter.mappers.RekvittMapper;
import com.example.kvitter.mappers.UserMapper;
import com.example.kvitter.repos.KvitterRepo;
import com.example.kvitter.repos.RekvittRepo;
import com.example.kvitter.repos.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RekvittService {

    private final RekvittRepo rekvittRepo;
    private final UserMapper userMapper;
    private final KvitterRepo kvitterRepo;
    private final RekvittMapper rekvittMapper;
    private final UserRepo userRepo;

    //TODO skriv test
    public void addRekvitt(String kvitterId, DetailedUserDto detailedUserDto) {
        User user = userRepo.findByEmail(detailedUserDto.getEmail());
        UUID uuid = UUID.fromString(kvitterId);
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Stockholm"));
        Kvitter kvitter = kvitterRepo.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Kvitter not found"));
        boolean rekvittExists = user.getRekvitts().stream()
                .anyMatch(rekvitt -> rekvitt.getOriginalKvitter().getId() == kvitter.getId());
        if (!rekvittExists) {
            Rekvitt rekvitt = Rekvitt.builder()
                    .originalKvitter(kvitter)
                    .user(user)
                    .createdDateAndTime(now.toLocalDateTime())
                    .build();
            rekvittRepo.save(rekvitt);
        }else{
            throw new AppException("User already rekvitted this kvitter", HttpStatus.BAD_REQUEST);
        }
    }

    //TODO skriv test
    public void removeRekvitt(String rekvittId) {
        UUID uuid = UUID.fromString(rekvittId);
        Rekvitt rekvitt = rekvittRepo.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Rekvitt not found"));
        rekvittRepo.deleteRekvittByById(rekvitt.getId());
    }

    //TODO skriv test
    public List<DetailedDtoInterface> getRekvitts(String userName, DetailedUserDto detailedUserDto) {
        User user = userRepo.findByEmail(detailedUserDto.getEmail());
        System.out.println("Fetching rekvitts for logged in user");
        return mapToInterfaceDto(rekvittRepo.getRekvittsByFollowedByAndUser(user.getId()), user);
    }

    private List<DetailedDtoInterface> mapToInterfaceDto(List<Rekvitt> rekvittList, User user) {
        return rekvittList.stream().map(rekvitt -> {
            DetailedRekvittDto dto = rekvittMapper.rekvittToDetailedRekvittDto(rekvitt);
            dto.getOriginalKvitter().setIsFollowing(user.getFollowing().contains(rekvitt.getOriginalKvitter().getUser()));
            dto.getOriginalKvitter().setIsLiked(user.getLikes().contains(rekvitt.getOriginalKvitter()));
            return dto;
        }).collect(Collectors.toList());
    }
}
