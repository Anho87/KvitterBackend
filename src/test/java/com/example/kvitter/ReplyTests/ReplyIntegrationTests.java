package com.example.kvitter.ReplyTests;

import com.example.kvitter.entities.Kvitter;
import com.example.kvitter.entities.Reply;
import com.example.kvitter.entities.User;
import com.example.kvitter.repos.KvitterRepo;
import com.example.kvitter.repos.ReplyRepo;
import com.example.kvitter.repos.UserRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class ReplyIntegrationTests {

    @Autowired
    private ReplyRepo replyRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private KvitterRepo kvitterRepo;

    @PersistenceContext
    private EntityManager entityManager;

    private User testUser;
    private Kvitter testKvitter;

    @BeforeEach
    void setUp() {
        replyRepo.deleteAll();
        kvitterRepo.deleteAll();
        userRepo.deleteAll();

        testUser = User.builder()
                .userName("testuser")
                .password("password")
                .email("testuser@example.com")
                .following(new java.util.ArrayList<>())
                .build();
        userRepo.saveAndFlush(testUser);

        testKvitter = Kvitter.builder()
                .message("Test Kvitter")
                .user(testUser)
                .createdDateAndTime(LocalDateTime.now())
                .isPrivate(false)
                .isActive(true)
                .build();
        kvitterRepo.saveAndFlush(testKvitter);
    }

    private Reply createReply(User user, Kvitter kvitter, String message) {
        Reply reply = Reply.builder()
                .user(user)
                .kvitter(kvitter)
                .message(message)
                .createdDateAndTime(LocalDateTime.now())
                .isActive(true)
                .build();
        return replyRepo.saveAndFlush(reply);
    }

    @Test
    @Transactional
    void testSaveAndFindReply() {
        Reply reply = createReply(testUser, testKvitter, "First reply");

        List<Reply> allReplies = replyRepo.findAll();
        assertThat(allReplies).isNotEmpty();
        assertThat(allReplies.get(0).getMessage()).isEqualTo("First reply");
        assertThat(allReplies.get(0).getMessage()).isNotEqualTo("Second reply");
    }

    @Test
    @Transactional
    void testDeleteReplyById() {
        Reply reply = createReply(testUser, testKvitter, "Reply to delete");

        replyRepo.deleteReplyById(reply.getId());
        entityManager.clear();

        boolean exists = replyRepo.findById(reply.getId()).isPresent();
        List<Reply> allReplies = replyRepo.findAll();
        assertThat(exists).isFalse();
        assertThat(allReplies).isEmpty();
    }

    @Test
    @Transactional
    void testNestedReplies() {
        User anotherUser = User.builder()
                .userName("anotherUser")
                .password("password")
                .email("followeduser@example.com")
                .following(new java.util.ArrayList<>())
                .build();
        userRepo.saveAndFlush(anotherUser);
                
        Reply parentReply = createReply(anotherUser, testKvitter, "Parent reply");

        Reply childReply = Reply.builder()
                .message("Child reply")
                .user(testUser)
                .kvitter(testKvitter)
                .parentReply(parentReply)
                .createdDateAndTime(LocalDateTime.now())
                .isActive(true)
                .build();
        replyRepo.saveAndFlush(childReply);

        entityManager.clear();

        Reply foundParent = replyRepo.findById(parentReply.getId()).orElseThrow();
        assertThat(foundParent.getReplies()).hasSize(1);
        assertThat(foundParent.getReplies().get(0).getMessage()).isEqualTo("Child reply");
        assertThat(foundParent.getUser().getId()).isEqualTo(anotherUser.getId());
        assertThat(foundParent.getReplies().get(0).getUser().getId()).isEqualTo(testUser.getId());
        
    }
}
