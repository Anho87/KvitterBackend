package com.example.kvitter.KvitterTests;

import com.example.kvitter.entities.Hashtag;
import com.example.kvitter.entities.Kvitter;
import com.example.kvitter.entities.User;
import com.example.kvitter.repos.HashtagRepo;
import com.example.kvitter.repos.KvitterRepo;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class KvitterIntegrationTests {

    @Autowired
    private KvitterRepo kvitterRepo;

    @Autowired
    private UserRepo userRepo; 
    
    @Autowired
    private HashtagRepo hashtagRepo;

    @PersistenceContext
    private EntityManager entityManager;

    private User activeUser;

    @BeforeEach
    void setUp() {
        kvitterRepo.deleteAll();
        userRepo.deleteAll();

        activeUser = User.builder()
                .userName("testuser")  
                .password("password")
                .email("testuser@example.com")
                .following(new ArrayList<>()) 
                .build();
        userRepo.saveAndFlush(activeUser);
    }

    private Kvitter createKvitter(String message, boolean isPrivate) {
        Kvitter kvitter = Kvitter.builder()
                .message(message)
                .user(activeUser)
                .createdDateAndTime(LocalDateTime.now())
                .isPrivate(isPrivate)
                .isActive(true)
                .build();
        return kvitterRepo.saveAndFlush(kvitter);
    }

    @Test
    @Transactional
    void testSaveAndFindKvitter() {
        Kvitter kvitter = createKvitter("Hello Kvitter!", false);

        List<Kvitter> allKvitter = kvitterRepo.findAll();
        assertThat(allKvitter).isNotEmpty();
        assertThat(allKvitter.get(0).getMessage()).isEqualTo("Hello Kvitter!");
    }

    @Test
    @Transactional
    void testDeleteKvitterById() {
        Kvitter kvitter = createKvitter("To delete", false);

        kvitterRepo.deleteKvitterById(kvitter.getId());
        entityManager.clear();

        boolean exists = kvitterRepo.findById(kvitter.getId()).isPresent();
        assertThat(exists).isFalse();
    }

    @Test
    @Transactional
    void testGetTenLatestKvitterThatIsNotPrivate() {
        for (int i = 0; i < 12; i++) {
            createKvitter("Public Kvitter " + i, false);
        }

        List<Kvitter> latestPublicKvitter = kvitterRepo.getTenLatestKvitterThatIsNotPrivate();
        assertThat(latestPublicKvitter).hasSize(10);
        for (int i = 0; i < 10; i++) {
            int expectedIndex = 11 - i;
            assertThat(latestPublicKvitter.get(i).getMessage()).isEqualTo("Public Kvitter " + expectedIndex);
        }
    }

    @Test
    @Transactional
    void testGetDynamicKvitterList() {
        User followedUser = User.builder()
                .userName("followeduser")
                .password("password")
                .email("followeduser@example.com")
                .following(new ArrayList<>())
                .build();
        userRepo.saveAndFlush(followedUser);

        followedUser.getFollowing().add(activeUser);
        userRepo.saveAndFlush(followedUser);

        Kvitter followedUserKvitter = Kvitter.builder()
                .message("Private Kvitter from Followed User")
                .user(followedUser)
                .createdDateAndTime(LocalDateTime.now())
                .isPrivate(true)
                .isActive(true)
                .build();
        kvitterRepo.saveAndFlush(followedUserKvitter);

        User unfollowedUser = User.builder()
                .userName("unfolloweduser")
                .password("password")
                .email("unfolloweduser@example.com")
                .following(new ArrayList<>())
                .build();
        userRepo.saveAndFlush(unfollowedUser);

        Kvitter unfollowedUserKvitter = Kvitter.builder()
                .message("Private Kvitter from Unfollowed User")
                .user(unfollowedUser)
                .createdDateAndTime(LocalDateTime.now())
                .isPrivate(true)
                .isActive(true)
                .build();
        kvitterRepo.saveAndFlush(unfollowedUserKvitter);

        Kvitter publicKvitter = createKvitter("Public Kvitter from TestUser", false);

        entityManager.clear();

        List<Kvitter> kvitterList = kvitterRepo.getDynamicKvitterList(activeUser.getId());

        assertThat(kvitterList).isNotEmpty();
        assertThat(kvitterList.stream().anyMatch(k -> k.getId().equals(publicKvitter.getId()))).isTrue();
        assertThat(kvitterList.stream().anyMatch(k -> k.getId().equals(followedUserKvitter.getId()))).isTrue();
        assertThat(kvitterList.stream().noneMatch(k -> k.getId().equals(unfollowedUserKvitter.getId()))).isTrue();
    }



    @Test
    @Transactional
    void testFindAllByTargetUserThatsFollowed() {
        User targetUser = User.builder()
                .userName("otherUser")
                .password("password")
                .email("otherUser@example.com")
                .following(new ArrayList<>())
                .build();
        userRepo.saveAndFlush(targetUser);

        targetUser.getFollowing().add(activeUser);
        userRepo.saveAndFlush(targetUser);

        Kvitter targetUserKvitterPrivate = Kvitter.builder()
                .message("Private Kvitter from targetUser")
                .user(targetUser)
                .createdDateAndTime(LocalDateTime.now())
                .isPrivate(true)
                .isActive(true)
                .build();
        kvitterRepo.saveAndFlush(targetUserKvitterPrivate);

        Kvitter targetUserKvitterNotPrivate = Kvitter.builder()
                .message("Public Kvitter from targetUser")
                .user(targetUser)
                .createdDateAndTime(LocalDateTime.now())
                .isPrivate(false)
                .isActive(true)
                .build();
        kvitterRepo.saveAndFlush(targetUserKvitterNotPrivate);

        Kvitter notTargetUserKvitter = Kvitter.builder()
                .message("Public Kvitter from targetUser")
                .user(activeUser)
                .createdDateAndTime(LocalDateTime.now())
                .isPrivate(false)
                .isActive(true)
                .build();
        kvitterRepo.saveAndFlush(notTargetUserKvitter);

        Kvitter notTargetUserKvitter2 = Kvitter.builder()
                .message("Public Kvitter from targetUser")
                .user(activeUser)
                .createdDateAndTime(LocalDateTime.now())
                .isPrivate(true)
                .isActive(true)
                .build();
        kvitterRepo.saveAndFlush(notTargetUserKvitter2);
        
        
        List<Kvitter> targetUserKvitter = kvitterRepo.findAllByTargetUser(targetUser.getId(), activeUser.getId());
        
        assertThat(targetUserKvitter).hasSize(2);
        assertThat(targetUserKvitter.stream().anyMatch(k -> k.getUser().getId().equals(targetUser.getId()))).isTrue();
        assertThat(targetUserKvitter.stream().noneMatch(k -> k.getUser().getId().equals(activeUser.getId()))).isTrue();
    }

    @Test
    @Transactional
    void testFindAllByTargetUserThatsNotFollowed() {
        User targetUser = User.builder()
                .userName("otherUser")
                .password("password")
                .email("otherUser@example.com")
                .following(new ArrayList<>())
                .build();
        userRepo.saveAndFlush(targetUser);
        

        Kvitter targetUserKvitterPrivate = Kvitter.builder()
                .message("Private Kvitter from targetUser")
                .user(targetUser)
                .createdDateAndTime(LocalDateTime.now())
                .isPrivate(true)
                .isActive(true)
                .build();
        kvitterRepo.saveAndFlush(targetUserKvitterPrivate);

        Kvitter targetUserKvitterNotPrivate = Kvitter.builder()
                .message("Public Kvitter from targetUser")
                .user(targetUser)
                .createdDateAndTime(LocalDateTime.now())
                .isPrivate(false)
                .isActive(true)
                .build();
        kvitterRepo.saveAndFlush(targetUserKvitterNotPrivate);

        Kvitter notTargetUserKvitter = Kvitter.builder()
                .message("Public Kvitter from targetUser")
                .user(activeUser)
                .createdDateAndTime(LocalDateTime.now())
                .isPrivate(false)
                .isActive(true)
                .build();
        kvitterRepo.saveAndFlush(notTargetUserKvitter);

        Kvitter notTargetUserKvitter2 = Kvitter.builder()
                .message("Public Kvitter from targetUser")
                .user(activeUser)
                .createdDateAndTime(LocalDateTime.now())
                .isPrivate(true)
                .isActive(true)
                .build();
        kvitterRepo.saveAndFlush(notTargetUserKvitter2);


        List<Kvitter> targetUserKvitter = kvitterRepo.findAllByTargetUser(targetUser.getId(), activeUser.getId());

        assertThat(targetUserKvitter).hasSize(1);
        assertThat(targetUserKvitter.stream().anyMatch(k -> k.getUser().getId().equals(targetUser.getId()))).isTrue();
        assertThat(targetUserKvitter.stream().noneMatch(k -> k.getUser().getId().equals(activeUser.getId()))).isTrue();
        assertThat(targetUserKvitter.stream().noneMatch(k -> k.getIsPrivate().equals(true))).isTrue();
    }

    @Test
    @Transactional
    void testFindAllByLoggedInUser() {
        User otherUser = User.builder()
                .userName("otherUser")
                .password("password")
                .email("otherUser@example.com")
                .following(new ArrayList<>())
                .build();
        userRepo.saveAndFlush(otherUser);


        Kvitter otherUserKvitterPrivate = Kvitter.builder()
                .message("Private Kvitter from otherUser")
                .user(otherUser)
                .createdDateAndTime(LocalDateTime.now())
                .isPrivate(true)
                .isActive(true)
                .build();
        kvitterRepo.saveAndFlush(otherUserKvitterPrivate);

        Kvitter otherUserKvitterNotPrivate = Kvitter.builder()
                .message("Public Kvitter from otherUser")
                .user(otherUser)
                .createdDateAndTime(LocalDateTime.now())
                .isPrivate(false)
                .isActive(true)
                .build();
        kvitterRepo.saveAndFlush(otherUserKvitterNotPrivate);

        Kvitter activeUserKvitter = Kvitter.builder()
                .message("Public Kvitter from activeUser")
                .user(activeUser)
                .createdDateAndTime(LocalDateTime.now())
                .isPrivate(false)
                .isActive(true)
                .build();
        kvitterRepo.saveAndFlush(activeUserKvitter);

        Kvitter activeUserKvitter2 = Kvitter.builder()
                .message("Public Kvitter from activeUser")
                .user(activeUser)
                .createdDateAndTime(LocalDateTime.now())
                .isPrivate(true)
                .isActive(true)
                .build();
        kvitterRepo.saveAndFlush(activeUserKvitter2);


        List<Kvitter> targetUserKvitter = kvitterRepo.findAllByLoggedInUser(activeUser.getId());

        assertThat(targetUserKvitter).hasSize(2);
        assertThat(targetUserKvitter.stream().anyMatch(k -> k.getUser().getId().equals(activeUser.getId()))).isTrue();
        assertThat(targetUserKvitter.stream().noneMatch(k -> k.getUser().getId().equals(otherUser.getId()))).isTrue();
    }


    @Test
    @Transactional
    void testSearchByHashtag() {
        Hashtag hashtag = Hashtag.builder()
                .hashtag("testtag")
                .build();
        hashtagRepo.saveAndFlush(hashtag);
        
        User testUser = User.builder()
                .userName("testuser2")
                .password("password")
                .email("testuser2@example.com")
                .following(new ArrayList<>())
                .build();
        userRepo.saveAndFlush(testUser);
        
        Kvitter publicKvitter = Kvitter.builder()
                .message("Public Kvitter with hashtag")
                .user(testUser)
                .createdDateAndTime(LocalDateTime.now())
                .isPrivate(false)
                .isActive(true)
                .hashtags(List.of(hashtag))
                .build();
        kvitterRepo.saveAndFlush(publicKvitter);
        
        Kvitter privateKvitter = Kvitter.builder()
                .message("Private Kvitter with hashtag")
                .user(testUser)
                .createdDateAndTime(LocalDateTime.now())
                .isPrivate(true)
                .isActive(true)
                .hashtags(List.of(hashtag))
                .build();
        kvitterRepo.saveAndFlush(privateKvitter);
        
        User otherUser = User.builder()
                .userName("otheruser")
                .password("password")
                .email("otheruser@example.com")
                .following(new ArrayList<>())
                .build();
        userRepo.saveAndFlush(otherUser);

        Kvitter privateKvitterOtherUser = Kvitter.builder()
                .message("Private Kvitter from other user")
                .user(otherUser)
                .createdDateAndTime(LocalDateTime.now())
                .isPrivate(true)
                .isActive(true)
                .hashtags(List.of(hashtag))
                .build();
        kvitterRepo.saveAndFlush(privateKvitterOtherUser);

        Kvitter notPrivateKvitterOtherUser = Kvitter.builder()
                .message("Not Private Kvitter from other user")
                .user(otherUser)
                .createdDateAndTime(LocalDateTime.now())
                .isPrivate(false)
                .isActive(true)
                .hashtags(List.of(hashtag))
                .build();
        kvitterRepo.saveAndFlush(notPrivateKvitterOtherUser);
        

        entityManager.clear();

       
        List<Kvitter> foundKvitter = kvitterRepo.searchByHashtag("testtag", testUser.getId());
        List<UUID> foundIds = foundKvitter.stream()
                .map(Kvitter::getId)
                .toList();

        assertThat(foundKvitter).isNotEmpty();
        assertThat(foundKvitter.stream().map(Kvitter::getId)).hasSize(3);
        assertThat(foundIds).contains(publicKvitter.getId());
        assertThat(foundIds).contains(privateKvitter.getId());
        assertThat(foundIds).contains(notPrivateKvitterOtherUser.getId());
        assertThat(foundIds).doesNotContain(privateKvitterOtherUser.getId());

    }


}

