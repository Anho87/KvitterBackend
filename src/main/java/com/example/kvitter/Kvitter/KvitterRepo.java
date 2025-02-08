package com.example.kvitter.Kvitter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface KvitterRepo extends JpaRepository<Kvitter, UUID> {
}
