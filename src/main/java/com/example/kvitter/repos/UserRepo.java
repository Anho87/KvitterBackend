package com.example.kvitter.repos;

import com.example.kvitter.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {
    User findByEmailIgnoreCase(String email);
    Optional<User> findByUserNameIgnoreCase(String userName);
   
}
