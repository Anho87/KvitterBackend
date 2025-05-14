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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RekvittService {

    private final RekvittRepo rekvittRepo;
    private final KvitterRepo kvitterRepo;
    private final RekvittMapper rekvittMapper;
    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final AuthService authService;

    public void addRekvitt(String kvitterId, String token) {
        DetailedUserDto detailedUserDto = authService.getUserFromToken(token);
        User user = userRepo.findByEmailIgnoreCase(detailedUserDto.getEmail());
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
        } else {
            throw new AppException("You have already rekvitted this kvitter!", HttpStatus.BAD_REQUEST);
        }
    }

    public void removeRekvitt(String rekvittId) {
        UUID uuid = UUID.fromString(rekvittId);
        Rekvitt rekvitt = rekvittRepo.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Rekvitt not found"));
        rekvittRepo.deleteRekvittByById(rekvitt.getId());
    }
    
    public List<DetailedDtoInterface> getFilteredRekvitts(String filterOption, String userName, String token) {
        System.out.println("filterOption: " + filterOption);
        System.out.println("username: " + userName);
        DetailedUserDto detailedUserDto = authService.getUserFromToken(token);
        String toLowerCaseFilterOption = filterOption.toLowerCase();
        User user = userRepo.findByEmailIgnoreCase(detailedUserDto.getEmail());
        Optional<User> optionalUser = userRepo.findByUserNameIgnoreCase(userName);
        User targetUser = userMapper.optionalToUser(optionalUser);
        List<DetailedDtoInterface> detailedInterfaceDtoList = new ArrayList<>();
        switch (toLowerCaseFilterOption) {
            case "user-info":
                if (optionalUser.isPresent()) {
                        System.out.println("Fetching rekvitts for target user");
                        detailedInterfaceDtoList = mapToInterfaceDtoList(rekvittRepo.findAllByUserId(targetUser.getId()), user);
                }
                break;
            case "following":
                System.out.println("Fetching kvitters for following");
                detailedInterfaceDtoList = mapToInterfaceDtoList(rekvittRepo.findAllRekvittsByUserFollows(user.getId()), user);
                break;
            case "myactivity":
                System.out.println("Fetching rekvitts for logged in user");
                detailedInterfaceDtoList = mapToInterfaceDtoList(rekvittRepo.findAllByUserId(user.getId()), user);
                break;
            default:
                break;
        }
        return detailedInterfaceDtoList;
    }

    private List<DetailedDtoInterface> mapToInterfaceDtoList(List<Rekvitt> rekvittList, User user) {
        return rekvittList.stream().map(rekvitt -> {
            DetailedRekvittDto dto = rekvittMapper.rekvittToDetailedRekvittDto(rekvitt);
            dto.getOriginalKvitter().setIsFollowing(user.getFollowing().contains(rekvitt.getOriginalKvitter().getUser()));
            dto.getOriginalKvitter().setIsLiked(user.getLikes().contains(rekvitt.getOriginalKvitter()));
            return dto;
        }).collect(Collectors.toList());
    }
}
