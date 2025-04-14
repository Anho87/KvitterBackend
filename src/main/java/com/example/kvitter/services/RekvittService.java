package com.example.kvitter.services;

import com.example.kvitter.dtos.DetailedDtoInterface;
import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.entities.Kvitter;
import com.example.kvitter.entities.Rekvitt;
import com.example.kvitter.entities.User;
import com.example.kvitter.mappers.RekvittMapper;
import com.example.kvitter.mappers.UserMapper;
import com.example.kvitter.repos.KvitterRepo;
import com.example.kvitter.repos.RekvittRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RekvittService {

    private final RekvittRepo rekvittRepo;
    private final UserMapper userMapper;
    private final KvitterRepo kvitterRepo;
    private final RekvittMapper rekvittMapper;

    //TODO skriv test
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
    //TODO skriv test
    public List<DetailedDtoInterface> getRekvitts(String userName, DetailedUserDto detailedUserDto){
        System.out.println("Fetching all rekvitts");
        return mapToInterfaceDto(rekvittRepo.findAll());
    }
    
    private List<DetailedDtoInterface> mapToInterfaceDto(List<Rekvitt> rekvittList){
        return rekvittList.stream().map(rekvittMapper::rekvittToDetailedRekvittDto).collect(Collectors.toList());
    }
}
