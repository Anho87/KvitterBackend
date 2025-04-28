package com.example.kvitter.HashtagTests;
import com.example.kvitter.entities.Hashtag;
import com.example.kvitter.repos.HashtagRepo;
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
public class HashtagIntegrationTests {

    @Autowired
    private HashtagRepo hashtagRepo;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        hashtagRepo.deleteAll(); 
    }
    @Test
    @Transactional
    void testSaveAndFindHashtag() {
        Hashtag hashtag = new Hashtag();
        hashtag.setHashtag("testtag");

        hashtagRepo.save(hashtag); 

        List<Hashtag> all = hashtagRepo.findAll();
        assertThat(all).isNotEmpty();
        assertThat(all.get(0).getHashtag()).isEqualTo("testtag");
    }

    @Test
    @Transactional
    void testDeleteHashtagById() {
        Hashtag hashtag = new Hashtag();
        hashtag.setHashtag("todelete");

        hashtagRepo.saveAndFlush(hashtag);

        hashtagRepo.deleteHashtagById(hashtag.getId());

        entityManager.clear();

        boolean exists = hashtagRepo.findById(hashtag.getId()).isPresent();
        assertThat(exists).isFalse();
    }

    @Test
    @Transactional
    void testDeleteHashtagByIdRemovesOnlySpecifiedHashtag() {
        Hashtag hashtag1 = Hashtag.builder()
                .hashtag("tag1")
                .build();

        Hashtag hashtag2 = Hashtag.builder()
                .hashtag("tag2")
                .build();

        hashtagRepo.saveAndFlush(hashtag1);
        hashtagRepo.saveAndFlush(hashtag2);
        
        hashtagRepo.deleteHashtagById(hashtag1.getId());
        entityManager.clear();
    
        boolean hashtag1Exists = hashtagRepo.findById(hashtag1.getId()).isPresent();
        boolean hashtag2Exists = hashtagRepo.findById(hashtag2.getId()).isPresent();

        assertThat(hashtag1Exists).isFalse();
        assertThat(hashtag2Exists).isTrue();   
    }

    @Test
    @Transactional
    void testGetFiveLastHashTags() {
        for (int i = 0; i < 7; i++) {
            Hashtag h = Hashtag.builder()
                    .hashtag("tag" + i)
                    .createdDateAndTime(LocalDateTime.now().minusMinutes(7 - i))
                    .build();
            hashtagRepo.saveAndFlush(h);
        }
        Hashtag h = Hashtag.builder()
                .hashtag("tag" + 1)
                .createdDateAndTime(LocalDateTime.now())
                .build();
        hashtagRepo.saveAndFlush(h);

        List<Hashtag> lastFive = hashtagRepo.getFiveLastHashTags();

        assertThat(lastFive).hasSize(5);
        assertThat(lastFive.stream().map(Hashtag::getHashtag))
                .containsExactly("tag1", "tag6", "tag5", "tag4", "tag3");
    }


}
