package com.example.kvitter.repos;

import com.example.kvitter.entities.Kvitter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface KvitterRepo extends JpaRepository<Kvitter, UUID> {
}
