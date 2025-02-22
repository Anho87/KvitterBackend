package com.example.kvitter.repos;


import com.example.kvitter.entities.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HashtagRepo extends JpaRepository<Hashtag, UUID> {
}
