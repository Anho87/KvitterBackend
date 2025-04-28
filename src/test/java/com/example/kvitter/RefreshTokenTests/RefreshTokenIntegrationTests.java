package com.example.kvitter.RefreshTokenTests;

import com.example.kvitter.entities.RefreshToken;
import com.example.kvitter.entities.User;
import com.example.kvitter.repos.RefreshTokenRepo;
import com.example.kvitter.repos.UserRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class RefreshTokenIntegrationTests {

    @Autowired
    private RefreshTokenRepo refreshTokenRepo;

    @Autowired
    private UserRepo userRepo;

    @PersistenceContext
    private EntityManager entityManager;

    private User testUser;

    @BeforeEach
    void setUp() {
        refreshTokenRepo.deleteAll();
        userRepo.deleteAll();

        testUser = User.builder()
                .userName("testuser")
                .password("password")
                .email("testuser@example.com")
                .following(new java.util.ArrayList<>())
                .build();
        userRepo.saveAndFlush(testUser);
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken token = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(Instant.now().plusSeconds(3600))
                .revoked(false)
                .build();
        return refreshTokenRepo.saveAndFlush(token);
    }

    @Test
    @Transactional
    void testSaveAndFindByToken() {
        RefreshToken token = createRefreshToken(testUser);

        Optional<RefreshToken> foundToken = refreshTokenRepo.findByToken(token.getToken());

        assertThat(foundToken).isPresent();
        assertThat(foundToken.get().getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    @Transactional
    void testFindByUserId() {
        RefreshToken token = createRefreshToken(testUser);

        RefreshToken foundToken = refreshTokenRepo.findByUserId(testUser.getId());

        assertThat(foundToken).isNotNull();
        assertThat(foundToken.getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    @Transactional
    void testDeleteByUserId() {
        RefreshToken token = createRefreshToken(testUser);

        refreshTokenRepo.deleteByUserId(testUser.getId());
        entityManager.clear(); 

        Optional<RefreshToken> deletedToken = refreshTokenRepo.findByToken(token.getToken());

        assertThat(deletedToken).isEmpty();
    }
}
