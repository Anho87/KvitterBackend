package com.example.kvitter.services;

import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.entities.Kvitter;
import com.example.kvitter.entities.Rekvitt;
import com.example.kvitter.entities.User;
import com.example.kvitter.mappers.UserMapper;
import com.example.kvitter.repos.KvitterRepo;
import com.example.kvitter.repos.RekvittRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class RekvittService {

    private final RekvittRepo rekvittRepo;
    private final UserMapper userMapper;
    private final KvitterRepo kvitterRepo;

    public void addRekvitt(String kvitterId, DetailedUserDto detailedUserDto) {
        User user = userMapper.detailedUserDTOToUser(detailedUserDto);
        UUID uuid = UUID.fromString(kvitterId);
        Kvitter kvitter = kvitterRepo.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Kvitter not found"));
        Rekvitt rekvitt = Rekvitt.builder()
                .originalKvitter(kvitter)
                .user(user)
                .build();
        rekvittRepo.save(rekvitt);
    }
}
