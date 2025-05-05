package com.example.kvitter.UserTests;

import com.example.kvitter.entities.User;
import com.example.kvitter.repos.UserRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserIntegrationTests {

    @Autowired
    private UserRepo userRepo;

    @PersistenceContext
    private EntityManager entityManager;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepo.deleteAll();

        testUser = User.builder()
                .userName("testuser")
                .password("password")
                .email("testuser@example.com")
                .following(new java.util.ArrayList<>())
                .build();
        userRepo.saveAndFlush(testUser);
    }

    @Test
    @Transactional
    void testSaveAndFindById() {
        Optional<User> foundUser = userRepo.findById(testUser.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUserName()).isEqualTo("testuser");
        assertThat(foundUser.get().getPassword()).isEqualTo("password");
        assertThat(foundUser.get().getEmail()).isEqualTo("testuser@example.com");
    }

    @Test
    @Transactional
    void testFindByEmail() {
        User foundUser = userRepo.findByEmailIgnoreCase(testUser.getEmail());

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUserName()).isEqualTo("testuser");
        assertThat(foundUser.getPassword()).isEqualTo("password");
        assertThat(foundUser.getEmail()).isEqualTo("testuser@example.com");
    }

    @Test
    @Transactional
    void testFindByUserName() {
        Optional<User> foundUser = userRepo.findByUserNameIgnoreCase(testUser.getUserName());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUserName()).isEqualTo("testuser");
        assertThat(foundUser.get().getPassword()).isEqualTo("password");
        assertThat(foundUser.get().getEmail()).isEqualTo("testuser@example.com");
    }

    @Test
    @Transactional
    void testDeleteUser() {
        userRepo.deleteById(testUser.getId());
        userRepo.flush();
        entityManager.clear();

        boolean exists = userRepo.findById(testUser.getId()).isPresent();
        assertThat(exists).isFalse();
    }
}

