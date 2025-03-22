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

    @Query(value = "SELECT * FROM Kvitter k WHERE k.is_private = false OR k.is_private = true AND k.user_id = :userId", nativeQuery = true)
    List<Kvitter> getAllKvitterThatIsPublic(@Param("userId") UUID userId);

}
