package com.example.kvitter.Kvitter;


import com.example.kvitter.Hashtag.Hashtag;
import com.example.kvitter.User.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "\"kvitter\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Kvitter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    private String message;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    private LocalDateTime createdDateAndTime;

    @ManyToMany
    @JoinTable(
            name = "kvitter_hashtags",
            joinColumns = @JoinColumn(name = "kvitter_id"),
            inverseJoinColumns = @JoinColumn(name = "hashtag_id")
    )
    private List<Hashtag> hashtags = new ArrayList<>();

}
