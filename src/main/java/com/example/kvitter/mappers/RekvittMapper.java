package com.example.kvitter.mappers;

import com.example.kvitter.dtos.DetailedRekvittDto;
import com.example.kvitter.entities.Rekvitt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {KvitterMapper.class, UserMapper.class})
public interface RekvittMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "user", target = "user")
    @Mapping(source = "originalKvitter", target = "originalKvitter")
    DetailedRekvittDto rekvittToDetailedRekvittDto(Rekvitt rekvitt);
}
