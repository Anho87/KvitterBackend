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

    @Query(value = "SELECT * FROM Kvitter LIMIT 10", nativeQuery = true)
    List<Kvitter> getAnyTenKvitter();
}
