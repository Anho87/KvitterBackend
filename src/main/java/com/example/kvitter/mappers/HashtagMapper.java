package com.example.kvitter.mappers;

import com.example.kvitter.dtos.MiniHashtagDto;
import com.example.kvitter.entities.Hashtag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {KvitterMapper.class, UserMapper.class})
public interface HashtagMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "hashtag", target = "hashtag")
    MiniHashtagDto hashtagToMiniHashtagDto(Hashtag hashtag);
}
