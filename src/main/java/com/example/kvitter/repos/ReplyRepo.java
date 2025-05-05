package com.example.kvitter.repos;

import com.example.kvitter.entities.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface ReplyRepo extends JpaRepository<Reply, UUID> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Reply r WHERE r.id = :Id")
    void deleteReplyById(@Param("Id")UUID Id);
}
