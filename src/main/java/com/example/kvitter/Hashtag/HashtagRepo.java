package com.example.kvitter.Hashtag;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HashtagRepo extends JpaRepository<Hashtag, UUID> {
}
