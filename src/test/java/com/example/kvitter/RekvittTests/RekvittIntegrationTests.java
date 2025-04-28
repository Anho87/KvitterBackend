package com.example.kvitter.RekvittTests;

import com.example.kvitter.entities.Kvitter;
import com.example.kvitter.entities.Rekvitt;
import com.example.kvitter.entities.User;
import com.example.kvitter.repos.KvitterRepo;
import com.example.kvitter.repos.RekvittRepo;
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
public class RekvittIntegrationTests {

    @Autowired
    private RekvittRepo rekvittRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private KvitterRepo kvitterRepo;

    @PersistenceContext
    private EntityManager entityManager;

    private User testUser;
    private Kvitter originalKvitter;

    @BeforeEach
    void setUp() {
        rekvittRepo.deleteAll();
        kvitterRepo.deleteAll();
        userRepo.deleteAll();

        testUser = User.builder()
                .userName("testuser")
                .password("password")
                .email("testuser@example.com")
                .following(new java.util.ArrayList<>())
                .build();
        userRepo.saveAndFlush(testUser);

        originalKvitter = Kvitter.builder()
                .message("Original Kvitter")
                .user(testUser)
                .createdDateAndTime(LocalDateTime.now())
                .isPrivate(false)
                .isActive(true)
                .build();
        kvitterRepo.saveAndFlush(originalKvitter);
    }

    private Rekvitt createRekvitt(User user, Kvitter kvitter) {
        Rekvitt rekvitt = Rekvitt.builder()
                .user(user)
                .originalKvitter(kvitter)
                .createdDateAndTime(LocalDateTime.now())
                .build();
        return rekvittRepo.saveAndFlush(rekvitt);
    }

    @Test
    @Transactional
    void testSaveAndFindRekvitt() {
        Rekvitt rekvitt = createRekvitt(testUser, originalKvitter);

        List<Rekvitt> allRekvitts = rekvittRepo.findAll();
        assertThat(allRekvitts).isNotEmpty();
        assertThat(allRekvitts.get(0).getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    @Transactional
    void testDeleteRekvittById() {
        Rekvitt rekvitt = createRekvitt(testUser, originalKvitter);

        rekvittRepo.deleteRekvittByById(rekvitt.getId());
        entityManager.clear();

        boolean exists = rekvittRepo.findById(rekvitt.getId()).isPresent();
        assertThat(exists).isFalse();
    }

    @Test
    @Transactional
    void testGetRekvittsWhenFollowingUser() {
        User followedTargetUser = User.builder()
                .userName("followeduser")
                .password("password")
                .email("followeduser@example.com")
                .following(new java.util.ArrayList<>())
                .build();
        userRepo.saveAndFlush(followedTargetUser);

        testUser.getFollowing().add(followedTargetUser);
        userRepo.saveAndFlush(testUser);

        Kvitter publicKvitter = Kvitter.builder()
                .message("Public Kvitter from Followed User")
                .user(followedTargetUser)
                .createdDateAndTime(LocalDateTime.now())
                .isPrivate(false)
                .isActive(true)
                .build();
        Kvitter privateKvitter = Kvitter.builder()
                .message("Private Kvitter from Followed User")
                .user(followedTargetUser)
                .createdDateAndTime(LocalDateTime.now())
                .isPrivate(true)
                .isActive(true)
                .build();
        kvitterRepo.saveAndFlush(publicKvitter);
        kvitterRepo.saveAndFlush(privateKvitter);

        Rekvitt publicRekvitt = Rekvitt.builder()
                .user(followedTargetUser)
                .originalKvitter(publicKvitter)
                .createdDateAndTime(LocalDateTime.now())
                .build();
        rekvittRepo.saveAndFlush(publicRekvitt);

        User unfollowedNotTargetUser = User.builder()
                .userName("unfolloweduser")
                .password("password")
                .email("unfolloweduser@example.com")
                .following(new java.util.ArrayList<>())
                .build();
        userRepo.saveAndFlush(unfollowedNotTargetUser);

        Kvitter unfollowedUserKvitter = Kvitter.builder()
                .message("Private Kvitter from Unfollowed User")
                .user(unfollowedNotTargetUser)
                .createdDateAndTime(LocalDateTime.now())
                .isPrivate(false)
                .isActive(true)
                .build();
        kvitterRepo.saveAndFlush(unfollowedUserKvitter);

        Rekvitt unfollowedUserRekvitt = Rekvitt.builder()
                .user(unfollowedNotTargetUser)
                .originalKvitter(unfollowedUserKvitter)
                .createdDateAndTime(LocalDateTime.now())
                .build();
        rekvittRepo.saveAndFlush(unfollowedUserRekvitt);

        entityManager.clear();

        List<Rekvitt> rekvitts = rekvittRepo.getRekvittsByFollowedByAndUser(testUser.getId());

        assertThat(rekvitts).isNotEmpty();
        assertThat(rekvitts).hasSize(1);
        assertThat(rekvitts.stream().anyMatch(r -> r.getOriginalKvitter().getId().equals(publicKvitter.getId()))).isTrue();
        assertThat(rekvitts.stream().noneMatch(r -> r.getOriginalKvitter().getIsPrivate().equals(true))).isTrue();
        assertThat(rekvitts.stream().noneMatch(r -> r.getOriginalKvitter().getUser().getId().equals(unfollowedNotTargetUser.getId()))).isTrue();
    }


    @Test
    @Transactional
    void testGetRekvittsWhenNotFollowingUser() {
        User followedTargetUser = User.builder()
                .userName("followeduser")
                .password("password")
                .email("followeduser@example.com")
                .following(new java.util.ArrayList<>())
                .build();
        userRepo.saveAndFlush(followedTargetUser);
        
        Kvitter publicKvitter = Kvitter.builder()
                .message("Public Kvitter from Followed User")
                .user(followedTargetUser)
                .createdDateAndTime(LocalDateTime.now())
                .isPrivate(false)
                .isActive(true)
                .build();
        Kvitter privateKvitter = Kvitter.builder()
                .message("Private Kvitter from Followed User")
                .user(followedTargetUser)
                .createdDateAndTime(LocalDateTime.now())
                .isPrivate(true)
                .isActive(true)
                .build();
        kvitterRepo.saveAndFlush(publicKvitter);
        kvitterRepo.saveAndFlush(privateKvitter);

        Rekvitt publicRekvitt = Rekvitt.builder()
                .user(followedTargetUser)
                .originalKvitter(publicKvitter)
                .createdDateAndTime(LocalDateTime.now())
                .build();
        rekvittRepo.saveAndFlush(publicRekvitt);

        User unfollowedNotTargetUser = User.builder()
                .userName("unfolloweduser")
                .password("password")
                .email("unfolloweduser@example.com")
                .following(new java.util.ArrayList<>())
                .build();
        userRepo.saveAndFlush(unfollowedNotTargetUser);

        Kvitter unfollowedUserKvitter = Kvitter.builder()
                .message("Private Kvitter from Unfollowed User")
                .user(unfollowedNotTargetUser)
                .createdDateAndTime(LocalDateTime.now())
                .isPrivate(false)
                .isActive(true)
                .build();
        kvitterRepo.saveAndFlush(unfollowedUserKvitter);

        Rekvitt unfollowedUserRekvitt = Rekvitt.builder()
                .user(unfollowedNotTargetUser)
                .originalKvitter(unfollowedUserKvitter)
                .createdDateAndTime(LocalDateTime.now())
                .build();
        rekvittRepo.saveAndFlush(unfollowedUserRekvitt);

        entityManager.clear();

        List<Rekvitt> rekvitts = rekvittRepo.getRekvittsByFollowedByAndUser(testUser.getId());

        assertThat(rekvitts).isEmpty();
        assertThat(rekvitts).hasSize(0);
    }
    
}

