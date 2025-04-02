package com.example.kvitter.repos;

import com.example.kvitter.entities.Rekvitt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RekvittRepo extends JpaRepository<Rekvitt, UUID> {
}
