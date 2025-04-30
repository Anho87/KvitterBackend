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
    void deleteKvitterById(@Param("Id") UUID Id);
    
    @Query(value = "SELECT * FROM Kvitter WHERE is_private = false AND is_active = true ORDER BY created_date_and_time DESC LIMIT 10", nativeQuery = true)
    List<Kvitter> getTenLatestKvitterThatIsNotPrivate();
    
    @Query(value = "SELECT DISTINCT k.* FROM Kvitter k LEFT JOIN User_following u ON k.user_id = u.follower_id WHERE (u.follower_id = :targetId and u.following_id = :activeId and k.is_private = true and k.is_active = true) OR (k.user_id = :targetId and k.is_private = false and k.is_active = true) ORDER BY k.created_date_and_time DESC", nativeQuery = true)
    List<Kvitter> findAllByTargetUser(@Param("targetId") UUID targetId, @Param("activeId") UUID activeId);

    @Query(value = "SELECT * FROM Kvitter WHERE (user_id = :userId and is_active = true) ORDER BY created_date_and_time DESC", nativeQuery = true)
    List<Kvitter> findAllByLoggedInUser(@Param("userId") UUID userId);

    @Query(value = "SELECT DISTINCT k.* FROM Kvitter k LEFT JOIN kvitter_hashtags kv on k.id = kv.kvitter_id left join Hashtag h on h.id = kv.hashtag_id where (h.hashtag = :searched and k.is_private = false) OR (h.hashtag = :searched and k.is_private = true and k.user_id = :activeId)", nativeQuery = true)
    List<Kvitter> searchByHashtag(@Param("searched") String searched, @Param("activeId") UUID activeId);

    List<Kvitter> findAllByMessageContainsIgnoreCaseAndIsPrivate(String searchWord, boolean isPrivate);
    
    @Query(value = "SELECT DISTINCT k.* FROM Kvitter k LEFT JOIN User_following u ON k.user_id = u.follower_id WHERE (u.following_id = :userId AND k.is_active = true) OR (k.is_private = false AND k.is_active = true)  OR (k.is_private = true AND k.is_active = true AND k.user_id = :userId) ORDER BY k.created_date_and_time DESC LIMIT 20", nativeQuery = true)
    List<Kvitter> getLatestKvitters(@Param("userId") UUID userId);
    
    @Query(value = "SELECT DISTINCT k.* FROM Kvitter k LEFT JOIN User_following u ON k.user_id = u.following_id WHERE u.follower_id = :userId AND k.is_active = true AND k.is_private = false ORDER BY k.created_date_and_time DESC LIMIT 10", nativeQuery = true)
    List<Kvitter> findAllByUserFollows(@Param("userId") UUID userId);
    
    @Query(value = "SELECT kvitter.* FROM kvitter LEFT JOIN user_following ON kvitter.user_id = user_following.follower_id INNER JOIN likes ON kvitter.id = likes.kvitter_id WHERE user_following.following_id = :userId OR (kvitter.is_private = false) OR (kvitter.is_private = true AND kvitter.user_id = :userId) GROUP BY kvitter.id ORDER BY COUNT(likes.user_id) DESC, kvitter.created_date_and_time DESC LIMIT 10", nativeQuery = true) 
    List<Kvitter> findMostPopularKvitter(@Param("userId") UUID userId);

}
