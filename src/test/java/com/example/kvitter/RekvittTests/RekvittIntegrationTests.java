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
    void testFindAllRekvittsByUserFollows() {
        User followedUser = User.builder()
                .userName("followeduser")
                .password("password")
                .email("followed@example.com")
                .following(new java.util.ArrayList<>())
                .build();
        userRepo.saveAndFlush(followedUser);

       
        testUser.getFollowing().add(followedUser);
        userRepo.saveAndFlush(testUser);

       
        Kvitter kvitter = Kvitter.builder()
                .message("Kvitter from followed user")
                .user(followedUser)
                .createdDateAndTime(LocalDateTime.now())
                .isPrivate(false)
                .isActive(true)
                .build();
        kvitterRepo.saveAndFlush(kvitter);

        Rekvitt rekvitt = Rekvitt.builder()
                .user(followedUser)
                .originalKvitter(kvitter)
                .createdDateAndTime(LocalDateTime.now())
                .build();
        rekvittRepo.saveAndFlush(rekvitt);

                User unfollowedNotTargetUser = User.builder()
                .userName("unfolloweduser")
                .password("password")
                .email("unfolloweduser@example.com")
                .following(new java.util.ArrayList<>())
                .build();
        userRepo.saveAndFlush(unfollowedNotTargetUser);
        

        Rekvitt unfollowedUserRekvitt = Rekvitt.builder()
                .user(unfollowedNotTargetUser)
                .originalKvitter(kvitter)
                .createdDateAndTime(LocalDateTime.now())
                .build();
        rekvittRepo.saveAndFlush(unfollowedUserRekvitt);

        entityManager.clear();

        List<Rekvitt> result = rekvittRepo.findAllRekvittsByUserFollows(testUser.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUser().getId()).isEqualTo(followedUser.getId());
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.stream().anyMatch(r -> r.getOriginalKvitter().getId().equals(kvitter.getId()))).isTrue();
        assertThat(result.stream().noneMatch(r -> r.getOriginalKvitter().getIsPrivate().equals(true))).isTrue();
        assertThat(result.stream().noneMatch(r -> r.getOriginalKvitter().getUser().getId().equals(unfollowedNotTargetUser.getId()))).isTrue();
    }

    @Test
    @Transactional
    void testFindAllRekvittsByUserFollows_whenUserFollowsNoOne_returnsEmptyList() {
    
        User otherUser = User.builder()
                .userName("otheruser")
                .password("password")
                .email("other@example.com")
                .following(new java.util.ArrayList<>())
                .build();
        userRepo.saveAndFlush(otherUser);

        Kvitter kvitter = Kvitter.builder()
                .message("Kvitter from other user")
                .user(otherUser)
                .createdDateAndTime(LocalDateTime.now())
                .isPrivate(false)
                .isActive(true)
                .build();
        kvitterRepo.saveAndFlush(kvitter);

        Rekvitt rekvitt = Rekvitt.builder()
                .user(otherUser)
                .originalKvitter(kvitter)
                .createdDateAndTime(LocalDateTime.now())
                .build();
        rekvittRepo.saveAndFlush(rekvitt);

        entityManager.clear();

        List<Rekvitt> result = rekvittRepo.findAllRekvittsByUserFollows(testUser.getId());

        assertThat(result).isEmpty();
    }


    @Test
    @Transactional
    void testFindAllByUserId() {
        Rekvitt rekvitt = createRekvitt(testUser, originalKvitter);

        entityManager.clear();

        List<Rekvitt> result = rekvittRepo.findAllByUserId(testUser.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUser().getId()).isEqualTo(testUser.getId());
    }

}

