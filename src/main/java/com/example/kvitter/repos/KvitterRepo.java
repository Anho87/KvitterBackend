package com.example.kvitter.repos;

import com.example.kvitter.entities.Kvitter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface KvitterRepo extends JpaRepository<Kvitter, UUID> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Kvitter k WHERE k.id = :Id")
    void deleteKvitterById(@Param("Id")UUID Id);
    
    
    
    @Query(value = "SELECT * FROM Kvitter WHERE is_private = false ORDER BY created_date_and_time DESC LIMIT 10", nativeQuery = true)
    List<Kvitter> getTenLatestKvitterThatIsNotPrivate();
    
    
    

    @Query(value = "SELECT DISTINCT k.* FROM Kvitter k LEFT JOIN User_following u ON k.user_id = u.follower_id WHERE (u.following_id = :userId) OR (k.is_private = false) OR (k.is_private = true AND k.user_id = :userId) ORDER BY k.created_date_and_time DESC LIMIT 10", nativeQuery = true)
    List<Kvitter> getDynamicKvitterList(@Param("userId") UUID userId);
    
    @Query(value = "SELECT DISTINCT k.* FROM Kvitter k LEFT JOIN User_following u ON k.user_id = u.follower_id WHERE u.follower_id = :targetId and u.following_id = :activeId and k.is_private = true OR k.user_id = :targetId and k.is_private = false ORDER BY k.created_date_and_time DESC", nativeQuery = true)
    List<Kvitter> findAllByTargetUser(@Param("targetId") UUID targetId, @Param("activeId") UUID activeId);
    
    @Query(value = "SELECT * FROM Kvitter WHERE user_id = :userId ORDER BY created_date_and_time DESC", nativeQuery = true)
    List<Kvitter> findAllByLoggedInUser(@Param("userId") UUID userId);

}
