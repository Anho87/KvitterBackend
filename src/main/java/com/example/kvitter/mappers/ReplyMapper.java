package com.example.kvitter.mappers;

import com.example.kvitter.dtos.DetailedReplyDto;
import com.example.kvitter.entities.Reply;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {KvitterMapper.class, UserMapper.class})
public interface ReplyMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "message", target = "message")
    @Mapping(source = "user", target = "user")
    @Mapping(source = "kvitter", target = "kvitter")
    @Mapping(source = "parentReply", target = "parentReply")
    @Mapping(source = "replies", target = "replies")
    DetailedReplyDto replyToDetailedReplyDTO(Reply reply);
}
