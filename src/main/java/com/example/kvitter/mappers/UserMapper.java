package com.example.kvitter.mappers;

import com.example.kvitter.dtos.DetailedUserDto;
import com.example.kvitter.dtos.SignUpDto;
import com.example.kvitter.entities.User;
import org.mapstruct.Mapper;

import org.mapstruct.Mapping;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "kvitterList", target = "kvitterList")
    DetailedUserDto userToDetailedUserDTO(User user);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "kvitterList", target = "kvitterList")
    User detailedUserDTOToUser(DetailedUserDto detailedUserDto);
    
    @Mapping(target = "password", ignore = true)
    User signUpToUser(SignUpDto signUpDto);

    default User optionalToUser(Optional<User> optionalUser) {
        return optionalUser.orElse(null); 
    }
    default DetailedUserDto optionalToDetailedUserDto(Optional<User> optionalUser) {
        return optionalUser.map(this::userToDetailedUserDTO).orElse(null);
    }
}

