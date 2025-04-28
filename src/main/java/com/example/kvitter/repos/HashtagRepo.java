package com.example.kvitter.repos;


import com.example.kvitter.entities.Hashtag;
import lombok.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface HashtagRepo extends JpaRepository<Hashtag, UUID> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Hashtag h WHERE h.id = :Id")
    void deleteHashtagById(@Param("Id")UUID Id);
    
    
    @Query(value = "SELECT * FROM (SELECT DISTINCT ON (hashtag) * FROM hashtag ORDER BY hashtag, created_date_and_time DESC) AS sub ORDER BY created_date_and_time DESC LIMIT 5", nativeQuery = true)
    List<Hashtag> getFiveLastHashTags();
}
