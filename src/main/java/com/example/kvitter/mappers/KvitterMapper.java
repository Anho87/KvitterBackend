package com.example.kvitter.mappers;

import com.example.kvitter.dtos.DetailedKvitterDto;
import com.example.kvitter.entities.Kvitter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface KvitterMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "message", target = "message")
    @Mapping(source = "user", target = "user")
    @Mapping(source = "createdDateAndTime", target = "createdDateAndTime")
    @Mapping(source = "hashtags", target = "hashtags")
    DetailedKvitterDto kvitterToDetailedKvitterDTO(Kvitter kvitter);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "message", target = "message")
    @Mapping(source = "user", target = "user")
    @Mapping(source = "createdDateAndTime", target = "createdDateAndTime")
    @Mapping(source = "hashtags", target = "hashtags")
    Kvitter detailedKvitterDTOtoKvitter(DetailedKvitterDto detailedKvitterDto);
}
