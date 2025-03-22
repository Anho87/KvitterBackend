package com.example.kvitter.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetailedUserDto {
    private UUID id;
    private String email;
    private String userName;
    private List<MiniKvitterDto> kvitterList;
    private List<MiniLikeDto> likes;
    private List<MiniReplyDto> replies;
    private List<MiniUserDto> following = new ArrayList<>();
    private List<MiniUserDto> followers = new ArrayList<>();
    private List<MiniRekvittDto> rekvitts = new ArrayList<>();
    
    public DetailedUserDto(String userName, String email){
        this.userName = userName;
        this.email = email;
    }
    
}
