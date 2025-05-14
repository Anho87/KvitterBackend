package com.example.kvitter.repos;

import com.example.kvitter.entities.Rekvitt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface RekvittRepo extends JpaRepository<Rekvitt, UUID> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Rekvitt r WHERE r.id = :Id")
    void deleteRekvittByById(@Param("Id")UUID Id);
    
    
    @Query(value = "SELECT DISTINCT r.* FROM REKVITTS r LEFT JOIN User_following u ON r.user_id = u.following_id WHERE u.follower_id = :userId ORDER BY r.created_date_and_time DESC LIMIT 10", nativeQuery = true)
    List<Rekvitt> findAllRekvittsByUserFollows(@Param("userId") UUID userId);
    
   
    List<Rekvitt> findAllByUserId(UUID userId);
    
}
