package com.example.kvitter.services;

import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.entities.Hashtag;
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

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ReplyService {
    private final ReplyRepo replyRepo;
    private final UserRepo userRepo;
    private final KvitterRepo kvitterRepo;


    //TODO skriv test
    public void addReply(String message, UUID kvitterId, UUID parentReplyId, DetailedUserDto detailedUserDto) {
        LocalDateTime localDateTime = LocalDateTime.now();
        User user = userRepo.findById(detailedUserDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Reply reply = Reply.builder()
                .message(message)
                .createdDateAndTime(localDateTime)
                .user(user)
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

    //TODO skriv test
    public void removeReply(String id) {
        UUID uuid = UUID.fromString(id);
        Reply reply = replyRepo.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Reply not found"));
        if (reply.getReplies().isEmpty()) {
            replyRepo.deleteReplyById(reply.getId());
        } else {
            reply.setMessage("Deleted...");
            replyRepo.save(reply);
        }
    }
}
