package com.example.kvitter.services;

import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.entities.Kvitter;
import com.example.kvitter.entities.Reply;
import com.example.kvitter.entities.User;
import com.example.kvitter.repos.KvitterRepo;
import com.example.kvitter.repos.ReplyRepo;
import com.example.kvitter.repos.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ReplyService {
    private final ReplyRepo replyRepo;
    private final UserRepo userRepo;
    private final KvitterRepo kvitterRepo;
    private final AuthService authService;


   
    public void addReply(String message, UUID kvitterId, UUID parentReplyId, String token) {
        DetailedUserDto detailedUserDto = authService.getUserFromToken(token);
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Stockholm"));
        User user = userRepo.findById(detailedUserDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Reply reply = Reply.builder()
                .message(message)
                .createdDateAndTime(now.toLocalDateTime())
                .user(user)
                .isActive(true)
                .build();

        if (parentReplyId != null) {
            Reply parentReply = replyRepo.findById(parentReplyId)
                    .orElseThrow(() -> new EntityNotFoundException("Parent reply not found"));
            reply.setParentReply(parentReply);
        } else {
            Kvitter kvitter = kvitterRepo.findById(kvitterId)
                    .orElseThrow(() -> new EntityNotFoundException("Kvitter not found"));
            reply.setKvitter(kvitter);
        }

        replyRepo.save(reply);
    }

    
    public void removeReply(String id) {
        UUID uuid = UUID.fromString(id);
        Reply reply = replyRepo.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Reply not found"));
        if (reply.getReplies().isEmpty()) {
            replyRepo.deleteReplyById(reply.getId());
        } else {
            reply.setMessage("Deleted...");
            reply.setIsActive(false);
            replyRepo.save(reply);
        }
    }
}
