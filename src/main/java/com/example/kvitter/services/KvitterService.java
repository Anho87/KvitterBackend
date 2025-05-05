package com.example.kvitter.services;

import com.example.kvitter.dtos.*;
import com.example.kvitter.entities.Hashtag;
import com.example.kvitter.entities.Kvitter;

import com.example.kvitter.entities.Reply;
import com.example.kvitter.exceptions.AppException;
import com.example.kvitter.mappers.KvitterMapper;
import com.example.kvitter.mappers.ReplyMapper;
import com.example.kvitter.mappers.UserMapper;
import com.example.kvitter.repos.KvitterRepo;
import com.example.kvitter.entities.User;
import com.example.kvitter.repos.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final ReplyMapper replyMapper;
    private final AuthService authService;
    
    public void addKvitter(KvitterRequest request, String token) {
        DetailedUserDto detailedUserDto = authService.getUserFromToken(token);

        if (request.message() == null || request.message().isBlank()) {
            throw new AppException("Message can't be empty.", HttpStatus.BAD_REQUEST);
        }
        
        List<Hashtag> hashtagList = request.hashtags().stream()
                .map(hashtagService::addHashTag)
                .toList();
        
        UUID userId = detailedUserDto.getId();
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Stockholm"));
        Optional<User> optionalUser = userRepo.findById(userId);
        User user = optionalUser.orElseThrow(() -> new EntityNotFoundException("User not found"));
        Kvitter kvitter = Kvitter.builder().message(request.message()).user(user).createdDateAndTime(now.toLocalDateTime()).hashtags(hashtagList).isPrivate(request.isPrivate()).isActive(true).build();
        kvitterRepo.save(kvitter);
    }

    public void removeKvitter(String id, String token) {
        authService.getUserFromToken(token);
        UUID uuid = UUID.fromString(id);
        Kvitter kvitter = kvitterRepo.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Kvitter not found"));
        for (Hashtag hashtag : kvitter.getHashtags()) {
            hashtag.getKvitters().remove(kvitter);
            hashtagService.removeHashtag(hashtag.getId());
        }
        if (kvitter.getReplies().isEmpty() && kvitter.getRekvitts().isEmpty()) {
            kvitterRepo.deleteKvitterById(uuid);
        } else {
            kvitter.setMessage("Deleted...");
            kvitter.setIsActive(false);
            kvitterRepo.save(kvitter);
        }
    }

   
    public List<DetailedDtoInterface> getSearchedKvitters(String category, String searched, String token) {
        DetailedUserDto detailedUserDto = authService.getUserFromToken(token);
        User user = userRepo.findByEmailIgnoreCase(detailedUserDto.getEmail());
        Optional<User> optionalUser = userRepo.findByUserNameIgnoreCase(searched);
        User targetUser = userMapper.optionalToUser(optionalUser);
        List<DetailedDtoInterface> detailedInterfaceDtoList = new ArrayList<>();
        switch (category) {
            case "hashtag":
                detailedInterfaceDtoList = mapToInterfaceDtoList(kvitterRepo.searchByHashtag(searched, user.getId()), user);
                break;
            case "user":
                if (optionalUser.isPresent()) {
                    if (targetUser.getId() != user.getId()) {
                        detailedInterfaceDtoList = mapToInterfaceDtoList(kvitterRepo.findAllByTargetUser(targetUser.getId(), user.getId()), user);
                    } else {
                        detailedInterfaceDtoList = mapToInterfaceDtoList(kvitterRepo.findAllByLoggedInUser(user.getId()), user);
                    }
                }
                break;
            case "message":
                detailedInterfaceDtoList = mapToInterfaceDtoList(kvitterRepo.findAllByMessageContainsIgnoreCaseAndIsPrivate(searched, false), user);
                break;
            default:
                detailedInterfaceDtoList = new ArrayList<>();
                break;
        }
        detailedInterfaceDtoList.sort(Comparator.comparing(DetailedDtoInterface::getCreatedDateAndTime).reversed());
        return detailedInterfaceDtoList;
    }

    
    public List<DetailedDtoInterface> getFilteredKvitters(String filterOption, String userName, String token) {
        System.out.println("filterOption: " + filterOption);
        System.out.println("username: " + userName);
        DetailedUserDto detailedUserDto = authService.getUserFromToken(token);
        User user = userRepo.findByEmailIgnoreCase(detailedUserDto.getEmail());
        Optional<User> optionalUser = userRepo.findByUserNameIgnoreCase(userName);
        User targetUser = userMapper.optionalToUser(optionalUser);
        List<DetailedDtoInterface> detailedInterfaceDtoList = new ArrayList<>();
        switch (filterOption.toLowerCase()) {
            case "popular":
                System.out.println("Fetching kvitters for popular");
                detailedInterfaceDtoList = mapToInterfaceDtoList(kvitterRepo.findMostPopularKvitter(user.getId()), user);
                break;
            case "following":
                System.out.println("Fetching kvitters for following");
                detailedInterfaceDtoList = mapToInterfaceDtoList(kvitterRepo.findAllByUserFollows(user.getId()), user);
                break;
            case "latest":
                System.out.println("Fetching kvitters for latest, including private ones for followers of logged-in user");
                detailedInterfaceDtoList = mapToInterfaceDtoList(kvitterRepo.getLatestKvitters(user.getId()),user);
                break;
            case "user-info":
                System.out.println("Fetching kvitters for user-info page");
                if (optionalUser.isPresent()) {
                        System.out.println("Fetching kvitters for target user by logged-in user");
                        detailedInterfaceDtoList = mapToInterfaceDtoList(kvitterRepo.findAllByTargetUser(targetUser.getId(), user.getId()), user);
                }
                break;
            case "myactivity":
                System.out.println("Fetching kvitters for logged in user");
                detailedInterfaceDtoList = mapToInterfaceDtoList(kvitterRepo.findAllByLoggedInUser(user.getId()), user);
                break;
            default:
                detailedInterfaceDtoList = new ArrayList<>();
                break;
        }
        return detailedInterfaceDtoList;
    }

    private List<DetailedDtoInterface> mapToInterfaceDtoList(List<Kvitter> kvitterList, User user) {
        return kvitterList.stream().map(kvitter -> {
            DetailedKvitterDto dto = kvitterMapper.kvitterToDetailedKvitterDTO(kvitter);

            dto.setIsFollowing(user.getFollowing().contains(kvitter.getUser()));
            dto.setIsLiked(user.getLikes().contains(kvitter));

            List<DetailedReplyDto> replyDtos = mapReplies(kvitter.getReplies(), user);
            dto.setReplies(replyDtos);

            return dto;
        }).collect(Collectors.toList());
    }


    private List<DetailedReplyDto> mapReplies(List<Reply> replies, User user) {
        return replies.stream().map(reply -> {
            DetailedReplyDto replyDto = replyMapper.replyToDetailedReplyDTO(reply);
            replyDto.setIsFollowing(user.getFollowing().contains(reply.getUser()));

            if (reply.getReplies() != null && !reply.getReplies().isEmpty()) {
                List<DetailedReplyDto> nestedReplies = mapReplies(reply.getReplies(), user);
                replyDto.setReplies(nestedReplies);
            }

            return replyDto;
        }).toList();
    }


    private List<DetailedKvitterDto> mapToDetailedKvitterDtoList(List<Kvitter> kvitterList) {
        return kvitterList.stream().map(kvitterMapper::kvitterToDetailedKvitterDTO).collect(Collectors.toList());
    }


    public List<DetailedKvitterDto> getTenLatestKvitterThatIsNotPrivate() {
        return mapToDetailedKvitterDtoList(kvitterRepo.getTenLatestKvitterThatIsNotPrivate());
    }
}
