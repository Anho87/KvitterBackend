package com.example.kvitter.User;

import com.example.kvitter.Kvitter.MiniKvitterDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetailedUserDTO {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private List<MiniKvitterDTO> kvitterList;
    
}
